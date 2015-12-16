package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.LinearClipData;

import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = WheelLayoutManager.class.getCanonicalName();

    private final Context context;
    private final CircleConfig circleConfig;
    private final ComputationHelper computationHelper;

    private final LayoutState mLayoutState;

    public WheelLayoutManager(Context context, CircleConfig circleConfig) {
        this.context = context;
        this.circleConfig = circleConfig;
        this.mLayoutState = new LayoutState();
        this.computationHelper = new ComputationHelper(circleConfig);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        removeAndRecycleAllViews(recycler);

        final double sectorAngleInRad = circleConfig.getAngularRestrictions().getSectorAngleInRad();
        int childPos = 0;
        double layoutAngle = circleConfig.getAngularRestrictions().getTopEdgeAngleRestrictionInRad();
        final double endAngle = circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad();
        while (layoutAngle >= endAngle && childPos < state.getItemCount()) {
            addViewForPosition(recycler, childPos, -layoutAngle);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }
    }

    // TODO: 10.12.2015 consider replacing to layoutCircleChunk
    private void addViewForPosition(RecyclerView.Recycler recycler, int position, double angleInRad) {
        final WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) recycler.getViewForPosition(position);

        measureBigWrapperView(bigWrapperView);

        Rect wrViewCoordsInCircleSystem = getWrapperViewCoordsInCircleSystem(bigWrapperView.getMeasuredWidth());
//        Log.e(TAG, "Before transformation [" + wrViewCoordsInCircleSystem.toShortString() + "]");

        Rect wrTransformedCoords = WheelUtils.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleConfig.getCircleCenterRelToRecyclerView(),
                wrViewCoordsInCircleSystem
        );

//        Log.e(TAG, "After transformation " + wrTransformedCoords.toShortString());
//        Log.e(TAG, "Sector wrapper width [" + computationHelper.getSectorWrapperViewWidth() + "]");

        bigWrapperView.layout(wrTransformedCoords.left, wrTransformedCoords.top, wrTransformedCoords.right, wrTransformedCoords.bottom);

        rotateBigWrapperViewToAngle(bigWrapperView, angleInRad);

        bigWrapperView.setSectorWrapperConfig(
                computationHelper.getSectorWrapperViewWidth(),
                computationHelper.getSectorWrapperViewHeight(),
                computationHelper.createSectorClipArea()
        );

        LayoutParams lp = (LayoutParams) bigWrapperView.getLayoutParams();
        lp.rotationAngleInRad = -angleInRad;

        addView(bigWrapperView);
    }

    private Rect getWrapperViewCoordsInCircleSystem(int wrapperViewWidth) {
        final int topEdge = computationHelper.getSectorWrapperViewHeight() / 2;
        return new Rect(0, topEdge, wrapperViewWidth, -topEdge);
    }

    private void rotateBigWrapperViewToAngle(View bigWrapperView, double angleToRotateInRad) {
        bigWrapperView.setPivotX(0);
        bigWrapperView.setPivotY(bigWrapperView.getMeasuredHeight() / 2);
        bigWrapperView.setRotation((float) WheelUtils.radToDegree(angleToRotateInRad));
    }

    private void measureBigWrapperView(View bigWrapperView) {
        final int viewWidth = circleConfig.getOuterRadius();
        // big wrapper view has the same height as the sector wrapper view
        final int viewHeight = computationHelper.getSectorWrapperViewHeight();

        final int childWidthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int childHeightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
        bigWrapperView.measure(childWidthSpec, childHeightSpec);
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        double angleToScrollInRad = Math.asin((double) Math.abs(dy) / circleConfig.getOuterRadius());
//        Log.e(TAG, "dy = [" + dy + "], outerRadius [" + circleConfig.getOuterRadius() + "], " +
//                "rotateCircleByAngle rotationAngle [" + WheelUtils.radToDegree(angleToScrollInRad) + "]");
        return rotateCircleByAngle(angleToScrollInRad, CircleRotationDirection.of(dy), recycler, state);
    }

    private int rotateCircleByAngle(double angleToScrollInRad,
                                    CircleRotationDirection circleRotationDirection,
                                    RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getChildCount() == 0 || angleToScrollInRad == 0) {
            return 0;
        }
        mLayoutState.mRecycle = true;
        updateLayoutState(angleToScrollInRad, circleRotationDirection, true);

//        final int freeScroll = mLayoutState.mScrollingOffset;

        final double consumedAngle = /*freeScroll +*/ fillCircleLayout(recycler, mLayoutState, state);
        if (consumedAngle < 0) {
            return 0;
        }

        final double actualRotationAngle = angleToScrollInRad > consumedAngle ? consumedAngle : angleToScrollInRad;
//                circleRotationDirection.direction * consumedAngle : circleRotationDirection.direction * angleToScrollInRad;

        doChildrenRotationByAngle(actualRotationAngle, circleRotationDirection);

        // TODO: 07.12.2015 most probably this computation is not correct
        return (int) (circleConfig.getOuterRadius() * Math.sin(actualRotationAngle));
    }

    private void doChildrenRotationByAngle(double angleToScrollInRad, CircleRotationDirection circleRotationDirection) {
        final double scrollAngleDelta = circleRotationDirection == CircleRotationDirection.Anticlockwise ?
                - angleToScrollInRad : angleToScrollInRad;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final LayoutParams childLp = (LayoutParams) child.getLayoutParams();
            childLp.rotationAngleInRad += angleToScrollInRad;

            double resAngle = WheelUtils.degreeToRadian(child.getRotation()) + scrollAngleDelta;
            rotateBigWrapperViewToAngle(child, resAngle);
        }
    }


    private void updateLayoutState(double angleToScrollInRad, CircleRotationDirection circleRotationDirection,
                                   /*int layoutDirection, int requiredSpace,*/ boolean canUseExistingSpace) {

        mLayoutState.mRotationDirection = circleRotationDirection;
        double fastScrollSpace = LayoutState.FAST_SCROLL_ANGLE_NOT_DEFINED;
        if (circleRotationDirection == CircleRotationDirection.Anticlockwise) {
            // get the first child in the direction we are going to
            final View child = getChildClosestToBottom();
            final LayoutParams childLp = (LayoutParams) child.getLayoutParams();

            mLayoutState.mCurrentPosition = getPosition(child) + 1;
            // here we need to calculate angular position of the sector's bottom edge because in LP
            // we remember only top edge angular pos.
            mLayoutState.mAngleToStartLayout = childLp.rotationAngleInRad - circleConfig.getAngularRestrictions().getSectorAngleInRad();

            // TODO: 10.12.2015 move outside the embracing if statement
            // calculate how much we can scroll without adding new children (independent of layout)
            if (isEdgeLimitReached(mLayoutState.mAngleToStartLayout, circleRotationDirection)) {
                fastScrollSpace = Math.abs(mLayoutState.mAngleToStartLayout) - Math.abs(circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad());
            }
        } else {

            final View child = getChildClosestToTop();
            final LayoutParams childLp = (LayoutParams) child.getLayoutParams();

            mLayoutState.mCurrentPosition = getPosition(child) - 1;

            mLayoutState.mAngleToStartLayout = childLp.rotationAngleInRad + circleConfig.getAngularRestrictions().getSectorAngleInRad();

            // TODO: 10.12.2015 move outside the embracing else statement
            if (isEdgeLimitReached(mLayoutState.mAngleToStartLayout, circleRotationDirection)) {
                fastScrollSpace = Math.abs(mLayoutState.mAngleToStartLayout) - Math.abs(circleConfig.getAngularRestrictions().getTopEdgeAngleRestrictionInRad());
            }
        }

        mLayoutState.mRequestedScrollAngle = angleToScrollInRad;
        mLayoutState.mFastScrollAngleInRad = fastScrollSpace;

//        if (canUseExistingSpace) {
//            mLayoutState.mAvailable -= fastScrollSpace;
//        }
//        mLayoutState.mScrollingOffset = fastScrollSpace;
    }


    private View getChildClosestToBottom() {
        return getChildAt(getChildCount() - 1);
    }

    private View getChildClosestToTop() {
        return getChildAt(0);
    }

    /**
     * Fills the given layout, defined by configured beforehand layoutState.
     */
    private double fillCircleLayout(RecyclerView.Recycler recycler, LayoutState layoutState, RecyclerView.State state) {

        double angleToCompensate = layoutState.mRequestedScrollAngle;
        double accumulatedRecycleAngle = 0;
        // takes into account fast scrolling region
        double virtualStartLayoutAngle = layoutState.mAngleToStartLayout;

        // examine if we can fill layout possibly without creating additional sectors
        if (layoutState.isFastScrollDefined()) {
            angleToCompensate -= layoutState.mFastScrollAngleInRad;
            accumulatedRecycleAngle += layoutState.mFastScrollAngleInRad;
            virtualStartLayoutAngle -= layoutState.mFastScrollAngleInRad;
        }

        // we don't need to create new sectors if we can simply fast scroll and this fast scrolling
        // compensates requested angle
        while (angleToCompensate > 0
                && layoutState.hasMore(state)
                && !isEdgeLimitReached(virtualStartLayoutAngle, layoutState.mRotationDirection)
                && !Double.isNaN(angleToCompensate)) { // todo: judicious

            Log.e(TAG, layoutState.toString());

            final double consumedAngle = layoutCircleChunk(recycler, state, layoutState);
            final double angleDelta = layoutState.mRotationDirection == CircleRotationDirection.Anticlockwise ?
                    -consumedAngle : consumedAngle;

            layoutState.mAngleToStartLayout += angleDelta;
            virtualStartLayoutAngle += angleDelta;

            angleToCompensate -= Math.abs(consumedAngle);

            accumulatedRecycleAngle += Math.abs(consumedAngle);

            recycleUnnecessaryViews(recycler, accumulatedRecycleAngle, layoutState.mRotationDirection);
        }

        return accumulatedRecycleAngle;
    }

    private boolean isEdgeLimitReached(double angleToExamine, CircleRotationDirection rotationDirection) {
//        Log.e(TAG, "angleToExamine [" + angleToExamine + "], " +
//                "bottomEdge [" + circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad() + "]");
        if (rotationDirection == CircleRotationDirection.Anticlockwise) {
            return angleToExamine < circleConfig.getAngularRestrictions().getBottomEdgeAngleRestrictionInRad();
        } else {
            return angleToExamine > circleConfig.getAngularRestrictions().getTopEdgeAngleRestrictionInRad();
        }
    }

    private double layoutCircleChunk(RecyclerView.Recycler recycler, RecyclerView.State state, LayoutState layoutState) {

        View view = layoutState.next(recycler);

        WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) view;

        measureBigWrapperView(view);

        Rect wrViewCoordsInCircleSystem = getWrapperViewCoordsInCircleSystem(view.getMeasuredWidth());

        Rect wrTransformedCoords = WheelUtils.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleConfig.getCircleCenterRelToRecyclerView(),
                wrViewCoordsInCircleSystem
        );

        view.layout(
                wrTransformedCoords.left,
                wrTransformedCoords.top,
                wrTransformedCoords.right,
                wrTransformedCoords.bottom
        );

        final double rotationAngle = layoutState.mRotationDirection == CircleRotationDirection.Anticlockwise ?
                -layoutState.mAngleToStartLayout : layoutState.mAngleToStartLayout;

        rotateBigWrapperViewToAngle(view, rotationAngle);

        bigWrapperView.setSectorWrapperConfig(
                computationHelper.getSectorWrapperViewWidth(),
                computationHelper.getSectorWrapperViewHeight(),
                computationHelper.createSectorClipArea()
        );

        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.rotationAngleInRad = layoutState.mAngleToStartLayout;

        if (layoutState.mRotationDirection == CircleRotationDirection.Anticlockwise) {
            addView(view);
        } else {
            addView(view, 0);
        }

        return circleConfig.getAngularRestrictions().getSectorAngleInRad();
    }

    private void recycleUnnecessaryViews(RecyclerView.Recycler recycler,
                                         double accumulatedRecycleAngle,
                                         CircleRotationDirection rotationDirection) {
        if (accumulatedRecycleAngle < 0) {
            Log.e(TAG, "No recycling for negative accumulated angle [" + accumulatedRecycleAngle + "]");
            return;
        }

        if (rotationDirection == CircleRotationDirection.Anticlockwise) {
            recycleViewsFromTop(recycler, accumulatedRecycleAngle);
        } else {
            recycleViewsFromBottom(recycler, accumulatedRecycleAngle);
        }
    }

    private void recycleViewsFromTop(RecyclerView.Recycler recycler, double accumulatedRecycleAngle) {
        // views that fits into the recycling area have to be recycled
        final double recyclingAreaStartEdge = circleConfig.getAngularRestrictions().getTopEdgeAngleRestrictionInRad() - accumulatedRecycleAngle;
        final int childCount = getChildCount();

        Log.e(TAG, "recycleViewsFromTop() childCount [" + childCount + "] recyclingAreaStartEdge [" + WheelUtils.radToDegree(recyclingAreaStartEdge) + "]");

        for (int i = childCount - 1; i >= 0; i--) {
            LayoutParams childLp = (LayoutParams) getChildAt(i).getLayoutParams();
            final double bottomSectorEdgeAngle = childLp.rotationAngleInRad - circleConfig.getAngularRestrictions().getSectorAngleInRad();
            Log.e(TAG, "recycleViewsFromTop() bottomSectorEdgeAngle [" + WheelUtils.radToDegree(bottomSectorEdgeAngle) + "]");
            // when bottom edge fit inside recycle area
            if (bottomSectorEdgeAngle > recyclingAreaStartEdge) {
                recycleChildren(recycler, 0, i);
                return;
            }
        }
    }

    private void recycleViewsFromBottom(RecyclerView.Recycler recycler, double accumulatedRecycleAngle) {
//        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Recycles children between given indices.
     *
     * @param startIndex inclusive
     * @param endIndex   exclusive
     */
    private void recycleChildren(RecyclerView.Recycler recycler, int startIndex, int endIndex) {
        Log.e(TAG, "Recycler children between [" + startIndex + "; " + endIndex + "]");
        if (startIndex == endIndex) {
            return;
        }

        if (endIndex > startIndex) {
            for (int i = endIndex - 1; i >= startIndex; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        } else {
            for (int i = startIndex; i > endIndex; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
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

    private enum CircleRotationDirection {

        Clockwise(-1), Anticlockwise(1);

        private final int direction;

        /**
         * When we move from circle's HEAD to TAIL (anticlockwise) - we increase
         * adapter position, and decrease it when scrolling clockwise.
         */
        private final int adapterPositionIncrementation;

        CircleRotationDirection(int directionSignum) {
            this.direction = directionSignum;
            this.adapterPositionIncrementation = directionSignum;
        }

        public static CircleRotationDirection of(int directionAsInt) {
            return directionAsInt < 0 ? Clockwise : Anticlockwise;
        }
    }

    /**
     * Helper class that keeps temporary state while {LayoutManager} is filling out the empty
     * space.
     */
    private static class LayoutState {

        final static double FAST_SCROLL_ANGLE_NOT_DEFINED = Double.MIN_VALUE;

        /**
         * We may not want to recycle children in some cases (e.g. layout)
         */
        boolean mRecycle = true;

        double mRequestedScrollAngle;
        CircleRotationDirection mRotationDirection;

        /**
         * Starting from this angle new children will be added to circle.
         */
        double mAngleToStartLayout;

        double mFastScrollAngleInRad;

        /**
         * Current position on the adapter to get the next item.
         */
        int mCurrentPosition;

        /**
         * Used when LayoutState is constructed in a scrolling state.
         * It should be set the amount of scrolling we can make without creating a new view.
         * Settings this is required for efficient view recycling.
         */
        @Deprecated
        int mRecycleSweepAngle;

        /**
         * When LLM needs to layout particular views, it sets this list in which case, LayoutState
         * will only return views from this list and return null if it cannot find an item.
         */
        @Deprecated
        List<RecyclerView.ViewHolder> mScrapList = null;

        public boolean isFastScrollDefined() {
            return mFastScrollAngleInRad != FAST_SCROLL_ANGLE_NOT_DEFINED;
        }

        /**
         * @return true if there are more items in the data adapter
         */
        boolean hasMore(RecyclerView.State state) {
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }

        /**
         * Gets the view for the next element that we should layout.
         * Also updates current item index to the next item, based on {@code mItemFetchDirection}
         *
         * @return The next element that we should layout.
         */
        View next(RecyclerView.Recycler recycler) {
            if (mScrapList != null) {
                return nextViewFromScrapList();
            }
            final View view = recycler.getViewForPosition(mCurrentPosition);
            mCurrentPosition += mRotationDirection.adapterPositionIncrementation;
            return view;
        }

        /**
         * Returns the next item from the scrap list.
         * <p>
         * Upon finding a valid VH, sets current item position to VH.itemPosition + mItemFetchDirection
         *
         * @return View if an item in the current position or direction exists if not null.
         */
        private View nextViewFromScrapList() {
            final int size = mScrapList.size();
            for (int i = 0; i < size; i++) {
                final View view = mScrapList.get(i).itemView;
                final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
                if (lp.isItemRemoved()) {
                    continue;
                }
                if (mCurrentPosition == lp.getViewLayoutPosition()) {
                    assignPositionFromScrapList(view);
                    return view;
                }
            }
            return null;
        }

        public void assignPositionFromScrapList() {
            assignPositionFromScrapList(null);
        }

        public void assignPositionFromScrapList(View ignore) {
            final View closest = nextViewInLimitedList(ignore);
            if (closest == null) {
                mCurrentPosition = NO_POSITION;
            } else {
                mCurrentPosition = ((RecyclerView.LayoutParams) closest.getLayoutParams())
                        .getViewLayoutPosition();
            }
        }

        public View nextViewInLimitedList(View ignore) {
            int size = mScrapList.size();
            View closest = null;
            int closestDistance = Integer.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                View view = mScrapList.get(i).itemView;
                final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
                if (view == ignore || lp.isItemRemoved()) {
                    continue;
                }
                final int distance = (lp.getViewLayoutPosition() - mCurrentPosition) * mRotationDirection.adapterPositionIncrementation;
                if (distance < 0) {
                    continue; // item is not in current direction
                }
                if (distance < closestDistance) {
                    closest = view;
                    closestDistance = distance;
                    if (distance == 0) {
                        break;
                    }
                }
            }
            return closest;
        }

        @Override
        public String toString() {
            return "LayoutState{" +
                    "mFastScrollAngle=" + WheelUtils.radToDegree(mFastScrollAngleInRad) +
                    ", mRequestedScrollAngle=" + WheelUtils.radToDegree(mRequestedScrollAngle) +
                    ", mAngleToStartLayout=" + WheelUtils.radToDegree(mAngleToStartLayout) +
                    ", mCurrentPosition=" + mCurrentPosition +
                    '}';
        }
    }

    private static final class ComputationHelper {

        private static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

        private final CircleConfig circleConfig;

        private int sectorWrapperViewWidth = NOT_DEFINED_VALUE;
        private int sectorWrapperViewHeight = NOT_DEFINED_VALUE;

        private LinearClipData sectorClipData;

        public ComputationHelper(CircleConfig circleConfig) {
            this.circleConfig = circleConfig;
        }

        /**
         * Width of the view which wraps the sector.
         */
        public int getSectorWrapperViewWidth() {
            if (sectorWrapperViewWidth == NOT_DEFINED_VALUE) {
                sectorWrapperViewWidth = computeViewWidth();
            }
            return sectorWrapperViewWidth;
        }

        private int computeViewWidth() {
            final double delta = circleConfig.getInnerRadius() * Math.cos(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
            return (int) (circleConfig.getOuterRadius() - delta);
        }

        /**
         * Height of the view which wraps the sector.
         */
        public int getSectorWrapperViewHeight() {
            if (sectorWrapperViewHeight == NOT_DEFINED_VALUE) {
                sectorWrapperViewHeight = computeViewHeight();
            }
            return sectorWrapperViewHeight;
        }

        private int computeViewHeight() {
            final double halfHeight = circleConfig.getOuterRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
            return (int) (2 * halfHeight);
        }


        public LinearClipData createSectorClipArea() {
            if (sectorClipData == null) {
                final int viewWidth = getSectorWrapperViewWidth();
                final int viewHalfHeight = getSectorWrapperViewHeight() / 2;

                final double leftBaseDelta = circleConfig.getInnerRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
                final double rightBaseDelta = circleConfig.getOuterRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);

                final CoordinatesHolder first = CoordinatesHolder.ofRect(0, viewHalfHeight + leftBaseDelta);
                final CoordinatesHolder third = CoordinatesHolder.ofRect(0, viewHalfHeight - leftBaseDelta);

                final CoordinatesHolder second = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight + rightBaseDelta);
                final CoordinatesHolder forth = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight - rightBaseDelta);

                sectorClipData = new LinearClipData(first, second, third, forth);
            }

            return sectorClipData;
        }
    }

    public static final class LayoutParams extends RecyclerView.LayoutParams {

        /**
         * Rotation angle defined by top edge of the sector.
         */
        double rotationAngleInRad;

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
