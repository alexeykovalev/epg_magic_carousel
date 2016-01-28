package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.R;

/**
 * @author Alexey Kovalev
 * @since 28.01.2016.
 */
public class SectorRayItemDecoration extends RecyclerView.ItemDecoration {

    private static final int DEFAULT_RAY_WIDTH = 700;
    private static final int DEFAULT_RAY_HEIGHT = 10;

    private final WheelComputationHelper computationHelper;
    private final Drawable rayDrawable;

    public SectorRayItemDecoration(Context context) {
        this.computationHelper = WheelComputationHelper.getInstance();
        this.rayDrawable = context.getResources().getDrawable(R.drawable.wheel_sector_ray_drawable);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View sectorView = parent.getChildAt(i);
            final WheelOfFortuneLayoutManager.LayoutParams childLp = (WheelOfFortuneLayoutManager.LayoutParams) sectorView.getLayoutParams();
            final double sectorAnglePositionInRad = childLp.anglePositionInRad;

            drawSectorTopEdgeRay(sectorAnglePositionInRad, canvas);
        }
    }

    private void drawSectorTopEdgeRay(double sectorAnglePositionInRad, Canvas canvas) {
        final PointF sectorReferencePoint = getSectorTopLeftCornerPos(sectorAnglePositionInRad);
        final float sectorAnglePositionInDegree = (float) WheelComputationHelper.radToDegree(sectorAnglePositionInRad);
        final float sectorHalfAngleInDegree = (float) WheelComputationHelper.radToDegree(
                computationHelper.getCircleConfig().getAngularRestrictions().getSectorAngleInRad() / 2
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
        final float startRayPosY = rayStartPos.y;
        final int rayHeight = rayDrawable.getIntrinsicHeight();
        rayDrawable.setBounds(
                (int) startRayPosX,
                (int) startRayPosY,
                (int) (startRayPosX + DEFAULT_RAY_WIDTH),
                (int) (startRayPosY + rayHeight)
        );
        return rayDrawable;
    }

    private PointF getSectorTopLeftCornerPos(double sectorAnglePosInRad) {
        final double sectorHalfAngleInRad = computationHelper.getCircleConfig().getAngularRestrictions().getSectorAngleInRad() / 2;

        final double topLeftSectorCornerAnglePosInRad = sectorAnglePosInRad + sectorHalfAngleInRad;
        final int innerRadius = computationHelper.getCircleConfig().getInnerRadius();

        final float refPointXPosInWheelCoordSystem = (float) (innerRadius * Math.cos(topLeftSectorCornerAnglePosInRad));
        final float refPointYPosInWheelCoordSystem = (float) (innerRadius * Math.sin(topLeftSectorCornerAnglePosInRad));
        final PointF topLeftCornerPosInWheelCoordsSystem = new PointF(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getCircleConfig().getCircleCenterRelToRecyclerView(),
                topLeftCornerPosInWheelCoordsSystem
        );
    }

    // TODO: 28.01.2016 make Point instance reusing
    @Deprecated
    private PointF getSectorReferencePoint(View sectorView) {
        final WheelOfFortuneLayoutManager.LayoutParams childLp = (WheelOfFortuneLayoutManager.LayoutParams) sectorView.getLayoutParams();
        final double sectorAnglePositionInRad = childLp.anglePositionInRad;

        final int innerRadius = computationHelper.getCircleConfig().getInnerRadius();
        final float refPointXPosInWheelCoordSystem = (float) (innerRadius * Math.cos(sectorAnglePositionInRad));
        final float refPointYPosInWheelCoordSystem = (float) (innerRadius * Math.sin(sectorAnglePositionInRad));
        final PointF sectorRefPointPosInWheelCoordSystem = new PointF(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getCircleConfig().getCircleCenterRelToRecyclerView(),
                sectorRefPointPosInWheelCoordSystem
        );
    }
}
