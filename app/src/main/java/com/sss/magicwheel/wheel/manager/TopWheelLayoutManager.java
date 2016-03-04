package com.sss.magicwheel.wheel.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.wheel.WheelAdapter;
import com.sss.magicwheel.wheel.misc.WheelComputationHelper;
import com.sss.magicwheel.wheel.widget.AbstractWheelContainerRecyclerView;

/**
 * Does children layout for wheel's top part.
 *
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class TopWheelLayoutManager extends AbstractWheelLayoutManager {

    private static final long TOP_WHEEL_STARTUP_ANIMATION_DURATION = 1000;

    /**
     * In order to make wheel infinite we have to set virtual position as start layout position.
     */
    public static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

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
        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad();
    }

    @Override
    protected int onLayoutChildrenForStartupAnimation(RecyclerView.Recycler recycler, RecyclerView.State state) {

        // delta angle in order to hide top wheel outside screen's left edge
        final double additionalDeltaAngleInRad = angularRestrictions.getWheelTopEdgeAngleRestrictionInRad()
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
    protected void notifyLayoutFinishingListener(int lastlyLayoutedChildPos) {
        initialLayoutFinishingListener.onInitialLayoutFinished(lastlyLayoutedChildPos);
    }

    @Override
    protected Animator createWheelStartupAnimator(final RecyclerView.Recycler recycler, final RecyclerView.State state) {

        final LayoutParams childClosestToLayoutStartEdgeLp = getChildLayoutParams(getChildClosestToLayoutStartEdge());

        final float fromAngleInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
        final float toAngleInRad = (float) (angularRestrictions.getWheelLayoutStartAngleInRad()
                - angularRestrictions.getSectorHalfAngleInRad()
        );

        final ValueAnimator startupWheelAnimator = ValueAnimator.ofFloat(fromAngleInRad, toAngleInRad);

        startupWheelAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float firstChildAnglePositionInRad = (float) childClosestToLayoutStartEdgeLp.anglePositionInRad;
                final float currentlyAnimatedAngleInRad = (Float) animation.getAnimatedValue();

                final double rotationDeltaInRad = firstChildAnglePositionInRad - currentlyAnimatedAngleInRad;
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
                setLayoutStartAngleInRad(wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad());
                notifyOnAnimationUpdate(WheelStartupAnimationStatus.Finished);
            }
        });

        startupWheelAnimator.setDuration(TOP_WHEEL_STARTUP_ANIMATION_DURATION);

        return startupWheelAnimator;
    }

}
