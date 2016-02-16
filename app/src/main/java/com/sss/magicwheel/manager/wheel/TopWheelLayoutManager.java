package com.sss.magicwheel.manager.wheel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class TopWheelLayoutManager extends AbstractWheelLayoutManager {

    /**
     * In order to make wheel infinite we have to set virtual position as start layout position.
     */
    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    public TopWheelLayoutManager(Context context, WheelComputationHelper computationHelper, WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        super(computationHelper, initialLayoutFinishingListener);
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

        final WheelConfig.AngularRestrictions angularRestrictions = wheelConfig.getAngularRestrictions();
        final double sectorAngleInRad = angularRestrictions.getSectorAngleInRad();
        final double halfSectorAngleInRad = angularRestrictions.getSectorAngleInRad() / 2;

//        final double additionalrotationAngleInRad = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
//                - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
        final double bottomLimitAngle = wheelConfig.getAngularRestrictions().getWheelTopEdgeAngleRestrictionInRad(); //getLayoutEndAngleInRad();

        double layoutAngle = animationValuesHolder.getStartAngleInRad();
        int childPos = getStartLayoutFromAdapterPosition();
        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }

        if (initialLayoutFinishingListener != null) {
            initialLayoutFinishingListener.onInitialLayoutFinished(childPos);
        }

//        createWheelStartupAnimator(recycler, state);

    }

    public Animator createWheelStartupAnimator(/*final RecyclerView.Recycler recycler, final RecyclerView.State state*/) {

        final LayoutParams childClosestToLayoutStartEdgeLp = getChildLayoutParams(getChildClosestToLayoutEndEdge());

        final float anglePositionInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
        final float endAngleInRad = (float) animationValuesHolder.getEndAngleInRad();
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

        animator.setDuration(1000);
//        animator.start();

        return animator;
    }

    @Override
    protected StartupAnimationValues createStartupAnimationValuesHolder() {
//        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
        final WheelConfig.AngularRestrictions angularRestrictions = wheelConfig.getAngularRestrictions();
        final double halfSectorAngleInRad = angularRestrictions.getSectorAngleInRad() / 2;

        final double additionalRotationAngleInRad = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
        double startAngleInRad = getLayoutStartAngleInRad() + halfSectorAngleInRad + additionalRotationAngleInRad;

        return new StartupAnimationValues(
                startAngleInRad,
                angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad() + halfSectorAngleInRad
        );
    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad();
//                - wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;
    }

    @Override
    protected int getStartLayoutFromAdapterPosition() {
        return START_LAYOUT_FROM_ADAPTER_POSITION;
    }
}
