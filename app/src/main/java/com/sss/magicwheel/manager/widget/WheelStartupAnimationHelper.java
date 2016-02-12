package com.sss.magicwheel.manager.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelRotationDirection;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 11.02.2016.
 */
final class WheelStartupAnimationHelper {

    private static final int TOP_WHEEL_APPEARING_ANIMATION_DURATION = 1000;
    private static final int FROM_TOP_GAP_EDGE_TO_BOTTOM_GAP_EDGE_BOTTOM_WHEEL_ANIMATION_DURATION = 1000;

    private final WheelComputationHelper computationHelper;
    private final WheelConfig.AngularRestrictions angularRestrictions;

    private final WheelContainerRecyclerView topWheelContainer;
    private final WheelContainerRecyclerView bottomWheelContainer;

    public WheelStartupAnimationHelper(WheelComputationHelper computationHelper,
                                       WheelContainerRecyclerView topWheelContainer,
                                       WheelContainerRecyclerView bottomWheelContainer) {
        this.computationHelper = computationHelper;
        this.angularRestrictions = computationHelper.getWheelConfig().getAngularRestrictions();
        this.topWheelContainer = topWheelContainer;
        this.bottomWheelContainer = bottomWheelContainer;

        setRotationPivotForContainer(topWheelContainer);
        setRotationPivotForContainer(bottomWheelContainer);

        setInitialTopWheelRotation();
    }

    public void playWheelStartupAnimation() {
        createWheelStartupAnimator().start();
    }

    private void setRotationPivotForContainer(WheelContainerRecyclerView wheelContainer) {
        final PointF circleCenterRelToRecyclerView = computationHelper.getWheelConfig().getCircleCenterRelToRecyclerView();
        wheelContainer.setPivotX(circleCenterRelToRecyclerView.x);
        wheelContainer.setPivotY(circleCenterRelToRecyclerView.y);
    }

    private void setInitialTopWheelRotation() {
        final float rotationAngleInDegree = getInitialTopWheelRotationAngleInDegree();
        setInitialRotationForWheelContainer(topWheelContainer, rotationAngleInDegree, WheelRotationDirection.Anticlockwise);
    }

    private float getInitialTopWheelRotationAngleInDegree() {
        final double rotationAngleInRad = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
        return (float) WheelComputationHelper.radToDegree(rotationAngleInRad);
    }

    /**
     * @param wheelContainer - either top or bottom wheel part
     */
    private void setInitialRotationForWheelContainer(WheelContainerRecyclerView wheelContainer,
                                                     float absRotationAngleInDegree,
                                                     WheelRotationDirection rotationDirection) {
        final int rotationSign = rotationDirection == WheelRotationDirection.Clockwise ? +1 : -1;
        final float rotationAngleWithSign = rotationSign * Math.abs(absRotationAngleInDegree);
        wheelContainer.setRotation(rotationAngleWithSign);
    }

    private Animator createWheelStartupAnimator() {
        final AnimatorSet startupWheelAnimator = new AnimatorSet();

        final TimeInterpolator timeInterpolator = new AccelerateDecelerateInterpolator();
        final Animator bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge = createBottomWheelAnimatorFromTopWheelEdgeToTopGapEdge(timeInterpolator);
        final Animator topWheelStartupAnimator = createTopWheelStartupAnimator(timeInterpolator);

        startupWheelAnimator.playSequentially(
                bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge,
                createBottomWheelAnimatorFromTopGapEdgeToBottomGapEdge());
        startupWheelAnimator.playTogether(topWheelStartupAnimator, bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge);

        startupWheelAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                topWheelContainer.setIsCutGapAreaActivated(true);
            }
        });

        return startupWheelAnimator;
    }

    private Animator createTopWheelStartupAnimator(TimeInterpolator timeInterpolator) {
        // top wheel part rotation
        final float topWheelStartRotationAngle = topWheelContainer.getRotation();
        final float topWheelEndRotationAngle = topWheelStartRotationAngle + getInitialTopWheelRotationAngleInDegree();
        final ObjectAnimator topWheelRotateAnimator = ObjectAnimator.ofFloat(
                topWheelContainer,
                View.ROTATION,
                topWheelStartRotationAngle, topWheelEndRotationAngle
        );

        topWheelRotateAnimator.setInterpolator(timeInterpolator);
        topWheelRotateAnimator.setDuration(TOP_WHEEL_APPEARING_ANIMATION_DURATION);

        return topWheelRotateAnimator;
    }

    private Animator createBottomWheelAnimatorFromTopWheelEdgeToTopGapEdge(TimeInterpolator timeInterpolator) {
        float endRotationAngleInDegree = getRotationAngleInDegreeForFirstStageBottomWheelAnimation();

        final ObjectAnimator res = ObjectAnimator.ofFloat(
                bottomWheelContainer,
                View.ROTATION,
                bottomWheelContainer.getRotation(),
                endRotationAngleInDegree
        );

        res.setInterpolator(timeInterpolator);
        res.setDuration(TOP_WHEEL_APPEARING_ANIMATION_DURATION);

        return res;
    }

    private Animator createBottomWheelAnimatorFromTopGapEdgeToBottomGapEdge() {
        float startRotationAngleInDegree = getRotationAngleInDegreeForFirstStageBottomWheelAnimation();
        final double rotationAngleInRad = angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad();
        final float endRotationAngleInDegree = startRotationAngleInDegree
                + (float) WheelComputationHelper.radToDegree(rotationAngleInRad);

        final ObjectAnimator res = ObjectAnimator.ofFloat(
                bottomWheelContainer,
                View.ROTATION,
                startRotationAngleInDegree,
                endRotationAngleInDegree
        );

        res.setInterpolator(new LinearInterpolator());
        res.setDuration(FROM_TOP_GAP_EDGE_TO_BOTTOM_GAP_EDGE_BOTTOM_WHEEL_ANIMATION_DURATION);

        return res;
    }

    /**
     * First stage is animation from top wheel angular restriction to top gap edge
     * angular restriction.
     */
    private float getRotationAngleInDegreeForFirstStageBottomWheelAnimation() {
        final double rotationAngleInRad = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
        return (float) WheelComputationHelper.radToDegree(rotationAngleInRad);
    }

}
