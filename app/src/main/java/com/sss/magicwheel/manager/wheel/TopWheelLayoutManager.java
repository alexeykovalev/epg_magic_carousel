package com.sss.magicwheel.manager.wheel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.widget.WheelStartupAnimationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class TopWheelLayoutManager extends AbstractWheelLayoutManager {

    /**
     * In order to make wheel infinite we have to set virtual position as start layout position.
     */
    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    public TopWheelLayoutManager(WheelComputationHelper computationHelper,
                                 WheelStartupAnimationHelper animationHelper,
                                 WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        super(computationHelper, animationHelper, initialLayoutFinishingListener);
    }

    @Override
    protected int getStartLayoutFromAdapterPosition() {
        return START_LAYOUT_FROM_ADAPTER_POSITION;
    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad();
    }

    @Override
    protected int onLayoutChildrenForStartupAnimation(RecyclerView.Recycler recycler, RecyclerView.State state) {

        // delta angle in order to hide top wheel outside screen's left edge
        final double additionalDeltaAngleInRad = angularRestrictions.getWheelLayoutStartAngleInRad()
                - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
        final double startLayoutAngleInRad = angularRestrictions.getWheelLayoutStartAngleInRad() + additionalDeltaAngleInRad;

        setLayoutStartAngleInRad(startLayoutAngleInRad);

        final double sectorAngleInRad = angularRestrictions.getSectorAngleInRad();
        final double bottomLimitAngle = angularRestrictions.getWheelLayoutStartAngleInRad();

        double layoutAngle = computationHelper.getSectorAlignmentAngleInRadBySectorTopEdge(startLayoutAngleInRad);

        int childPos = getStartLayoutFromAdapterPosition();
        int layoutedChildrenCount = 0;
        boolean isInsideLayoutBounds = computationHelper.getSectorAngleBottomEdgeInRad(layoutAngle) > bottomLimitAngle;
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
    protected int onLayoutChildrenRegular(RecyclerView.Recycler recycler, RecyclerView.State state) {

        final double startLayoutAngleInRad = angularRestrictions.getWheelLayoutStartAngleInRad();
        setLayoutStartAngleInRad(startLayoutAngleInRad);

        final double sectorAngleInRad = angularRestrictions.getSectorAngleInRad();
        final double bottomLimitAngle = getLayoutEndAngleInRad();

        double layoutAngle = computationHelper.getSectorAlignmentAngleInRadBySectorTopEdge(startLayoutAngleInRad);

        int childPos = getStartLayoutFromAdapterPosition();
        int layoutedChildrenCount = 0;
        boolean isInsideLayoutBounds = computationHelper.getSectorAngleBottomEdgeInRad(layoutAngle) > bottomLimitAngle;
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
    protected void informLayoutFinishingListener(int lastlyLayoutedChildPos) {
        if (initialLayoutFinishingListener != null) {
            initialLayoutFinishingListener.onInitialLayoutFinished(lastlyLayoutedChildPos + 1);
        }
    }

    @Override
    public Animator createWheelStartupAnimator() {

        final LayoutParams childClosestToLayoutStartEdgeLp = getChildLayoutParams(getChildClosestToLayoutEndEdge());

        final float anglePositionInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
        final float endAngleInRad = 0; //(float) animationValuesHolder.getEndAngleInRad();
        ValueAnimator animator = ValueAnimator.ofFloat(
//                (float) animationValuesHolder.getStartAngleInRad(),
                anglePositionInRad,
                endAngleInRad
        );

        final double rAngle = WheelComputationHelper.degreeToRadian(120);

//        clockwiseRotator.rotateWheelBy(rAngle);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float firstChildAnglePositionInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
                final float newRotationAngleInRad = (Float) animation.getAnimatedValue();

                double rotationDeltaInRad = firstChildAnglePositionInRad - newRotationAngleInRad;

                clockwiseRotator.rotateWheelBy(rotationDeltaInRad);


               /* Log.e("TAG",
                        "newRotationAngleInRad [" + WheelComputationHelper.radToDegree(newRotationAngleInRad) + "], " +
                                "firstChildAnglePositionInRad [" + WheelComputationHelper.radToDegree(firstChildAnglePositionInRad) + "], " +
                                "rotationDeltaInRad [" + WheelComputationHelper.radToDegree(rotationDeltaInRad) + "]"
                );*/
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                final WheelConfig.AngularRestrictions angularRestrictions = wheelConfig.getAngularRestrictions();
//                final double halfSectorAngleInRad = angularRestrictions.getSectorAngleInRad() / 2;
//                final double newLayoutStartAngleInRad = angularRestrictions.getGapAreaBottomEdgeAngleRestrictionInRad() + halfSectorAngleInRad;
//
//                Log.e("TAG", "Animation finished newLayoutStartAngleInRad ["
//                        + WheelComputationHelper.radToDegree(newLayoutStartAngleInRad) + "]");
//                setLayoutStartAngleInRad(newLayoutStartAngleInRad);



//                clockwiseRotator.recycleSectors(recycler, state);
            }
        });

        animator.setDuration(WheelStartupAnimationHelper.TOP_WHEEL_ANIMATION_DURATION);
//        animator.start();

        return animator;
    }

}
