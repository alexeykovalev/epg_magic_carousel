package com.sss.magicwheel.manager.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelRotationDirection;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 11.02.2016.
 */
final class WheelStartupAnimationHelper {

    private static final int TOP_WHEEL_APPEARING_ANIMATION_DURATION = 3000;
    private static final int FROM_TOP_WHEEL_EDGE_TO_GAP_TOP_EDGE_BOTTOM_WHEEL_ANIMATION_DURATION = 3000;

    private static final int BOTTOM_WHEEL_APPEARING_ANIMATION_DURATION = 3000;

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
        final double rotationAngleInRad = angularRestrictions.getWheelLayoutStartAngleInRad()
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


    private boolean isThresholdAngleReached = false;

    private Animator createWheelStartupAnimator() {

        AnimatorSet startupWheelAnimator = new AnimatorSet();

        final ObjectAnimator bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge = createBottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge();
//        final Animator bottomWheelAnimatorFromTopGapEdgeToBottomGapEdge = createBottomWheelAnimatorFromTopGapEdgeToBottomGapEdge();
//        bottomWheelAnimatorFromTopGapEdgeToBottomGapEdge.setStartDelay(FROM_TOP_WHEEL_EDGE_TO_GAP_TOP_EDGE_BOTTOM_WHEEL_ANIMATION_DURATION);


//        final float thresholdAngleInDegree = (float) WheelComputationHelper.radToDegree(
//                angularRestrictions.getWheelTopEdgeAngleRestrictionInRad() - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad()
//        );
//        bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float animatedValue = (Float) animation.getAnimatedValue();
///*
//                if (animatedValue >= thresholdAngleInDegree && !isThresholdAngleReached) {
//                    isThresholdAngleReached = true;
//                    topWheelContainer.setVisibility(View.VISIBLE);
//                }
//*/
//            }
//        });

        final ObjectAnimator topWheelStartupAnimator = createTopWheelStartupAnimator();

        startupWheelAnimator.playTogether(bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge, topWheelStartupAnimator);

//        startupWheelAnimator.playSequentially(bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge, bottomWheelAnimatorFromTopGapEdgeToBottomGapEdge);

        return startupWheelAnimator;
//        return bottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge;
    }

    private ObjectAnimator createBottomWheelAnimatorFromTopWheelEdgeToBottomGapEdge() {
        final double rotationAngleInRad = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad();
        float endRotationAngleInDegree = (float) WheelComputationHelper.radToDegree(rotationAngleInRad);

        final ObjectAnimator res = ObjectAnimator.ofFloat(
                bottomWheelContainer,
                View.ROTATION,
                bottomWheelContainer.getRotation(),
                endRotationAngleInDegree
        );
        res.setInterpolator(new LinearInterpolator());
        res.setDuration(FROM_TOP_WHEEL_EDGE_TO_GAP_TOP_EDGE_BOTTOM_WHEEL_ANIMATION_DURATION);

        return res;
    }

    private ObjectAnimator createTopWheelStartupAnimator() {
        // top wheel part rotation
        final float topWheelStartRotationAngle = topWheelContainer.getRotation();
        final float topWheelEndRotationAngle = topWheelStartRotationAngle + getInitialTopWheelRotationAngleInDegree();
        final ObjectAnimator topWheelRotateAnimator = ObjectAnimator.ofFloat(
                topWheelContainer,
                View.ROTATION,
                topWheelStartRotationAngle, topWheelEndRotationAngle
        );

        topWheelRotateAnimator.setInterpolator(new LinearInterpolator());
        topWheelRotateAnimator.setDuration(TOP_WHEEL_APPEARING_ANIMATION_DURATION);

        return topWheelRotateAnimator;
    }


    /*private static final int FROM_TOP_GAP_EDGE_TO_BOTTOM_GAP_EDGE_BOTTOM_WHEEL_ANIMATION_DURATION = 3000;
    private Animator createBottomWheelAnimatorFromTopGapEdgeToBottomGapEdge() {

        float startRotationAngle = (float) WheelComputationHelper.radToDegree(angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad());

        final double rotationAngleInRad = angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad();
        float endRotationAngleInDegree = (float) WheelComputationHelper.radToDegree(rotationAngleInRad);

//        final float startRotationAngle = bottomWheelContainer.getRotation();
        final ObjectAnimator res = ObjectAnimator.ofFloat(
                bottomWheelContainer,
                View.ROTATION,
                startRotationAngle,
                startRotationAngle + endRotationAngleInDegree
        );
        res.setInterpolator(new LinearInterpolator());
        res.setDuration(FROM_TOP_GAP_EDGE_TO_BOTTOM_GAP_EDGE_BOTTOM_WHEEL_ANIMATION_DURATION);

        return res;
    }*/

}
