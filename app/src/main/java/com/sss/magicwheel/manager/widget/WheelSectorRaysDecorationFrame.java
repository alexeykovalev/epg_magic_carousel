package com.sss.magicwheel.manager.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.sss.magicwheel.R;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 12.02.2016.
 */
@Deprecated
public final class WheelSectorRaysDecorationFrame extends FrameLayout {

    private static final int DEFAULT_RAY_WIDTH = 700;
    private static final int DEFAULT_RAY_HEIGHT = 10;

    // TODO: 28.01.2016 ray with default draw does not fit exactly at sector's edge. This magic constant compensates this divergence.
    private static final int MAGIC_CONSTANT_FOR_RAY_ALIGNMENT = 12;


    private final Drawable rayDrawable;
    private final WheelComputationHelper computationHelper;
    private WheelContainerRecyclerView topWheelContainerView;
    private WheelContainerRecyclerView bottomWheelContainerView;

    /**
     * Baseline - is wheel middle line starting from wheel center which goes
     * directly to the screen's right side without any rotation angle.
     */
    private final double bottomWheelContainerRotationRelativeToBaseLineInDegree;

    private boolean isActivateRayDrawing;

    public WheelSectorRaysDecorationFrame(Context context) {
        this(context, null);
    }

    public WheelSectorRaysDecorationFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelSectorRaysDecorationFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.computationHelper = WheelComputationHelper.getInstance();
        this.bottomWheelContainerRotationRelativeToBaseLineInDegree = WheelComputationHelper.radToDegree(
                computationHelper.getWheelConfig().getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad()
        );
        this.rayDrawable = context.getResources().getDrawable(R.drawable.wheel_sector_ray_drawable);
    }

    public void setConfig(final WheelStartupAnimationHelper wheelStartupAnimationHelper,
                          final WheelContainerRecyclerView topWheelContainerView,
                          final WheelContainerRecyclerView bottomWheelContainer) {

        this.topWheelContainerView = topWheelContainerView;
        this.bottomWheelContainerView = bottomWheelContainer;

        wheelStartupAnimationHelper.addAnimationListener(new WheelStartupAnimationHelper.OnWheelStartupAnimationListener() {
            @Override
            public void onAnimationUpdate(WheelStartupAnimationHelper.WheelStartupAnimationStatus animationStatus) {
                if (animationStatus == WheelStartupAnimationHelper.WheelStartupAnimationStatus.Finished) {
                    isActivateRayDrawing = true;
                    invalidate();
                    topWheelContainerView.removeSectorRayItemDecorations();
                    bottomWheelContainer.removeSectorRayItemDecorations();
                    wheelStartupAnimationHelper.removeAnimationListener(this);
                }
            }
        });

        topWheelContainerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                invalidate();
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isActivateRayDrawing) {
            drawRaysForTopWheelContainer(canvas);
            drawRaysForBottomWheelContainer(canvas);
        }
        super.dispatchDraw(canvas);
    }

    private void drawRaysForTopWheelContainer(Canvas canvas) {
        if (topWheelContainerView != null) {
            for (int i = 0; i < topWheelContainerView.getChildCount(); i++) {
                final View sectorView = topWheelContainerView.getChildAt(i);
                drawRayForTopWheelSector(sectorView, canvas);
            }
        }
    }

    private void drawRaysForBottomWheelContainer(Canvas canvas) {
        if (bottomWheelContainerView != null) {
            for (int i = 0; i < bottomWheelContainerView.getChildCount(); i++) {
                final View sectorView = bottomWheelContainerView.getChildAt(i);
                drawRayForBottomWheelSector(sectorView, canvas);
            }
        }
    }

    private void drawRayForTopWheelSector(View sectorView, Canvas canvas) {
        final double sectorAnglePositionInRad = getSectorAnglePositionInRad(sectorView);
        final PointF sectorReferencePoint = getSectorTopLeftCornerPos(sectorView);
        final float sectorAnglePositionInDegree = (float) WheelComputationHelper.radToDegree(sectorAnglePositionInRad);
        final float sectorHalfAngleInDegree = (float) WheelComputationHelper.radToDegree(
                computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2
        );

        final float rotateRayByAngle = sectorAnglePositionInDegree + sectorHalfAngleInDegree;

        drawRayForPosition(canvas, sectorReferencePoint, rotateRayByAngle);
    }

    private void drawRayForBottomWheelSector(View sectorView, Canvas canvas) {
        final double sectorAnglePositionInRad = getSectorAnglePositionInRad(sectorView);
        final double sectorHalfAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2;
        final float sectorBottomEdgeAnglePositionInDegree = (float) WheelComputationHelper.radToDegree(sectorAnglePositionInRad - sectorHalfAngleInRad);

        if (sectorBottomEdgeAnglePositionInDegree <= bottomWheelContainerRotationRelativeToBaseLineInDegree) {
            final PointF sectorReferencePoint = getSectorBottomLeftCornerPos(sectorView);

            drawRayForPosition(canvas, sectorReferencePoint, sectorBottomEdgeAnglePositionInDegree);
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
                (int) (startRayPosX + DEFAULT_RAY_WIDTH),
                (int) (startRayPosY + rayHeight)
        );
        return rayDrawable;
    }

    private PointF getSectorTopLeftCornerPos(View sectorView) {
        final double topLeftCornerSectorAnglePosInRad = getSectorTopEdgeAnglePositionInRad(sectorView);
        return getRayPositionInRecyclerViewCoordsSystem(topLeftCornerSectorAnglePosInRad);
    }

    private PointF getSectorBottomLeftCornerPos(View sectorView) {
        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
        final double bottomLeftCornerSectorAnglePosInRad = getSectorTopEdgeAnglePositionInRad(sectorView) - sectorAngleInRad;
        return getRayPositionInRecyclerViewCoordsSystem(bottomLeftCornerSectorAnglePosInRad);
    }

    private PointF getRayPositionInRecyclerViewCoordsSystem(double rayReferenceAngleInRad) {
        final int innerRadius = computationHelper.getWheelConfig().getInnerRadius();
        final float refPointXPosInWheelCoordSystem = (float) (innerRadius * Math.cos(rayReferenceAngleInRad));
        final float refPointYPosInWheelCoordSystem = (float) (innerRadius * Math.sin(rayReferenceAngleInRad));
        final PointF topLeftCornerPosInWheelCoordsSystem = new PointF(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(topLeftCornerPosInWheelCoordsSystem);
    }

    private double getSectorAnglePositionInRad(View sectorView) {
        final AbstractWheelLayoutManager.LayoutParams childLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
        return childLp.anglePositionInRad;
    }

    private double getSectorTopEdgeAnglePositionInRad(View sectorView) {
        final double halfSectorAngle = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2;
        return getSectorAnglePositionInRad(sectorView) + halfSectorAngle;
    }

}
