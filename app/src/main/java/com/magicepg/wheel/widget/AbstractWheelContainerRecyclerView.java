package com.magicepg.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.magicepg.wheel.WheelAdapter;
import com.magicepg.wheel.WheelListener;
import com.magicepg.wheel.entity.WheelDataItem;
import com.magicepg.wheel.entity.WheelRotationDirection;
import com.magicepg.wheel.layout.AbstractWheelLayoutManager;
import com.magicepg.wheel.WheelComputationHelper;
import com.magicepg.wheel.entity.WheelConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 02.02.2017
 */
public abstract class AbstractWheelContainerRecyclerView extends RecyclerView {

    /**
     * Value between 0 for no fling, and 1 for normal fling, or more for faster fling.
     */
    private static final float FLING_GESTURE_VELOCITY_SCALE_DOWN_FACTOR = 0.5f;

    protected final WheelComputationHelper computationHelper;
    protected final WheelConfig wheelConfig;

    /**
     * Lastly selected sector adapter position.
     * Store it on fields level in order to don't fire sector selection
     * notification twice.
     */
    private int lastlySelectedSectorAdapterPosition = RecyclerView.NO_POSITION;

    private WheelListener.WheelRotationState lastWheelRotationState = WheelListener.WheelRotationState.RotationStopped;

    protected final Paint gapRayDrawingPaint;
    private boolean isCutGapAreaActivated;

    private final List<WheelListener> dataItemSelectionListeners = new ArrayList<>();

    private class AutoAngleAdjustmentScrollListener extends OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (!isWheelInRotationStage()) {
                final double rotateByAngleInRad = computeAdjustmentRotationAngle();
                smoothRotateWheelByAngleInRad(rotateByAngleInRad, WheelRotationDirection.Clockwise);
            }
            notifyOnSectorSelectedIfNeeded();
            notifyOnWheelRotationStateChanged();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    public AbstractWheelContainerRecyclerView(Context context) {
        this(context, null);
    }

    public AbstractWheelContainerRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractWheelContainerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.computationHelper = WheelComputationHelper.getInstance();
        this.wheelConfig = computationHelper.getWheelConfig();

        this.gapRayDrawingPaint = createGapRaysDrawingPaint();

        addOnScrollListener(new AutoAngleAdjustmentScrollListener());
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private double computeAdjustmentRotationAngle() {
        final AbstractWheelLayoutManager.LayoutParams closestToEndEdgeChildLp =
                AbstractWheelLayoutManager.getChildLayoutParams(getLayoutManager().getChildClosestToLayoutEndEdge());

        final double sectorAngularPositionInRad = closestToEndEdgeChildLp.anglePositionInRad;
        final double sectorAngleTopEdgeInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorAngularPositionInRad);
        final double sectorAngleBottomEdgeInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorAngularPositionInRad);

        final double layoutEndAngleInRad = getLayoutManager().getLayoutEndAngleInRad();
        final boolean isInSectorTopPart = layoutEndAngleInRad >= sectorAngularPositionInRad
                && layoutEndAngleInRad <= sectorAngleTopEdgeInRad;

        return isInSectorTopPart ?
                (sectorAngleTopEdgeInRad - layoutEndAngleInRad) :
                (sectorAngleBottomEdgeInRad - layoutEndAngleInRad);
    }

    public abstract void handleTapOnSectorView(View sectorViewToSelect);

    protected abstract void drawGapLineRay(Canvas canvas);

    protected abstract void doCutGapArea(Canvas canvas);


    public void smoothRotateWheelByAngleInRad(double rotateByAngleInRad, WheelRotationDirection rotationDirection) {
        final double distanceToMove = rotationDirection.getDirectionSign()
                * computationHelper.fromWheelRotationAngleToTraveledDistance(rotateByAngleInRad);
        smoothScrollBy(0, (int) distanceToMove);
    }

    protected final void notifyOnSectorSelectedIfNeeded() {
        if (!isWheelInRotationStage()) {
            final int newlySelectedSectorAdapterPos = getNewlySelectedSectorAdapterPosition();
            if (lastlySelectedSectorAdapterPosition != newlySelectedSectorAdapterPos) {
                lastlySelectedSectorAdapterPosition = newlySelectedSectorAdapterPos;
                final WheelDataItem selectedSectorDataItem = getAdapter().getDataItemByPosition(newlySelectedSectorAdapterPos);
                for (WheelListener listener : dataItemSelectionListeners) {
                    listener.onDataItemSelected(selectedSectorDataItem);
                }
            }
        }
    }

    private void notifyOnWheelRotationStateChanged() {
        WheelListener.WheelRotationState newWheelRotationState = isWheelInRotationStage() ?
                WheelListener.WheelRotationState.InRotation : WheelListener.WheelRotationState.RotationStopped;

        if (lastWheelRotationState != newWheelRotationState) {
            lastWheelRotationState = newWheelRotationState;
            for (WheelListener listener : dataItemSelectionListeners) {
                listener.onWheelRotationStateChange(newWheelRotationState);
            }
        }
    }

    /**
     * Returns adapter position for currently selected sector.
     */
    private int getNewlySelectedSectorAdapterPosition() {
        final double gapTopEdgeAngleInRad = wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad();
        final View sectorClosestToTopGapEdge = getLayoutManager().getChildClosestToLayoutStartEdge();
        final double sectorMiddleLineAnglePositionInRad =
                AbstractWheelLayoutManager.getChildLayoutParams(sectorClosestToTopGapEdge).anglePositionInRad;

        int newlySelectedSectorAdapterPos = RecyclerView.NO_POSITION;
        if (sectorMiddleLineAnglePositionInRad > gapTopEdgeAngleInRad) { // sector is above the top gap edge
            newlySelectedSectorAdapterPos = getChildAdapterPosition(sectorClosestToTopGapEdge) - 1;
        } else if (sectorMiddleLineAnglePositionInRad < gapTopEdgeAngleInRad) { // sector is below the top gap edge
            newlySelectedSectorAdapterPos = getChildAdapterPosition(sectorClosestToTopGapEdge);
        }
        return newlySelectedSectorAdapterPos;
    }

    private boolean isWheelInRotationStage() {
        return getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
    }

    public void addWheelListener(WheelListener listener) {
        dataItemSelectionListeners.add(listener);
    }

    public void removeWheelListener(WheelListener listener) {
        dataItemSelectionListeners.remove(listener);
    }

    /**
     * Slows down usual fling gesture for RecyclerView.
     */
    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= FLING_GESTURE_VELOCITY_SCALE_DOWN_FACTOR;
        return super.fling(velocityX, velocityY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dataItemSelectionListeners.clear();
    }

    @Override
    public void scrollToPosition(int position) {
        throw new UnsupportedOperationException("Don't call this method directly.");
    }

    @Override
    public void smoothScrollToPosition(int position) {
        throw new UnsupportedOperationException("Don't call this method directly.");
    }

    @Override
    public AbstractWheelLayoutManager getLayoutManager() {
        return (AbstractWheelLayoutManager) super.getLayoutManager();
    }

    @Override
    public WheelAdapter getAdapter() {
        return (WheelAdapter) super.getAdapter();
    }

    @Override
    public void onDraw(Canvas canvas) {
//        drawGapLineRay(canvas);
        if (isCutGapAreaActivated) {
            doCutGapArea(canvas);
        }
        super.onDraw(canvas);
    }

    public final void setIsCutGapAreaActivated(boolean isCutGapAreaActivated) {
        this.isCutGapAreaActivated = isCutGapAreaActivated;
        invalidate();
    }

    private static Paint createGapRaysDrawingPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(8);
        return paint;
    }

}
