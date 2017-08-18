package com.magicepg.wheel.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.magicepg.wheel.rotator.AbstractWheelRotator;
import com.magicepg.wheel.rotator.TopAnticlockwiseWheelRotator;
import com.magicepg.wheel.rotator.TopClockwiseWheelRotator;
import com.magicepg.wheel.WheelComputationHelper;

/**
 * Does children layout for wheel's top part.
 *
 * @author Alexey Kovalev
 * @since 05.02.2017
 */
public final class TopWheelLayoutManager extends AbstractWheelLayoutManager {

    private static final long TOP_WHEEL_STARTUP_ANIMATION_DURATION = 2000;

    private final WheelOnScrollingCallback scrollingCallback;

    public interface WheelOnScrollingCallback {
        void onScrolledBy(int dy);
    }

    public TopWheelLayoutManager(Context context,
                                 WheelComputationHelper computationHelper,
                                 WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener,
                                 WheelOnScrollingCallback scrollingCallback) {
        super(context, computationHelper, initialLayoutFinishingListener);
        this.scrollingCallback = scrollingCallback;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int scrollingDy = super.scrollVerticallyBy(dy, recycler, state);
        scrollingCallback.onScrolledBy(scrollingDy);
        return scrollingDy;
    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return angularRestrictions.getWheelTopEdgeAngleRestrictionInRad();
    }

    @Override
    protected AbstractWheelRotator createClockwiseRotator() {
        return new TopClockwiseWheelRotator(this, computationHelper);
    }

    @Override
    protected AbstractWheelRotator createAnticlockwiseRotator() {
        return new TopAnticlockwiseWheelRotator(this, computationHelper);
    }

    @Override
    protected int onLayoutChildrenForStartupAnimation(RecyclerView.Recycler recycler, RecyclerView.State state) {

        final double wheelTopEdgeAngleRestrictionInRad = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad();
        // delta angle in order to hide top wheel outside screen's left edge
        final double additionalDeltaAngleInRad = wheelTopEdgeAngleRestrictionInRad - angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
        final double topLimitAngleInRad = wheelTopEdgeAngleRestrictionInRad + additionalDeltaAngleInRad;

        final double sectorAngleInRad = angularRestrictions.getSectorAngleInRad();
        double layoutAngle = computationHelper.getSectorAlignmentAngleInRadBySectorTopEdge(wheelTopEdgeAngleRestrictionInRad);

        int childPos = getStartLayoutFromAdapterPosition();
        int layoutedChildrenCount = 0;
        boolean isInsideLayoutBounds = computationHelper.getSectorAngleBottomEdgeInRad(layoutAngle) < topLimitAngleInRad;
        while (isInsideLayoutBounds && layoutedChildrenCount < state.getItemCount()) {
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle += sectorAngleInRad;
            isInsideLayoutBounds = computationHelper.getSectorAngleTopEdgeInRad(layoutAngle) < topLimitAngleInRad;
            layoutedChildrenCount++;
            childPos++;
        }

        return childPos;
    }

    @Override
    protected int onLayoutChildrenRegular(RecyclerView.Recycler recycler, RecyclerView.State state) {

        final double startLayoutAngleInRad = angularRestrictions.getGapAreaTopEdgeAngleRestrictionInRad();
        setLayoutStartAngleInRad(startLayoutAngleInRad);

        final double sectorAngleInRad = angularRestrictions.getSectorAngleInRad();
        final double topLimitAngle = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad();

        double layoutAngle = startLayoutAngleInRad;

        int childPos = getStartLayoutFromAdapterPosition();
        int layoutedChildrenCount = 0;
        boolean isInsideLayoutBounds = computationHelper.getSectorAngleBottomEdgeInRad(layoutAngle) < topLimitAngle;
        while (isInsideLayoutBounds && layoutedChildrenCount < state.getItemCount()) {
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle += sectorAngleInRad;
            isInsideLayoutBounds = computationHelper.getSectorAngleTopEdgeInRad(layoutAngle) < topLimitAngle;
            layoutedChildrenCount++;
            childPos++;
        }

        return childPos;
    }

    @Override
    protected void notifyLayoutFinishingListener(int lastlyLayoutedChildPos) {
        initialLayoutFinishingListener.onInitialLayoutFinished(lastlyLayoutedChildPos);
    }

    @Override
    protected Animator createWheelStartupAnimator(final RecyclerView.Recycler recycler, final RecyclerView.State state) {

        final LayoutParams childClosestToLayoutEndEdgeLp = getChildLayoutParams(getChildClosestToLayoutEndEdge());

        final float fromAngleInRad = (float) childClosestToLayoutEndEdgeLp.anglePositionInRad;
        final float toAngleInRad = (float) (angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
                - angularRestrictions.getSectorHalfAngleInRad()
        );

        final ValueAnimator startupWheelAnimator = ValueAnimator.ofFloat(fromAngleInRad, toAngleInRad);

        startupWheelAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float lastChildAnglePositionInRad = (float) childClosestToLayoutEndEdgeLp.anglePositionInRad;
                final float currentlyAnimatedAngleInRad = (Float) animation.getAnimatedValue();

                final double rotationDeltaInRad = lastChildAnglePositionInRad - currentlyAnimatedAngleInRad;
                clockwiseRotator.rotateWheelBy(rotationDeltaInRad);
                notifyOnAnimationUpdate(WheelStartupAnimationStatus.InProgress);
            }
        });

        startupWheelAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                notifyOnAnimationUpdate(WheelStartupAnimationStatus.Start);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                notifyOnAnimationUpdate(WheelStartupAnimationStatus.Finished);
            }
        });

        startupWheelAnimator.setDuration(TOP_WHEEL_STARTUP_ANIMATION_DURATION);

        return startupWheelAnimator;
    }

}
