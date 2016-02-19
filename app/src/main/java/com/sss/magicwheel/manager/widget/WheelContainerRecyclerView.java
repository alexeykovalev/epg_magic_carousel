package com.sss.magicwheel.manager.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.entity.WheelRotationDirection;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.decor.WheelSectorRayItemDecoration;
import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 02.02.2016.
 */
public final class WheelContainerRecyclerView extends RecyclerView {

    private static final String TAG = WheelContainerRecyclerView.class.getCanonicalName();

    private final WheelComputationHelper computationHelper;
    private final WheelConfig wheelConfig;

    private final Paint gapDrawingPaint;
    private final PointF gapTopRay;
    private final PointF gapBottomRay;

    private final Path gapPath;
    private boolean isCutGapAreaActivated;

    private final List<ItemDecoration> itemDecorators = new ArrayList<>();

    private class AutoAngleAdjustmentScrollListener extends OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            Log.e("TAG", "class [" + getLayoutManager().getClass() + "] scrolling newState [" + newState + "], " +
//                    "rotationDirection [" + rotationDirection + "]");

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                final AbstractWheelLayoutManager.LayoutParams closestToEndEdgeChildLp =
                        AbstractWheelLayoutManager.getChildLayoutParams(getLayoutManager().getChildClosestToLayoutEndEdge());

                final double sectorAngularPositionInRad = closestToEndEdgeChildLp.anglePositionInRad;
                final double sectorAngleTopEdgeInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorAngularPositionInRad);
                final double sectorAngleBottomEdgeInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorAngularPositionInRad);

                final double layoutEndAngleInRad = getLayoutManager().getLayoutEndAngleInRad();
                final boolean isInSectorTopPart = layoutEndAngleInRad >= sectorAngularPositionInRad
                        && layoutEndAngleInRad <= sectorAngleTopEdgeInRad;

                final double rotateByAngleInRad = isInSectorTopPart ?
                        (sectorAngleTopEdgeInRad - layoutEndAngleInRad) :
                        (sectorAngleBottomEdgeInRad - layoutEndAngleInRad);

                smoothRotateWheelByAngleInRad(rotateByAngleInRad, WheelRotationDirection.Clockwise);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    public WheelContainerRecyclerView(Context context) {
        this(context, null);
    }

    public WheelContainerRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelContainerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.computationHelper = WheelComputationHelper.getInstance();
        this.wheelConfig = computationHelper.getWheelConfig();

        this.gapDrawingPaint = createGapRaysDrawingPaint();
        this.gapTopRay = computeGapTopRayPosition();
        this.gapBottomRay = computeGapBottomRayPosition();

        this.gapPath = createGapClipPath();

        addOnScrollListener(new AutoAngleAdjustmentScrollListener());
    }

    @Deprecated
    public void smoothlySelectDataItem(WheelDataItem dataItemToSelect) {
//        super.smoothScrollToPosition(getVirtualPositionForDataItem(dataItemToSelect));
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }

    @Override
    public void addItemDecoration(ItemDecoration decor, int index) {
        super.addItemDecoration(decor, index);
        itemDecorators.add(decor);
    }

    @Override
    public void addItemDecoration(ItemDecoration decor) {
        super.addItemDecoration(decor);
        itemDecorators.add(decor);
    }

    @Override
    public void removeItemDecoration(ItemDecoration decor) {
        super.removeItemDecoration(decor);
        itemDecorators.remove(decor);
    }

    public void removeSectorRayItemDecorations() {
        // two for looping in order to eliminate ConcurrentModificationException
        final List<ItemDecoration> itemsToRemove = new ArrayList<>();
        for (ItemDecoration itemDecorator : itemDecorators) {
            if (itemDecorator instanceof WheelSectorRayItemDecoration) {
                itemsToRemove.add(itemDecorator);
            }
        }

        for (ItemDecoration itemDecoration : itemsToRemove) {
            removeItemDecoration(itemDecoration);
        }
    }

    public void handleTapOnSectorView(View sectorViewToSelect) {
        final String title = ((WheelBigWrapperView) sectorViewToSelect).getTitle();
        Log.e("TAG", "handleTapOnSectorView title [" + title + "]");

//        super.smoothScrollToPosition(getChildAdapterPosition(sectorViewToSelect));

//        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
//        smoothScrollByAngleInRad(sectorAngleInRad, WheelRotationDirection.Clockwise);
    }

    private void smoothRotateWheelByAngleInRad(double rotateByAngleInRad, WheelRotationDirection rotationDirection) {
        final double distanceToMove = rotationDirection.getDirectionSign()
                * computationHelper.fromWheelRotationAngleToTraveledDistance(rotateByAngleInRad);
        smoothScrollBy(0, (int) distanceToMove);
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
        drawHelperGapLines(canvas);
        cutGapArea(canvas);
        super.onDraw(canvas);
    }


    public void setIsCutGapAreaActivated(boolean isCutGapAreaActivated) {
        this.isCutGapAreaActivated = isCutGapAreaActivated;
    }

    private void cutGapArea(Canvas canvas) {
        if (isCutGapAreaActivated) {
            canvas.clipPath(gapPath, Region.Op.DIFFERENCE);
        }
    }

    private void drawHelperGapLines(Canvas canvas) {
        final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();
        canvas.drawLine(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y,
                gapTopRay.x, gapTopRay.y, gapDrawingPaint);

        canvas.drawLine(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y,
                gapBottomRay.x, gapBottomRay.y, gapDrawingPaint);
    }

    private static Paint createGapRaysDrawingPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(8);
        return paint;
    }

    private PointF computeGapTopRayPosition() {
        final PointF pos = CoordinatesHolder.ofPolar(2 * wheelConfig.getOuterRadius(),
                wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }

    private PointF computeGapBottomRayPosition() {
        final PointF pos = CoordinatesHolder.ofPolar(2 * wheelConfig.getOuterRadius(),
                wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }

    private Path createGapClipPath() {
        final Path res = new Path();
        final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();

        res.moveTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.lineTo(gapTopRay.x, gapTopRay.y);
        res.lineTo(gapBottomRay.x, gapBottomRay.y);
        res.lineTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.close();

        return res;
    }

}
