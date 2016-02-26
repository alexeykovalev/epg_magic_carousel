package com.sss.magicwheel.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.wheel.WheelAdapter;
import com.sss.magicwheel.wheel.WheelComputationHelper;
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

    protected final Paint gapRayDrawingPaint;
    private boolean isCutGapAreaActivated;

    private final List<OnDataItemSelectionListener> dataItemSelectionListeners = new ArrayList<>();

    private class AutoAngleAdjustmentScrollListener extends OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (!isWheelInRotationStage()) {
                final double rotateByAngleInRad = computeAdjustmentRotationAngle();
                smoothRotateWheelByAngleInRad(rotateByAngleInRad, WheelRotationDirection.Clockwise);
                notifyOnSectorSelectedIfNeeded();
            }
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

    private void notifyOnSectorSelectedIfNeeded() {
        if (!isWheelInRotationStage()) {
            final int newlySelectedSectorAdapterPos = getNewlySelectedSectorAdapterPosition();
            if (lastlySelectedSectorAdapterPosition != newlySelectedSectorAdapterPos) {
                lastlySelectedSectorAdapterPosition = newlySelectedSectorAdapterPos;
                final WheelDataItem selectedSectorDataItem = getAdapter().getDataItemByPosition(newlySelectedSectorAdapterPos);
                for (OnDataItemSelectionListener listener : dataItemSelectionListeners) {
                    listener.onDataItemSelected(selectedSectorDataItem);
                }
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

    public void addDataItemSelectionListener(OnDataItemSelectionListener listener) {
        dataItemSelectionListeners.add(listener);
    }

    public void removeDataItemSelectionListener(OnDataItemSelectionListener listener) {
        dataItemSelectionListeners.remove(listener);
    }

    @Deprecated
    public void smoothScrollByAngleInRad(double absAngleInRad, WheelRotationDirection rotationDirection) {
        // TODO: 09.02.2016 ensure absAngleInRad is positive
        View sectorViewClosestToLayoutEndEdge = getLayoutManager().getChildClosestToLayoutEndEdge();
        final int referenceSectorViewVirtualAdapterPosition = getChildAdapterPosition(sectorViewClosestToLayoutEndEdge);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();

        final int positionIncrement = rotationDirection == WheelRotationDirection.Clockwise ? -1 : +1;

        int virtualPositionToScroll = referenceSectorViewVirtualAdapterPosition;
        double tmpAngleInRad = absAngleInRad;
        while (tmpAngleInRad > 0) {
            tmpAngleInRad -= sectorAngleInRad;
            virtualPositionToScroll += positionIncrement;
        }

        super.smoothScrollToPosition(virtualPositionToScroll);
    }

    @Deprecated
    private int getVirtualPositionForDataItem(WheelDataItem dataItemToSelect) {
        final int dataItemToSelectRealPosition = getRealPositionForDataItem(dataItemToSelect);
        final int firstChildVirtualAdapterPosition = getChildAdapterPosition(getChildAt(0));
        final int firstChildRealAdapterPosition = getAdapter().toRealPosition(firstChildVirtualAdapterPosition);

        final int positionDelta = dataItemToSelectRealPosition - firstChildRealAdapterPosition;
        return firstChildVirtualAdapterPosition + positionDelta;

//        final int realItemsCount = getAdapter().getRealItemCount();
//        final int dataItemToSelectRealPosition = getRealPositionForDataItem(dataItemToSelect);
//        final int firstChildVirtualAdapterPosition = getChildAdapterPosition(getChildAt(0));
//
//        final int chunksAmount = (firstChildVirtualAdapterPosition - WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT) / realItemsCount;
//
//        return
    }

    // TODO: 08.02.2016 Guava's find method has to be here
    @Deprecated
    private int getRealPositionForDataItem(WheelDataItem dataItemToFind) {
        final List<WheelDataItem> dataItems = getAdapter().getData();
        int res = 0;
        for (WheelDataItem dataItem : dataItems) {
            if (dataItem.equals(dataItemToFind)) {
                break;
            }
            res++;
        }
        return res;
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
        drawGapLineRay(canvas);
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
