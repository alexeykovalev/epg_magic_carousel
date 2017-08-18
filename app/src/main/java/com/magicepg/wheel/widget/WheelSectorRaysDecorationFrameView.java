package com.magicepg.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.magicepg.R;
import com.magicepg.wheel.layout.AbstractWheelLayoutManager;
import com.magicepg.util.DimensionUtils;
import com.magicepg.wheel.WheelComputationHelper;
import com.magicepg.wheel.entity.WheelConfig;

/**
 * Renders rays drawables on sector edges.
 *
 * @author Alexey Kovalev
 * @since 12.02.2017
 */
public final class WheelSectorRaysDecorationFrameView extends FrameLayout {

    private static final int DEFAULT_RAY_WIDTH = 500;
    private static final int DEFAULT_RAY_HEIGHT = 10;

    // TODO: WheelOfFortune 28.01.2016 ray with default draw does not fit exactly at sector's edge. This magic constant compensates this divergence.
    private static final int MAGIC_CONSTANT_FOR_RAY_ALIGNMENT = 0;

    private static final int RAY_TRANSPARENCY = 170;
    private static final int RAY_LINE_THICKNESS_IN_DP = 2;

    private static final double ANGLE_PRECESSION_IN_RAD = WheelComputationHelper.degreeToRadian(0.5);

    private final WheelComputationHelper computationHelper;
    private final WheelConfig.AngularRestrictions wheelAngularRestrictions;

    private final Drawable rayDrawable;
    private final Paint rayPaint;
    private final int rayWidth;

    private AbstractWheelContainerRecyclerView topWheelContainerView;
    private AbstractWheelContainerRecyclerView bottomWheelContainerView;

    /**
     * Baseline - is wheel middle line starting from wheel center which goes
     * directly to the screen's right side without any rotation angle.
     */
    private final double bottomWheelContainerRotationRelativeToBaseLineInDegree;

    public WheelSectorRaysDecorationFrameView(Context context) {
        this(context, null);
    }

    public WheelSectorRaysDecorationFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelSectorRaysDecorationFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.computationHelper = WheelComputationHelper.getInstance();
        this.wheelAngularRestrictions = computationHelper.getWheelConfig().getAngularRestrictions();
        this.bottomWheelContainerRotationRelativeToBaseLineInDegree = WheelComputationHelper.radToDegree(
                wheelAngularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad()
        );
        this.rayDrawable = context.getResources().getDrawable(R.drawable.wheel_sector_ray_drawable);

        this.rayWidth = (int) getRayWidth();
        this.rayPaint = createGapRaysDrawingPaint();
    }

    private static Paint createGapRaysDrawingPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(DimensionUtils.dpToPixels(RAY_LINE_THICKNESS_IN_DP));
        paint.setAlpha(RAY_TRANSPARENCY);
        return paint;
    }

    public void setWheelContainers(final AbstractWheelContainerRecyclerView topWheelContainerView,
                                   final AbstractWheelContainerRecyclerView bottomWheelContainer) {

        this.topWheelContainerView = topWheelContainerView;
        this.bottomWheelContainerView = bottomWheelContainer;

        topWheelContainerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                invalidate();
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (topWheelContainerView != null && bottomWheelContainerView != null) {
            drawRaysForTopWheelContainer(canvas);
            drawRaysForBottomWheelContainer(canvas);
        }
        super.dispatchDraw(canvas);
    }

    private void drawRaysForTopWheelContainer(Canvas canvas) {
        for (int i = 0; i < topWheelContainerView.getChildCount(); i++) {
            final View sectorView = topWheelContainerView.getChildAt(i);
            drawRayForTopWheelSector(sectorView, canvas);
        }
    }

    private void drawRaysForBottomWheelContainer(Canvas canvas) {
        for (int i = 0; i < bottomWheelContainerView.getChildCount(); i++) {
            final View sectorView = bottomWheelContainerView.getChildAt(i);
            drawRayForBottomWheelSector(sectorView, canvas);
        }
    }

    private void drawRayForTopWheelSector(View sectorView, Canvas canvas) {
        final double sectorAnglePositionInRad = getSectorAnglePositionInRad(sectorView);
        final double sectorHalfAngleInRad = wheelAngularRestrictions.getSectorAngleInRad() / 2;
        final double sectorBottomEdgeAnglePositionInRad = sectorAnglePositionInRad - sectorHalfAngleInRad;

        if (sectorBottomEdgeAnglePositionInRad >=
                wheelAngularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad() - ANGLE_PRECESSION_IN_RAD) {

            final PointF rayStartPoint = getSectorBottomLeftCornerPos(sectorView);

            final double sectorAngleInRad = wheelAngularRestrictions.getSectorAngleInRad();
            final double bottomLeftCornerSectorAnglePosInRad = getSectorTopEdgeAnglePositionInRad(sectorView) - sectorAngleInRad;

            final PointF rayEndPoint = getRayPositionInRecyclerViewCoordsSystem(
                    bottomLeftCornerSectorAnglePosInRad,
                    computationHelper.getWheelConfig().getInnerRadius() + rayWidth
            );

            canvas.drawLine(rayStartPoint.x, rayStartPoint.y, rayEndPoint.x, rayEndPoint.y, rayPaint);
        }
    }

    private void drawRayForBottomWheelSector(View sectorView, Canvas canvas) {
        final PointF rayStartPoint = getSectorTopLeftCornerPos(sectorView);
        final double sectorTopEdgeAnglePositionInRad = getSectorTopEdgeAnglePositionInRad(sectorView);
        if (sectorTopEdgeAnglePositionInRad <=
                wheelAngularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad() + ANGLE_PRECESSION_IN_RAD) {
            final PointF rayEndPoint = getRayPositionInRecyclerViewCoordsSystem(
                    sectorTopEdgeAnglePositionInRad,
                    computationHelper.getWheelConfig().getInnerRadius() + rayWidth
            );
            canvas.drawLine(rayStartPoint.x, rayStartPoint.y, rayEndPoint.x, rayEndPoint.y, rayPaint);
        }
    }

    private void drawRayForPosition(Canvas canvas, PointF sectorReferencePoint, float rotateRayByAngle) {
        canvas.save();
        // negative rotation angle due to anticlockwise rotation
        canvas.rotate(-rotateRayByAngle, sectorReferencePoint.x, sectorReferencePoint.y);
        Drawable topEdgeRayDrawable = getRayDrawable(sectorReferencePoint);
        topEdgeRayDrawable.draw(canvas);
        canvas.restore();
    }

    private Drawable getRayDrawable(PointF rayStartPos) {
        final float startRayPosX = rayStartPos.x;
        final float startRayPosY = rayStartPos.y - MAGIC_CONSTANT_FOR_RAY_ALIGNMENT;
        final int rayHeight = rayDrawable.getIntrinsicHeight();
        rayDrawable.setBounds(
                (int) startRayPosX,
                (int) startRayPosY,
                (int) (startRayPosX + getRayWidth()),
                (int) (startRayPosY + rayHeight)
        );
        return rayDrawable;
    }

    private float getRayWidth() {
        final double sectorWidth = computationHelper.getWheelConfig().getOuterRadius() - computationHelper.getWheelConfig().getInnerRadius();
        return (float) (1.5 * sectorWidth);
    }

    private PointF getSectorTopLeftCornerPos(View sectorView) {
        final double topLeftCornerSectorAnglePosInRad = getSectorTopEdgeAnglePositionInRad(sectorView);
        return getRayPositionInRecyclerViewCoordsSystem(topLeftCornerSectorAnglePosInRad, computationHelper.getWheelConfig().getInnerRadius());
    }

    private PointF getSectorBottomLeftCornerPos(View sectorView) {
        final double sectorAngleInRad = wheelAngularRestrictions.getSectorAngleInRad();
        final double bottomLeftCornerSectorAnglePosInRad = getSectorTopEdgeAnglePositionInRad(sectorView) - sectorAngleInRad;
        return getRayPositionInRecyclerViewCoordsSystem(bottomLeftCornerSectorAnglePosInRad, computationHelper.getWheelConfig().getInnerRadius());
    }

    private PointF getRayPositionInRecyclerViewCoordsSystem(double rayReferenceAngleInRad, int radius) {
        final float refPointXPosInWheelCoordSystem = (float) (radius * Math.cos(rayReferenceAngleInRad));
        final float refPointYPosInWheelCoordSystem = (float) (radius * Math.sin(rayReferenceAngleInRad));
        final PointF topLeftCornerPosInWheelCoordsSystem = new PointF(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(topLeftCornerPosInWheelCoordsSystem);
    }

    private double getSectorAnglePositionInRad(View sectorView) {
        final AbstractWheelLayoutManager.LayoutParams childLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
        return childLp.anglePositionInRad;
    }

    private double getSectorTopEdgeAnglePositionInRad(View sectorView) {
        return computationHelper.getSectorAngleTopEdgeInRad(getSectorAnglePositionInRad(sectorView));
    }

}
