package com.magicepg.coversflow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.magicepg.wheel.WheelComputationHelper;
import entity.CoordinatesHolder;

/**
 * Store measurements for horizontal stripe view (covers flow).
 *
 * @author Alexey Kovalev
 * @since 24.02.2017
 */
public final class CoversFlowComputationHelper {

    public static final int ALPHA_ANIMATION_DURATION = 300;

    public static final double COVER_ASPECT_RATIO = 1.7;
    private static final float MAX_HEIGHT_DEFAULT_HEIGHT_COEFFICIENT = 1.3f;

    private static final int INNER_TOP_PADDING = 15;
    private static final int INNER_BOTTOM_PADDING = 15;

    private static CoversFlowComputationHelper instance;

    private final int maxCoverHeight;
    private final Rect coverDefaultMargins;
    private final ViewGroup.MarginLayoutParams initialCoverLayoutParams;

    private final float resizingEdgePosition;

    private final int leftOffset;
    private final int rightOffset;

    public static Animator createAlphaAnimatorBetweenValues(View targetView, float startAlphaValue, float endAlphaValue) {
        final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(targetView, View.ALPHA, startAlphaValue, endAlphaValue);
        alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimator.setDuration(ALPHA_ANIMATION_DURATION);
        return alphaAnimator;
    }

    public static void initialize(WheelComputationHelper computationHelper) {
        instance = new CoversFlowComputationHelper(computationHelper);
    }

    public static CoversFlowComputationHelper getInstance() {
        return instance;
    }

    private final WheelComputationHelper computationHelper;

    private CoversFlowComputationHelper(WheelComputationHelper computationHelper) {
        this.computationHelper = computationHelper;
        instance = this;

        this.maxCoverHeight = (int) (
                getGapBottomRayPositionForInnerRadius().y - getGapTopRayPositionForInnerRadius().y
                        - INNER_TOP_PADDING - INNER_BOTTOM_PADDING
        );
        final int coverDefaultHeight = (int) (maxCoverHeight / MAX_HEIGHT_DEFAULT_HEIGHT_COEFFICIENT);
        final int coverDefaultWidth = (int) (COVER_ASPECT_RATIO * coverDefaultHeight);

        this.initialCoverLayoutParams = new ViewGroup.MarginLayoutParams(coverDefaultWidth, coverDefaultHeight);

        this.coverDefaultMargins = new Rect(
                initialCoverLayoutParams.leftMargin,
                initialCoverLayoutParams.topMargin,
                initialCoverLayoutParams.rightMargin,
                initialCoverLayoutParams.bottomMargin
        );

        this.resizingEdgePosition = computeCoverResizingEdgePosition();
        this.leftOffset = (int) (resizingEdgePosition - getCoverMaxWidth() / 2);
        this.rightOffset = (int) (computationHelper.getComputedScreenDimensions().getWidth() - resizingEdgePosition
                - getCoverMaxWidth() / 2
        );
    }

    private float computeCoverResizingEdgePosition() {
        return getGapTopRayPositionForInnerRadius().x + getCoverMaxWidth() / 2;
    }

    public ViewGroup.MarginLayoutParams safeCopyInitialLayoutParams() {
        return new ViewGroup.MarginLayoutParams(initialCoverLayoutParams);
    }

    public int getCoverMaxHeight() {
        return maxCoverHeight;
    }

    public int getCoverMaxWidth() {
        return (int) Math.round(COVER_ASPECT_RATIO * maxCoverHeight);
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

    public int getLeftOffset() {
        return leftOffset;
    }

    public int getRightOffset() {
        return rightOffset;
    }

    public float getResizingEdgePosition() {
        return resizingEdgePosition;
    }

    private PointF getGapTopRayPositionForInnerRadius() {
        final PointF pos = CoordinatesHolder.ofPolar(computationHelper.getWheelConfig().getInnerRadius(),
                computationHelper.getWheelConfig().getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }

    private PointF getGapBottomRayPositionForInnerRadius() {
        final PointF pos = CoordinatesHolder.ofPolar(computationHelper.getWheelConfig().getInnerRadius(),
                computationHelper.getWheelConfig().getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad()
        ).toPointF();

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(pos);
    }
}
