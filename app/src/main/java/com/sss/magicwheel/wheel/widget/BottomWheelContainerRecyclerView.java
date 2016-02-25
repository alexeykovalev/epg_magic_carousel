package com.sss.magicwheel.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.wheel.WheelComputationHelper;
import com.sss.magicwheel.wheel.entity.CoordinatesHolder;
import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 19.02.2016.
 */
public class BottomWheelContainerRecyclerView extends AbstractWheelContainerRecyclerView {

    private final Path gapPath;
    private final PointF bottomRayPosition;

    public interface OnBottomWheelSectorTapListener {
        void onRotateWheelByAngle(double rotationAngleInRad);
    }

    private OnBottomWheelSectorTapListener bottomWheelSectorTapListener;

    public BottomWheelContainerRecyclerView(Context context) {
        this(context, null);
    }

    public BottomWheelContainerRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomWheelContainerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bottomRayPosition = computeGapBottomRayPosition();
        gapPath = createGapClipPath();
    }

    public void setBottomWheelSectorTapListener(OnBottomWheelSectorTapListener bottomWheelSectorTapListener) {
        this.bottomWheelSectorTapListener = bottomWheelSectorTapListener;
    }

    @Override
    public void handleTapOnSectorView(View sectorViewToSelect) {
        bottomWheelSectorTapListener.onRotateWheelByAngle(computeWheelRotationForSector(sectorViewToSelect));
    }

    private double computeWheelRotationForSector(View sectorViewToSelect) {
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorViewToSelect);
        final double sectorViewBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);
        return getLayoutManager().getLayoutStartAngleInRad() - sectorViewBottomEdge;
    }

    @Override
    protected void doCutGapArea(Canvas canvas) {
        canvas.clipPath(gapPath);
    }

    @Override
    protected void drawGapLineRay(Canvas canvas) {
        final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();
        canvas.drawLine(
                circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y,
                bottomRayPosition.x, bottomRayPosition.y, gapRayDrawingPaint
        );
    }

    private Path createGapClipPath() {
        final Path res = new Path();
        final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();

        final int wheelDiameter = 2 * wheelConfig.getOuterRadius();

        res.moveTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.lineTo(bottomRayPosition.x, bottomRayPosition.y);
        res.lineTo(bottomRayPosition.x, wheelDiameter);
        res.lineTo(0, wheelDiameter);
        res.lineTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.close();

        return res;
    }

    private PointF computeGapBottomRayPosition() {
        final PointF pos = CoordinatesHolder.ofPolar(wheelConfig.getOuterRadius(),
                wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }

}
