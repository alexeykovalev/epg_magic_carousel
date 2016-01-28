package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
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
        final Point sectorReferencePoint = getSectorTopLeftCornerPos(sectorAnglePositionInRad);
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

    private Drawable getRayDrawable(Point rayStartPos) {
        final int startRayPosX = rayStartPos.x;
        final int startRayPosY = rayStartPos.y;
        final int rayHeight = rayDrawable.getIntrinsicHeight();
        rayDrawable.setBounds(
                startRayPosX, startRayPosY,
                startRayPosX + DEFAULT_RAY_WIDTH, startRayPosY + rayHeight
        );
        return rayDrawable;
    }

    private Point getSectorTopLeftCornerPos(double sectorAnglePosInRad) {
        final double sectorHalfAngleInRad = computationHelper.getCircleConfig().getAngularRestrictions().getSectorAngleInRad() / 2;

        final double topLeftSectorCornerAnglePosInRad = sectorAnglePosInRad + sectorHalfAngleInRad;
        final int innerRadius = computationHelper.getCircleConfig().getInnerRadius();

        final int refPointXPosInWheelCoordSystem = (int) (innerRadius * Math.cos(topLeftSectorCornerAnglePosInRad));
        final int refPointYPosInWheelCoordSystem = (int) (innerRadius * Math.sin(topLeftSectorCornerAnglePosInRad));
        final Point topLeftCornerPosInWheelCoordsSystem = new Point(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getCircleConfig().getCircleCenterRelToRecyclerView(),
                topLeftCornerPosInWheelCoordsSystem
        );
    }

    // TODO: 28.01.2016 make Point instance reusing
    @Deprecated
    private Point getSectorReferencePoint(View sectorView) {
        final WheelOfFortuneLayoutManager.LayoutParams childLp = (WheelOfFortuneLayoutManager.LayoutParams) sectorView.getLayoutParams();
        final double sectorAnglePositionInRad = childLp.anglePositionInRad;

        final int innerRadius = computationHelper.getCircleConfig().getInnerRadius();
        final int refPointXPosInWheelCoordSystem = (int) (innerRadius * Math.cos(sectorAnglePositionInRad));
        final int refPointYPosInWheelCoordSystem = (int) (innerRadius * Math.sin(sectorAnglePositionInRad));
        final Point sectorRefPointPosInWheelCoordSystem = new Point(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getCircleConfig().getCircleCenterRelToRecyclerView(),
                sectorRefPointPosInWheelCoordSystem
        );
    }
}
