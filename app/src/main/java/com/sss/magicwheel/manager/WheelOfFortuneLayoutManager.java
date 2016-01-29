package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.entity.CircleConfig;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
public final class WheelOfFortuneLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = WheelOfFortuneLayoutManager.class.getCanonicalName();

    private final CircleConfig circleConfig;
    private final WheelComputationHelper computationHelper;

    public WheelOfFortuneLayoutManager() {
        this.computationHelper = WheelComputationHelper.getInstance();
        this.circleConfig = computationHelper.getCircleConfig();
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        removeAndRecycleAllViews(recycler);
        recycler.clear();
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

        final double sectorAngleInRad = circleConfig.getAngularRestrictions().getSectorAngleInRad();
        final double bottomLimitAngle = circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad();

        double layoutAngle = computationHelper.getLayoutStartAngle();
        int childPos = 0;
        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
            setupViewForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }
    }

    private void setupViewForPosition(RecyclerView.Recycler recycler, int positionIndex, double angularPosition, boolean isAddViewToBottom) {
        final WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) recycler.getViewForPosition(positionIndex);
        measureBigWrapperView(bigWrapperView);

        RectF wrViewCoordsInCircleSystem = computationHelper.getWrapperViewCoordsInCircleSystem(bigWrapperView.getMeasuredWidth());
        RectF wrTransformedCoords = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleConfig.getCircleCenterRelToRecyclerView(),
                wrViewCoordsInCircleSystem
        );

        bigWrapperView.layout(
                (int) wrTransformedCoords.left, (int) wrTransformedCoords.top,
                (int) wrTransformedCoords.right, (int) wrTransformedCoords.bottom
        );

        alignBigWrapperViewByAngle(bigWrapperView, -angularPosition);

        LayoutParams lp = (LayoutParams) bigWrapperView.getLayoutParams();
        lp.anglePositionInRad = angularPosition;

        if (isAddViewToBottom) {
            addView(bigWrapperView);
        } else {
            addView(bigWrapperView, 0);
        }
    }

    private void measureBigWrapperView(View bigWrapperView) {
        final int viewWidth = circleConfig.getOuterRadius();
        // big wrapper view has the same height as the sector wrapper view
        final int viewHeight = computationHelper.getSectorWrapperViewHeight();

        final int childWidthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int childHeightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
        bigWrapperView.measure(childWidthSpec, childHeightSpec);
    }

    private void alignBigWrapperViewByAngle(View bigWrapperView, double angleAlignToInRad) {
        bigWrapperView.setPivotX(0);
        bigWrapperView.setPivotY(bigWrapperView.getMeasuredHeight() / 2);
        float angleInDegree = (float) WheelComputationHelper.radToDegree(angleAlignToInRad);
        // TODO: 16.12.2015 ugly bug fix related to central view disappearing while scrolling
        if (angleInDegree > -0.1f && angleInDegree < 0.1f) {
            angleInDegree = 0;
        }
        bigWrapperView.setRotation(angleInDegree);

//        final String text = ((WheelBigWrapperView) bigWrapperView).getText();
//        Log.e(TAG, "alignBigWrapperViewByAngle text [" + text + "], angleInDegree [" + angleInDegree + "]");
    }


    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int childCount = getChildCount();
        Log.i(TAG, "scrollVerticallyBy dy [" + dy + "], childCount [" + childCount + "]");
        if (childCount == 0) {
            // we cannot scroll if we don't have views
            return 0;
        }

        final double rotationAngle = computeRotationAngleBasedOnCurrentState(dy, state);

        if (rotationAngle == Double.MIN_VALUE) {
            return 0;
        }

        final WheelRotationDirection rotationDirection = WheelRotationDirection.of(dy);

        rotateWheel(rotationAngle, rotationDirection);
        performRecycling(rotationDirection, recycler, state);

//        Log.i(TAG, "scrollVerticallyBy() rotationAngle [" + WheelUtils.radToDegree(rotationAngle) + "]");
//        Log.i(TAG, "Views count in layout [" + getChildCount() + "]");

        final int resultSwipeDistanceAbs = (int) Math.round(circleConfig.getInnerRadius() * Math.sin(Math.abs(rotationAngle)));
        return rotationDirection == WheelRotationDirection.Anticlockwise ?
                resultSwipeDistanceAbs : -resultSwipeDistanceAbs;
    }

    private void performRecycling(WheelRotationDirection rotationDirection,
                                  RecyclerView.Recycler recycler,
                                  RecyclerView.State state) {
        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
            recycleViewsFromTopIfNeeded(recycler);
            addViewsToBottomIfNeeded(recycler, state);
        } else if (rotationDirection == WheelRotationDirection.Clockwise) {
            recycleViewsFromBottomIfNeeded(recycler);
            addViewsToTopIfNeeded(recycler, state);
        } else {
            throw new IllegalArgumentException("...");
        }
    }

    private void addViewsToTopIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View firstChild = getChildClosestToTop();
        final LayoutParams firstChildLp = (LayoutParams) firstChild.getLayoutParams();

        final double sectorAngleInRad = circleConfig.getAngularRestrictions().getSectorAngleInRad();

        double layoutAngle = firstChildLp.anglePositionInRad + sectorAngleInRad;
        int childPos = getPosition(firstChild) - 1;
        while (layoutAngle < computationHelper.getLayoutStartAngle() && childPos >= 0) {
            Log.i(TAG, "addViewsToTopIfNeeded() " +
                            "layoutAngle [" + WheelComputationHelper.radToDegree(layoutAngle) + "], " +
                            "childPos [" + childPos + "]"
            );
            setupViewForPosition(recycler, childPos, layoutAngle, false);
            layoutAngle += sectorAngleInRad;
            childPos--;
        }
    }

    private void recycleViewsFromBottomIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            final LayoutParams childLp = (LayoutParams) getChildAt(i).getLayoutParams();
            if (childLp.anglePositionInRad < circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad()) {
                removeAndRecycleViewAt(i, recycler);
                Log.e(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    private void addViewsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View lastChild = getChildClosestToBottom();
        final LayoutParams lastChildLp = (LayoutParams) lastChild.getLayoutParams();

        final double sectorAngleInRad = circleConfig.getAngularRestrictions().getSectorAngleInRad();
        final double bottomLimitAngle = circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad();

        double layoutAngle = computationHelper.getSectorAngleBottomEdge(lastChildLp.anglePositionInRad);
        int childPos = getPosition(lastChild) + 1;
        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
//            Log.e(TAG, "addViewsToBottomIfNeeded() " +
//                    "layoutAngle [" + WheelUtils.radToDegree(layoutAngle) + "], " +
//                    "childPos [" + childPos + "]"
//            );
            setupViewForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }
    }

    private void recycleViewsFromTopIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = 0; i < getChildCount(); i++) {
            final LayoutParams childLp = (LayoutParams) getChildAt(i).getLayoutParams();
            if (childLp.anglePositionInRad > computationHelper.getLayoutStartAngle()) {
                removeAndRecycleViewAt(i, recycler);
                Log.i(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    // TODO: 15.12.2015 same code snippets - remove code duplication
    private void rotateWheel(double rotationAngle, WheelRotationDirection rotationDirection) {
        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                final LayoutParams childLp = (LayoutParams) child.getLayoutParams();
                childLp.anglePositionInRad += rotationAngle;
                child.setLayoutParams(childLp);
                alignBigWrapperViewByAngle(child, -childLp.anglePositionInRad);
            }
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                final LayoutParams childLp = (LayoutParams) child.getLayoutParams();
                childLp.anglePositionInRad -= rotationAngle;
                child.setLayoutParams(childLp);
                alignBigWrapperViewByAngle(child, -childLp.anglePositionInRad);
            }
        }
    }

    /**
     * Anticlockwise rotation will correspond to positive return type.
     */
    private double computeRotationAngleBasedOnCurrentState(int dy, RecyclerView.State state) {
        final WheelRotationDirection rotationDirection = WheelRotationDirection.of(dy);
        final double angleToRotate = toWheelRotationAngle(dy);

        final View referenceChild;
        final LayoutParams refChildLp;
        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
            referenceChild = getChildClosestToBottom();
            refChildLp = (LayoutParams) referenceChild.getLayoutParams();
            final int extraChildrenCount = state.getItemCount() - 1 - getPosition(referenceChild);
            final double lastSectorBottomEdge = computationHelper.getSectorAngleBottomEdge(refChildLp.anglePositionInRad);

            // compute available space
            if (extraChildrenCount == 0) { // is last child
                // last sector's bottom edge outside bottom limit - only scroll this extra space
                // TODO: 15.12.2015 replace with isBottomBoundsReached()
                if (circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge > 0) {
                    return Math.min(
                            angleToRotate,
                            circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge
                    );
                } else {
                    return Double.MIN_VALUE;
                }
            } else if (extraChildrenCount > 0) {
                return Math.min(angleToRotate, circleConfig.getAngularRestrictions().getSectorAngleInRad() * extraChildrenCount);
            } else {
                return Double.MIN_VALUE;
            }
        } else {
            referenceChild = getChildClosestToTop();
            refChildLp = (LayoutParams) referenceChild.getLayoutParams();
            final int extraChildrenCount = getPosition(referenceChild);
            final double firstSectorTopEdge = refChildLp.anglePositionInRad;

            // first top sector goes outside top edge
            if (extraChildrenCount == 0) {
                if (firstSectorTopEdge - computationHelper.getLayoutStartAngle() > 0) {
                    return Math.min(
                            angleToRotate,
                            firstSectorTopEdge - computationHelper.getLayoutStartAngle()
                    );
                } else {
                    return Double.MIN_VALUE;
                }
            } else if (extraChildrenCount > 0) {
                return Math.min(angleToRotate, circleConfig.getAngularRestrictions().getSectorAngleInRad() * extraChildrenCount);
            } else {
                return Double.MIN_VALUE;
            }
        }
    }

    private boolean isBottomBoundsReached() {
        View lastChild = getChildClosestToBottom();
        LayoutParams lastChildLp = (LayoutParams) lastChild.getLayoutParams();
        final double lastSectorBottomEdge = computationHelper.getSectorAngleBottomEdge(lastChildLp.anglePositionInRad);

        return circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge <= 0;
    }

    /**
     * Transforms swipe gesture's travelled distance {@code scrollDelta} into relevant
     * wheel rotation angle.
     */
    private double toWheelRotationAngle(int scrollDelta) {
        final double scrollDeltaAbsValue = (double) Math.abs(scrollDelta);
        return Math.asin(scrollDeltaAbsValue / circleConfig.getInnerRadius());
    }

    private View getChildClosestToBottom() {
        return getChildAt(getChildCount() - 1);
    }

    private View getChildClosestToTop() {
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
