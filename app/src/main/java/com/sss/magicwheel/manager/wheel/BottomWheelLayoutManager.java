package com.sss.magicwheel.manager.wheel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class BottomWheelLayoutManager extends AbstractWheelLayoutManager {

    // TODO: 05.02.2016 for testing
//    @Deprecated
//    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    public BottomWheelLayoutManager(Context context, WheelComputationHelper computationHelper, WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        super(computationHelper, initialLayoutFinishingListener);
        createStartupAnimationValuesHolder();
    }

    @Override
    protected StartupAnimationValues createStartupAnimationValuesHolder() {
//        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
        final double halfSectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;

        final double startAngleInRad = getLayoutStartAngleInRad(); // + halfSectorAngleInRad;
        return new StartupAnimationValues(
                startAngleInRad,
                wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad() + halfSectorAngleInRad
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // We have nothing to show for an empty data set but clear any existing views
        int itemCount = getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        removeAndRecycleAllViews(recycler);

        if (getStartLayoutFromAdapterPosition() == NOT_DEFINED_ADAPTER_POSITION) {
            return;
        }

        // TODO: 16.02.2016 depending on isAnimationPlayed ot not call different layout childred methods

//        final int lastlyLayoutedChildPos = onLayoutChildrenRegular(recycler, state);
        final int lastlyLayoutedChildPos = onLayoutChildrenForStartupAnimation(recycler, state);

        if (initialLayoutFinishingListener != null) {
            initialLayoutFinishingListener.onInitialLayoutFinished(lastlyLayoutedChildPos - 1);
        }

        // TODO: 16.02.2016
        createWheelStartupAnimator().start();
    }

    private int onLayoutChildrenForStartupAnimation(RecyclerView.Recycler recycler, RecyclerView.State state) {

//        setLayoutStartAngleInRad(angularRestrictions.getWheelLayoutStartAngleInRad());

        final double sectorAngleInRad = angularRestrictions.getSectorAngleInRad();
        final double bottomLimitAngle = getLayoutEndAngleInRad();

        double layoutAngle = computationHelper.getSectorAlignmentAngleInRadBySectorTopEdge(
                angularRestrictions.getWheelLayoutStartAngleInRad()
        );

        int childPos = getStartLayoutFromAdapterPosition();
        int layoutedChildrenCount = 0;
        boolean isInsideLayoutBounds = computationHelper.getSectorAngleTopEdgeInRad(layoutAngle) > bottomLimitAngle;
        while (isInsideLayoutBounds && layoutedChildrenCount < state.getItemCount()) {
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            isInsideLayoutBounds = computationHelper.getSectorAngleTopEdgeInRad(layoutAngle) > bottomLimitAngle;
            layoutedChildrenCount++;
            childPos++;
        }

        return childPos;
    }

    private int onLayoutChildrenRegular(RecyclerView.Recycler recycler, RecyclerView.State state) {

        setLayoutStartAngleInRad(angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad());

        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
        final double bottomLimitAngle = getLayoutEndAngleInRad();

        double layoutAngle = computationHelper.getSectorAlignmentAngleInRadBySectorTopEdge(
                angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad()
        );

        int childPos = getStartLayoutFromAdapterPosition();
        int layoutedChildrenCount = 0;
        // when sector's top edge goes outside bottom edge layout angle - then stop children layout
        boolean isInsideLayoutBounds = computationHelper.getSectorAngleTopEdgeInRad(layoutAngle) > bottomLimitAngle;
        while (isInsideLayoutBounds && layoutedChildrenCount < state.getItemCount()) {
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            isInsideLayoutBounds = computationHelper.getSectorAngleTopEdgeInRad(layoutAngle) > bottomLimitAngle;
            layoutedChildrenCount++;
            childPos++;
        }

        return childPos;
    }

    @Override
    public Animator createWheelStartupAnimator() {
        final LayoutParams childClosestToLayoutStartEdgeLp = getChildLayoutParams(getChildClosestToLayoutStartEdge());

        final float fromAngleInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
        final float toAngleInRad = (float) computationHelper.getSectorAlignmentAngleInRadBySectorTopEdge(
                angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad()
        );

        final ValueAnimator wheelStartupAnimator = ValueAnimator.ofFloat(fromAngleInRad, toAngleInRad);

        wheelStartupAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float firstChildAnglePositionInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
                final float currentlyAnimatedAngleInRad = (Float) animation.getAnimatedValue();

                final double rotationDeltaInRad = firstChildAnglePositionInRad - currentlyAnimatedAngleInRad;
                clockwiseRotator.rotateWheelBy(rotationDeltaInRad);
            }
        });

        wheelStartupAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setLayoutStartAngleInRad(angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad());
            }
        });

        wheelStartupAnimator.setDuration(WheelComputationHelper.BOTTOM_WHEEL_ANIMATION_DURATION);

        return wheelStartupAnimator;
    }

    @Deprecated
    private Animator createSecondStageAnimator() {

        final LayoutParams childClosestToLayoutStartEdgeLp = getChildLayoutParams(getChildClosestToLayoutStartEdge());

        final WheelConfig.AngularRestrictions angularRestrictions = wheelConfig.getAngularRestrictions();
        final double halfSectorAngleInRad = angularRestrictions.getSectorAngleInRad() / 2;

        final float secondStageStartEdgePositionInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
        final float secondStageEndEdgePositionInRad = (float) animationValuesHolder.getEndAngleInRad();

        ValueAnimator secondStageAnimator = ValueAnimator.ofFloat(
//                (float) animationValuesHolder.getStartAngleInRad(),
                secondStageStartEdgePositionInRad,
                secondStageEndEdgePositionInRad
        );

        secondStageAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float firstChildAnglePositionInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
                final float newRotationAngleInRad = (Float) animation.getAnimatedValue();

                double rotationDeltaInRad = firstChildAnglePositionInRad - newRotationAngleInRad;

                clockwiseRotator.rotateWheelBy(rotationDeltaInRad);

                Log.e("TAG",
                        "newRotationAngleInRad [" + WheelComputationHelper.radToDegree(newRotationAngleInRad) + "], " +
                                "firstChildAnglePositionInRad [" + WheelComputationHelper.radToDegree(firstChildAnglePositionInRad) + "], " +
                                "rotationDeltaInRad [" + WheelComputationHelper.radToDegree(rotationDeltaInRad) + "]"
                );
            }
        });

        secondStageAnimator.setDuration(3000);

        return secondStageAnimator;


    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad();

//                + wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();
    }

//    @Override
//    protected void rotateWheelBy(double rotationAngleInRad, WheelRotationDirection rotationDirection,
//                               RecyclerView.Recycler recycler, RecyclerView.State state) {
//
//        super.rotateWheelBy(rotationAngleInRad, rotationDirection, recycler, state);
//
//        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
//            rotateWheelAnticlockwise(rotationAngleInRad, recycler, state);
//        } else {
//            throw new UnsupportedOperationException("Not implemented feature yet.");
//        }
//    }


    @Deprecated
    private void rotateWheelAnticlockwise(double rotationAngleInRad, RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < getChildCount(); i++) {
            final View sectorView = getChildAt(i);
            final LayoutParams sectorViewLp = getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad += rotationAngleInRad;
            sectorView.setLayoutParams(sectorViewLp);
            alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }

        recycleSectorsFromTopIfNeeded(recycler, state);
        addSectorsToBottomIfNeeded(recycler, state);
    }

    @Deprecated
    /**
     * When sectorView's bottom edge goes outside
     */
    private void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < getChildCount(); i++) {
            final View sectorView = getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewBottomEdgeAngularPosInRad > computationHelper.getWheelLayoutStartAngleInRad()) {
                removeAndRecycleViewAt(i, recycler);
//                Log.i(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    @Deprecated
    private void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToEndSectorView = getChildClosestToLayoutEndEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToEndSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
        final double bottomEndLayoutAngleInRad = getLayoutEndAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad - sectorAngleInRad;
        double newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = getPosition(closestToEndSectorView) + 1;
        int alreadyLayoutedChildrenCount = 0;

//        Log.e("TAG", "addSectorsToBottomIfNeeded() " +
//                "newSectorViewTopEdgeAngularPosInRad [" + WheelComputationHelper.radToDegree(newSectorViewTopEdgeAngularPosInRad) + "], " +
//                "bottomEndLayoutAngleInRad [" + WheelComputationHelper.radToDegree(bottomEndLayoutAngleInRad) + "]");

        while (newSectorViewTopEdgeAngularPosInRad > bottomEndLayoutAngleInRad && alreadyLayoutedChildrenCount < state.getItemCount()) {

//            Log.e("TAG", "addSectorsToBottomIfNeeded() " +
//                    "newSectorViewTopEdgeAngularPosInRad [" + WheelComputationHelper.radToDegree(newSectorViewTopEdgeAngularPosInRad) + "], " +
//                    "bottomEndLayoutAngleInRad [" + WheelComputationHelper.radToDegree(bottomEndLayoutAngleInRad) + "]");

            setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, true);
            newSectorViewLayoutAngle -= sectorAngleInRad;
            newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos++;
            alreadyLayoutedChildrenCount++;
        }
    }


}
