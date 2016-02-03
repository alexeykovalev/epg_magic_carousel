package com.sss.magicwheel.manager.decor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.R;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 28.01.2016.
 */
public final class WheelSectorRayItemDecoration extends WheelBaseItemDecoration {

    private static final int DEFAULT_RAY_WIDTH = 700;
    private static final int DEFAULT_RAY_HEIGHT = 10;

    // TODO: 28.01.2016 ray with default draw does not fit exactly at sector's edge. This magic constant compensates this divergence.
    private static final int MAGIC_CONSTANT_FOR_RAY_ALIGNMENT = 12;

    private final Drawable rayDrawable;

    public WheelSectorRayItemDecoration(Context context) {
        super(context);
        this.rayDrawable = context.getResources().getDrawable(R.drawable.wheel_sector_ray_drawable);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView wheelView, RecyclerView.State state) {
        for (int i = 0; i < wheelView.getChildCount(); i++) {
            final View sectorView = wheelView.getChildAt(i);
            drawSectorTopEdgeRay(sectorView, canvas);
        }
    }

    private void drawSectorTopEdgeRay(View sectorView, Canvas canvas) {
        final double sectorAnglePositionInRad = getSectorAnglePositionInRad(sectorView);
        final PointF sectorReferencePoint = getSectorTopLeftCornerPos(sectorView);
        final float sectorAnglePositionInDegree = (float) WheelComputationHelper.radToDegree(sectorAnglePositionInRad);
        final float sectorHalfAngleInDegree = (float) WheelComputationHelper.radToDegree(
                computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2
        );

        final float rotateRayByAngle = sectorAnglePositionInDegree + sectorHalfAngleInDegree;

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
        final double topLeftSectorCornerAnglePosInRad = getSectorTopEdgeAnglePositionInRad(sectorView);
        final int innerRadius = computationHelper.getWheelConfig().getInnerRadius();

        final float refPointXPosInWheelCoordSystem = (float) (innerRadius * Math.cos(topLeftSectorCornerAnglePosInRad));
        final float refPointYPosInWheelCoordSystem = (float) (innerRadius * Math.sin(topLeftSectorCornerAnglePosInRad));
        final PointF topLeftCornerPosInWheelCoordsSystem = new PointF(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(topLeftCornerPosInWheelCoordsSystem);
    }
}
