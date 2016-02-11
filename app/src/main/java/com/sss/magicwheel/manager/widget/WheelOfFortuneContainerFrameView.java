package com.sss.magicwheel.manager.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.entity.WheelRotationDirection;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.decor.WheelFrameItemDecoration;
import com.sss.magicwheel.manager.decor.WheelSectorRayItemDecoration;
import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;
import com.sss.magicwheel.manager.wheel.BottomWheelLayoutManager;
import com.sss.magicwheel.manager.wheel.TopWheelLayoutManager;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 10.02.2016.
 */
public final class WheelOfFortuneContainerFrameView extends FrameLayout {

    private static final int TOP_WHEEL_APPEARING_ANIMATION_DURATION = 5000;
    private static final int BOTTOM_WHEEL_APPEARING_ANIMATION_DURATION = 3000;

    private final WheelComputationHelper computationHelper;
    private final WheelConfig.AngularRestrictions angularRestrictions;

    private BottomWheelLayoutManager bottomWheelLayoutManager;

    private WheelContainerRecyclerView topWheelContainer;
    private WheelContainerRecyclerView bottomWheelContainer;
    private WheelStartupAnimationHelper wheelStartupAnimationHelper;

    public WheelOfFortuneContainerFrameView(Context context) {
        this(context, null);
    }

    public WheelOfFortuneContainerFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelOfFortuneContainerFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        computationHelper = WheelComputationHelper.getInstance();
        angularRestrictions = computationHelper.getWheelConfig().getAngularRestrictions();
        inflateAndBindContainerView(context);

        // in order to simplify wheel appearing animation angle computation
//        topWheelContainer.setVisibility(INVISIBLE);
//        bottomWheelContainer.setVisibility(INVISIBLE);

        initBottomWheelContainer(bottomWheelContainer);
        initTopWheelContainer(topWheelContainer);

        wheelStartupAnimationHelper = new WheelStartupAnimationHelper(computationHelper, topWheelContainer, bottomWheelContainer);

//        setInitialTopWheelRotation();
//        setInitialBottomWheelRotation();
    }

    private void inflateAndBindContainerView(Context context) {
        inflate(context, R.layout.wheel_container_layout, this);
        topWheelContainer = (WheelContainerRecyclerView) findViewById(R.id.top_wheel_container);
        bottomWheelContainer = (WheelContainerRecyclerView) findViewById(R.id.bottom_wheel_container);
    }

    @Deprecated
    private void setInitialBottomWheelRotation() {
        final float rotationAngleInDegree = getInitialBottomWheelRotationAngleInDegree();
//        setInitialRotationForWheelContainer(bottomWheelContainer, rotationAngleInDegree, WheelRotationDirection.Anticlockwise);
    }

    private float getInitialBottomWheelRotationAngleInDegree() {
        final WheelConfig.AngularRestrictions angularRestrictions = computationHelper.getWheelConfig().getAngularRestrictions();
        final double rotationAngleInRad = angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getWheelBottomEdgeAngleRestrictionInRad();
        return (float) WheelComputationHelper.radToDegree(rotationAngleInRad);
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        topWheelContainer.dispatchTouchEvent(event);
        bottomWheelContainer.dispatchTouchEvent(event);
        return true;
    }

    public void swapData(List<WheelDataItem> newData) {
        final List<WheelDataItem> unmodifiableNewData = Collections.unmodifiableList(newData);
        topWheelContainer.getAdapter().swapData(unmodifiableNewData);
        bottomWheelContainer.getAdapter().swapData(unmodifiableNewData);
    }

    private void initTopWheelContainer(RecyclerView topWheelContainerView) {
        topWheelContainerView.setLayoutManager(new TopWheelLayoutManager(getContext(), computationHelper,
                new AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener() {
                    @Override
                    public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                        bottomWheelLayoutManager.setStartLayoutFromAdapterPosition(finishedAtAdapterPosition);
                    }
                }));
        topWheelContainerView.setAdapter(createEmptyWheelAdapter());
        addTopWheelItemDecorations(topWheelContainerView);
    }

    private void initBottomWheelContainer(RecyclerView topWheelContainerView) {
        bottomWheelLayoutManager = new BottomWheelLayoutManager(getContext(), computationHelper,
                new AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener() {
            @Override
            public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
//                createStartWheelAppearingAnimation().start();
//                createAlternativeStartAnimation().start();

                wheelStartupAnimationHelper.playWheelStartupAnimation();
            }
        });
        topWheelContainerView.setLayoutManager(bottomWheelLayoutManager);
        topWheelContainerView.setAdapter(createEmptyWheelAdapter());
        addBottomWheelItemDecorations(topWheelContainerView);
    }

    private void addTopWheelItemDecorations(RecyclerView wheelContainerView) {
        wheelContainerView.addItemDecoration(new WheelFrameItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

    private void addBottomWheelItemDecorations(RecyclerView wheelContainerView) {
//        wheelContainerView.addItemDecoration(new WheelFrameItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

//    private Animator createStartWheelAppearingAnimation() {
//
//        // top wheel part rotation
//        final float topWheelStartRotationAngle = topWheelContainer.getRotation();
//        final float topWheelEndRotationAngle = topWheelStartRotationAngle + getInitialTopWheelRotationAngleInDegree();
//        final ObjectAnimator topWheelRotateAnimator = ObjectAnimator.ofFloat(
//                topWheelContainer,
//                View.ROTATION,
//                topWheelStartRotationAngle, topWheelEndRotationAngle
//        );
//
//        topWheelRotateAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                bottomWheelContainer.setVisibility(VISIBLE);
//            }
//        });
//        topWheelRotateAnimator.setInterpolator(new LinearInterpolator());
//        topWheelRotateAnimator.setDuration(TOP_WHEEL_APPEARING_ANIMATION_DURATION);
//
//        // bottom wheel part rotation
//        final float bottomWheelStartRotationAngle = bottomWheelContainer.getRotation();
//        final float bottomWheelEndRotationAngle = bottomWheelStartRotationAngle + getInitialBottomWheelRotationAngleInDegree();
//        final ObjectAnimator bottomWheelRotateAnimator = ObjectAnimator.ofFloat(
//                bottomWheelContainer,
//                View.ROTATION,
//                bottomWheelStartRotationAngle, bottomWheelEndRotationAngle
//        );
//
//        bottomWheelRotateAnimator.setInterpolator(new LinearInterpolator());
//        bottomWheelRotateAnimator.setDuration(BOTTOM_WHEEL_APPEARING_ANIMATION_DURATION);
//
//        final AnimatorSet wheelAppearingAnimator = new AnimatorSet();
//        wheelAppearingAnimator.playSequentially(topWheelRotateAnimator, bottomWheelRotateAnimator);
//
//        return wheelAppearingAnimator;
//    }

    private WheelAdapter createEmptyWheelAdapter() {
        return new WheelAdapter(getContext(), Collections.<WheelDataItem>emptyList(), new WheelAdapter.OnWheelItemClickListener() {
            @Override
            public void onItemClicked(View clickedSectorView, WheelDataItem dataItem) {
                topWheelContainer.handleTapOnSectorView(clickedSectorView);
//                topWheelContainer.smoothlySelectDataItem(dataItem);
            }
        });
    }

}
