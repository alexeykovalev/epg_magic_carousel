package com.sss.magicwheel.coversflow;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.ViewGroup;

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
    private final Rect coverDefaultMargins;
    private final ViewGroup.MarginLayoutParams initialCoverLayoutParams;

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
        final int coverDefaultHeight = maxCoverHeight - 2 * 50;
        final int coverDefaultWidth = (int) (COVER_ASPECT_RATIO * coverDefaultHeight);

        this.initialCoverLayoutParams = new ViewGroup.MarginLayoutParams(coverDefaultWidth, coverDefaultHeight);
        this.initialCoverLayoutParams.leftMargin = 15;

        this.coverDefaultMargins = new Rect(
                initialCoverLayoutParams.leftMargin,
                initialCoverLayoutParams.topMargin,
                initialCoverLayoutParams.rightMargin,
                initialCoverLayoutParams.bottomMargin
        );
    }

    public ViewGroup.MarginLayoutParams safeCopyInitialLayoutParams() {
        return new ViewGroup.MarginLayoutParams(initialCoverLayoutParams);
    }

    public int getCoverMaxHeight() {
        return maxCoverHeight;
    }

    public int getCoverDefaultHeight() {
        return initialCoverLayoutParams.height;
    }

    public int getCoverDefaultWidth() {
        return initialCoverLayoutParams.width;
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
