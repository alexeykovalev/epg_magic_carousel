package com.sss.magicwheel.manager.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
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
import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;

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

    private final RectF gapClipRectInRvCoords;
    private final Path gapPath;

    private class AutoAngleAdjustmentScrollListener extends OnScrollListener {

        private WheelRotationDirection rotationDirection = null;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && rotationDirection != null) {
//                final View sectorViewClosestToLayoutEndEdge = getLayoutManager().getChildClosestToLayoutEndEdge();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            rotationDirection = WheelRotationDirection.of(dy);
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

        this.gapClipRectInRvCoords = createGapClipRect();
        this.gapPath = createGapClipPath(gapClipRectInRvCoords);

        addOnScrollListener(new AutoAngleAdjustmentScrollListener());
    }

    @Deprecated
    public void smoothlySelectDataItem(WheelDataItem dataItemToSelect) {
//        super.smoothScrollToPosition(getVirtualPositionForDataItem(dataItemToSelect));
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }

    public void handleTapOnSectorView(View sectorViewToSelect) {
//        super.smoothScrollToPosition(getChildAdapterPosition(sectorViewToSelect));

//        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
//        smoothScrollByAngleInRad(sectorAngleInRad, WheelRotationDirection.Clockwise);
    }

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

    private static Paint createGapRaysDrawingPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(8);
        return paint;
    }

    private RectF createGapClipRect() {
        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                new RectF(
                        0,
                        150,
                        computationHelper.getWheelConfig().getOuterRadius() + 150,
                        -150
                )
        );
    }



    private Path createGapClipPath(RectF gapClipRect) {
        final Path res = new Path();

        /*res.moveTo(gapClipRect.left, gapClipRect.top);
        res.lineTo(gapClipRect.right, gapClipRect.top);
        res.lineTo(gapClipRect.right, gapClipRect.bottom);
        res.lineTo(gapClipRect.left, gapClipRect.bottom);
        res.lineTo(gapClipRect.left, gapClipRect.top);
        res.close();*/

        res.addRect(gapClipRect, Path.Direction.CW);

        return res;
    }


    @Override
    public void onDraw(Canvas canvas) {
        if (true) {

            final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();
            canvas.drawLine(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y,
                    gapTopRay.x, gapTopRay.y, gapDrawingPaint);

            canvas.drawLine(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y,
                    gapBottomRay.x, gapBottomRay.y, gapDrawingPaint);

            super.onDraw(canvas);

            return;
        }

        canvas.clipPath(gapPath);
        super.onDraw(canvas);
    }

}
