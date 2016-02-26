package com.sss.magicwheel.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.wheel.WheelComputationHelper;
import com.sss.magicwheel.wheel.entity.CoordinatesHolder;
import com.sss.magicwheel.wheel.entity.WheelRotationDirection;
import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 19.02.2016.
 */
public class TopWheelContainerRecyclerView extends AbstractWheelContainerRecyclerView {

    private final Path gapPath;
    private final PointF topRayPosition;

    private final AbstractWheelLayoutManager.WheelOnStartupAnimationListener animationFinishingListener =
            new AbstractWheelLayoutManager.WheelOnStartupAnimationListener() {
                @Override
                public void onAnimationUpdate(AbstractWheelLayoutManager.WheelStartupAnimationStatus animationStatus) {
                    if (animationStatus == AbstractWheelLayoutManager.WheelStartupAnimationStatus.Finished) {
                        notifyOnSectorSelectedIfNeeded();
                    }
                }
            };

    public TopWheelContainerRecyclerView(Context context) {
        this(context, null);
    }

    public TopWheelContainerRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopWheelContainerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        topRayPosition = computeGapTopRayPosition();
        gapPath = createGapClipPath();
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        getLayoutManager().removeWheelStartupAnimationListener(animationFinishingListener);
        getLayoutManager().addWheelStartupAnimationListener(animationFinishingListener);
    }

    @Override
    public void handleTapOnSectorView(View sectorViewToSelect) {
        smoothRotateWheelByAngleInRad(computeWheelRotationForTapOnSector(sectorViewToSelect), WheelRotationDirection.Clockwise);
    }

    private double computeWheelRotationForTapOnSector(View sectorViewToSelect) {
        AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorViewToSelect);
        final double sectorAngleTopEdgeInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad);
        return sectorAngleTopEdgeInRad - getLayoutManager().getLayoutEndAngleInRad();
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
                topRayPosition.x, topRayPosition.y, gapRayDrawingPaint
        );
    }

    private Path createGapClipPath() {
        final Path res = new Path();
        final PointF circleCenterRelToRecyclerView = wheelConfig.getCircleCenterRelToRecyclerView();

        res.moveTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.lineTo(topRayPosition.x, topRayPosition.y);
        res.lineTo(topRayPosition.x, 0);
        res.lineTo(0, 0);
        res.lineTo(circleCenterRelToRecyclerView.x, circleCenterRelToRecyclerView.y);
        res.close();

        return res;
    }

    private PointF computeGapTopRayPosition() {
        final PointF pos = CoordinatesHolder.ofPolar(wheelConfig.getOuterRadius(),
                wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }

}
