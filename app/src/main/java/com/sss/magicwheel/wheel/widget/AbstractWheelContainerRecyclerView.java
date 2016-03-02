package com.sss.magicwheel.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.wheel.WheelAdapter;
import com.sss.magicwheel.wheel.misc.WheelComputationHelper;
import com.sss.magicwheel.wheel.entity.WheelConfig;
import com.sss.magicwheel.wheel.entity.WheelDataItem;
import com.sss.magicwheel.wheel.entity.WheelRotationDirection;
import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 02.02.2016.
 */
public abstract class AbstractWheelContainerRecyclerView extends RecyclerView {

    private static final String TAG = AbstractWheelContainerRecyclerView.class.getCanonicalName();

    protected final WheelComputationHelper computationHelper;
    protected final WheelConfig wheelConfig;

    /**
     * Lastly selected sector adapter position.
     * Store it in the field in order to don't fire sector selection
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
            notifyOnWheelRotationStateChanged();
            notifyOnSectorSelectedIfNeeded();
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
        final View sectorClosestToTopGapEdge = getLayoutManager().getChildClosestToLayoutEndEdge();
        final double sectorMiddleLineAnglePositionInRad =
                AbstractWheelLayoutManager.getChildLayoutParams(sectorClosestToTopGapEdge).anglePositionInRad;

        int newlySelectedSectorAdapterPos = RecyclerView.NO_POSITION;
        if (sectorMiddleLineAnglePositionInRad > gapTopEdgeAngleInRad) { // sector is above the top gap edge
            newlySelectedSectorAdapterPos = getChildAdapterPosition(sectorClosestToTopGapEdge) + 1;
        } else if (sectorMiddleLineAnglePositionInRad < gapTopEdgeAngleInRad) { // sector is below the top gap edge
            newlySelectedSectorAdapterPos = getChildAdapterPosition(sectorClosestToTopGapEdge);
        }
        return newlySelectedSectorAdapterPos;
    }

    private boolean isWheelInRotationStage() {
        return getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
    }

    public void addDataItemSelectionListener(WheelListener listener) {
        dataItemSelectionListeners.add(listener);
    }

    public void removeDataItemSelectionListener(WheelListener listener) {
        dataItemSelectionListeners.remove(listener);
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

    public void setIsCutGapAreaActivated(boolean isCutGapAreaActivated) {
        this.isCutGapAreaActivated = isCutGapAreaActivated;
    }

    private static Paint createGapRaysDrawingPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(8);
        return paint;
    }

}
