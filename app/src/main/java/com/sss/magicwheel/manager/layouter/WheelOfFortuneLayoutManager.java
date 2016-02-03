package com.sss.magicwheel.manager.layouter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelRotationDirection;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
public final class WheelOfFortuneLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = WheelOfFortuneLayoutManager.class.getCanonicalName();
    private static final double NOT_DEFINED_ROTATION_ANGLE = Double.MIN_VALUE;

    /**
     * In order to make wheel infinite we have to set virtual position as start layout position.
     */
    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    private static final boolean IS_LOG_ACTIVATED = true;
    private static final boolean IS_FILTER_LOG_BY_METHOD_NAME = true;

    private static final Set<String> ALLOWED_METHOD_NAMES = new HashSet<>();

    static {
        ALLOWED_METHOD_NAMES.add("scrollVerticallyBy");
        ALLOWED_METHOD_NAMES.add("onLayoutChildren");
    }

    private static boolean isMessageContainsAllowedMethod(String logMessage) {
        if (logMessage == null || logMessage.isEmpty()) {
            return false;
        }
        for (String methodName : ALLOWED_METHOD_NAMES) {
            if (logMessage.contains(methodName)) {
                return true;
            }
        }
        return false;
    }


    private final WheelConfig wheelConfig;
    private final WheelComputationHelper computationHelper;

    private final TopSubWheelLayouter topSubWheelLayouter;
    private final BottomSubWheelLayouter bottomSubWheelLayouter;

    public WheelOfFortuneLayoutManager(WheelComputationHelper computationHelper) {
        this.computationHelper = computationHelper;
        this.wheelConfig = computationHelper.getWheelConfig();
        BaseSubWheelLayouter.initialize(this, computationHelper);
        this.topSubWheelLayouter = BaseSubWheelLayouter.topSubwheel();
        this.bottomSubWheelLayouter = BaseSubWheelLayouter.bottomSubwheel();
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        removeAndRecycleAllViews(recycler);
        recycler.clear();
    }

    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        // We have nothing to show for an empty data set but clear any existing views
        int itemCount = getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        removeAndRecycleAllViews(recycler);

        topSubWheelLayouter.doInitialChildrenLayout(recycler, state, START_LAYOUT_FROM_ADAPTER_POSITION,
                new BaseSubWheelLayouter.OnInitialLayoutFinishingListener() {
                    @Override
                    public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                        bottomSubWheelLayouter.doInitialChildrenLayout(recycler, state, finishedAtAdapterPosition, null);
                    }
                });

    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            // we cannot scroll if we don't have views
            return 0;
        }

        final double absRotationAngleInRad = computeRotationAngleInRadBasedOnCurrentState(dy, state);

        if (absRotationAngleInRad == NOT_DEFINED_ROTATION_ANGLE) {
            Log.i(TAG, "HIT INTO NOT_DEFINED_ROTATION_ANGLE");
            return 0;
        }

        final WheelRotationDirection rotationDirection = WheelRotationDirection.of(dy);

        rotateWheel(absRotationAngleInRad, rotationDirection);
        recycleAndAddSectors(rotationDirection, recycler, state);

        final int resultSwipeDistanceAbs = (int) Math.round(fromWheelRotationAngleToTraveledDistance(absRotationAngleInRad));
        logI(
                "scrollVerticallyBy() " +
                "dy [" + dy + "], " +
                "resultSwipeDistanceAbs [" + resultSwipeDistanceAbs + "], " +
                "rotationAngleInDegree [" + WheelComputationHelper.radToDegree(absRotationAngleInRad) + "]"
        );
        return rotationDirection == WheelRotationDirection.Anticlockwise ?
                resultSwipeDistanceAbs : -resultSwipeDistanceAbs;
    }

    /**
     * Transforms swipe gesture's travelled distance {@code scrollDelta} into relevant
     * wheel rotation angle.
     */
    private double fromTraveledDistanceToWheelRotationAngle(int scrollDelta) {
        final int outerDiameter = 2 * wheelConfig.getOuterRadius();
        final double asinArg = Math.abs(scrollDelta) / (double) outerDiameter;
        return Math.asin(asinArg);
    }

    private double fromWheelRotationAngleToTraveledDistance(double rotationAngleInRad) {
        final int outerDiameter = 2 * wheelConfig.getOuterRadius();
        return outerDiameter * Math.sin(rotationAngleInRad);
    }

    private void recycleAndAddSectors(WheelRotationDirection rotationDirection,
                                      RecyclerView.Recycler recycler,
                                      RecyclerView.State state) {

            topSubWheelLayouter.recycleAndAddSectors(rotationDirection, recycler, state);
        // TODO: 03.02.2016 uncomment when implementation will be added
//            bottomSubWheelLayouter.recycleAndAddSectors(rotationDirection, recycler, state);
    }



    private void rotateWheel(double rotationAngle, WheelRotationDirection rotationDirection) {
        topSubWheelLayouter.rotateSubWheel(rotationAngle, rotationDirection);
        bottomSubWheelLayouter.rotateSubWheel(rotationAngle, rotationDirection);
    }

    /**
     * Anticlockwise rotation will correspond to positive return type.
     */
    private double computeRotationAngleInRadBasedOnCurrentState(int dy, RecyclerView.State state) {
        final WheelRotationDirection rotationDirection = WheelRotationDirection.of(dy);
        final double angleToRotate = fromTraveledDistanceToWheelRotationAngle(dy);

        return rotationDirection == WheelRotationDirection.Anticlockwise ?
                    computeRotationAngleInRadForAnticlockwiseRotation(state, angleToRotate) :
                    computeRotationAngleInRadForClockwiseRotation(angleToRotate);
    }

    private double computeRotationAngleInRadForAnticlockwiseRotation(RecyclerView.State state, double angleToRotate) {
        final View referenceChild = getChildClosestToBottom();
        final LayoutParams refChildLp = (LayoutParams) referenceChild.getLayoutParams();
        final int extraChildrenCount = state.getItemCount() - 1 - getPosition(referenceChild);
        final double lastSectorBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(refChildLp.anglePositionInRad);

        double res = NOT_DEFINED_ROTATION_ANGLE;

        // compute available space
        if (extraChildrenCount == 0) { // is last child
            // if last sector's bottom edge outside bottom limit - only scroll this extra space
            // TODO: 15.12.2015 replace with isBottomBoundsReached()
            if (wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge > 0) {
                res = Math.min(
                        angleToRotate,
                        wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge
                );
            }
        } else if (extraChildrenCount > 0) {
            res = Math.min(angleToRotate, wheelConfig.getAngularRestrictions().getSectorAngleInRad() * extraChildrenCount);
        }

        return res;
    }

    private double computeRotationAngleInRadForClockwiseRotation(double angleToRotate) {
        final View referenceChild = getChildClosestToTop();
        final LayoutParams refChildLp = (LayoutParams) referenceChild.getLayoutParams();
        final int extraChildrenCount = getPosition(referenceChild);
        final double firstSectorTopEdge = refChildLp.anglePositionInRad;

        double res = NOT_DEFINED_ROTATION_ANGLE;

        // first top sector goes outside top edge
        if (extraChildrenCount == 0) {
            if (firstSectorTopEdge - computationHelper.getWheelLayoutStartAngleInRad() > 0) {
                res = Math.min(
                        angleToRotate,
                        firstSectorTopEdge - computationHelper.getWheelLayoutStartAngleInRad()
                );
            }
        } else if (extraChildrenCount > 0) {
            res = Math.min(angleToRotate, wheelConfig.getAngularRestrictions().getSectorAngleInRad() * extraChildrenCount);
        }

        return res;
    }

    private boolean isBottomBoundsReached() {
        View lastChild = getChildClosestToBottom();
        LayoutParams lastChildLp = (LayoutParams) lastChild.getLayoutParams();
        final double lastSectorBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(lastChildLp.anglePositionInRad);

        return wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge <= 0;
    }

    private void logI(String message) {
        if (IS_LOG_ACTIVATED) {
            if (IS_FILTER_LOG_BY_METHOD_NAME && isMessageContainsAllowedMethod(message)) {
                Log.i(TAG, message);
            }
        }
    }

    View getChildClosestToBottom() {
        return getChildAt(getChildCount() - 1);
    }

    View getChildClosestToTop() {
        return getChildAt(0);
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    public static final class LayoutParams extends RecyclerView.LayoutParams {

        /**
         * Defines middle (sector's wrapper view half height) edge sector's position on circle.
         * Effectively it equals to view's rotation angle.
         */
        public double anglePositionInRad;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }
    }
}
