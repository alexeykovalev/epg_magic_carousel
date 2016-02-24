package com.sss.magicwheel.coversflow;

import android.graphics.PointF;
import android.graphics.Rect;

import com.sss.magicwheel.wheel.WheelComputationHelper;
import com.sss.magicwheel.wheel.entity.CoordinatesHolder;

/**
 * @author Alexey Kovalev
 * @since 24.02.2016.
 */
public final class CoversFlowListMeasurements {

    public static final double COVER_ASPECT_RATIO = 1.5;

    private static CoversFlowListMeasurements instance;

    private final int maxCoverHeight;
    private final int coverDefaultHeight;
    private final Rect coverDefaultMargins;

    public static void initialize(WheelComputationHelper computationHelper) {
        instance = new CoversFlowListMeasurements(computationHelper);
    }

    public static CoversFlowListMeasurements getInstance() {
        return instance;
    }

    private final WheelComputationHelper computationHelper;

    private CoversFlowListMeasurements(WheelComputationHelper computationHelper) {
        this.computationHelper = computationHelper;
        instance = this;

        this.maxCoverHeight = (int) (getGapBottomRayPosition().y - getGapTopRayPosition().y);
        this.coverDefaultHeight = maxCoverHeight - 2 * 50;
        this.coverDefaultMargins = new Rect(15, 0, 0, 0);
    }


    public int getCoverMaxHeight() {
        return maxCoverHeight;
    }

    public int getCoverDefaultHeight() {
        return coverDefaultHeight;
    }

    public int getCoverDefaultWidth() {
        return (int) (COVER_ASPECT_RATIO * coverDefaultHeight);
    }

    public Rect getCoverDefaultMargins() {
        return coverDefaultMargins;
    }

    private PointF getGapTopRayPosition() {
        final PointF pos = CoordinatesHolder.ofPolar(computationHelper.getWheelConfig().getInnerRadius(),
                computationHelper.getWheelConfig().getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }

    private PointF getGapBottomRayPosition() {
        final PointF pos = CoordinatesHolder.ofPolar(computationHelper.getWheelConfig().getInnerRadius(),
                computationHelper.getWheelConfig().getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }
}
