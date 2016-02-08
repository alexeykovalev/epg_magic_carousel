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

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 02.02.2016.
 */
public final class WheelContainerRecyclerView extends RecyclerView {


    private final WheelComputationHelper computationHelper;
    private final WheelConfig wheelConfig;

    private final Paint gapDrawingPaint;
    private final PointF gapTopRay;
    private final PointF gapBottomRay;

    private final RectF gapClipRectInRvCoords;
    private final Path gapPath;


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
    }

    public void smoothlySelectDataItem(WheelDataItem dataItemToSelect) {
        super.smoothScrollToPosition(getAdapter().getVirtualPositionForDataItem(dataItemToSelect));
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
