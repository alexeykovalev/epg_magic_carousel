package com.sss.magicwheel.manager.decor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 01.02.2016.
 */
public final class WheelFrameItemDecoration extends WheelBaseItemDecoration {

    private static final int FRAME_LINE_COLOR = Color.GRAY;
    private static final int FRAME_LINE_THICKNESS = 35;
    private static final int FRAME_LINE_TRANSPARENCY = 170;

    private final Paint framePaint;
    private final RectF innerCircleEmbracingSquare;
    private final RectF outerCircleEmbracingSquare;

    private final int wheelTopEdgeAngleInDegree;
    private final int wheelFrameSweepAngleInDegree;

    public WheelFrameItemDecoration(Context context) {
        super(context);
        this.framePaint = createFramePaint();
        this.innerCircleEmbracingSquare = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getInnerCircleEmbracingSquareInCircleCoordsSystem()
        );
        this.outerCircleEmbracingSquare = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getOuterCircleEmbracingSquareInCircleCoordsSystem()
        );

        this.wheelTopEdgeAngleInDegree =
                (int) WheelComputationHelper.radToDegree(computationHelper.getWheelLayoutStartAngleInRad());

        final int wheelBottomEdgeAngleInDegree = (int) WheelComputationHelper.radToDegree(
                computationHelper.getWheelConfig().getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad()
        );
        this.wheelFrameSweepAngleInDegree = wheelTopEdgeAngleInDegree - wheelBottomEdgeAngleInDegree;
    }

    private static Paint createFramePaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(FRAME_LINE_COLOR);
        paint.setStrokeWidth(FRAME_LINE_THICKNESS);
        paint.setAlpha(FRAME_LINE_TRANSPARENCY);
        return paint;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        drawInnerFrameLine(canvas);
        drawOuterFrameLine(canvas);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView wheelView, RecyclerView.State state) {
    }

    private void drawInnerFrameLine(Canvas canvas) {
        canvas.drawArc(innerCircleEmbracingSquare, -wheelTopEdgeAngleInDegree, wheelFrameSweepAngleInDegree, false, framePaint);
    }

    private void drawOuterFrameLine(Canvas canvas) {
        canvas.drawArc(outerCircleEmbracingSquare, -wheelTopEdgeAngleInDegree, wheelFrameSweepAngleInDegree, false, framePaint);
    }
}
