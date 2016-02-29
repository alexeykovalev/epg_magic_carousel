package com.sss.magicwheel.coversflow.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.sss.magicwheel.App;
import com.sss.magicwheel.coversflow.CoversFlowAdapter;
import com.sss.magicwheel.coversflow.CoversFlowListMeasurements;
import com.sss.magicwheel.coversflow.HorizontalCoversFlowEdgeDecorator;
import com.sss.magicwheel.coversflow.entity.CoverEntity;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class HorizontalCoversFlowView extends RecyclerView {

    private static final int HORIZONTAL_SPACING_IN_DP = 15;
    private static final int SCALING_ANIMATION_DURATION = 300;

    private static class ScrollingData {

        private static ScrollingData Instance = new ScrollingData();

        private int absScrollingDistance;
        private boolean isSwipeToLeft;

        private ScrollingData() {
        }

        public static ScrollingData update(int deltaX) {
            Instance.isSwipeToLeft = deltaX >= 0;
            Instance.absScrollingDistance = Math.abs(deltaX);
            return Instance;
        }

        public boolean isSwipeToLeft() {
            return Instance.isSwipeToLeft;
        }
    }

    private class CoverZoomScrollListener extends OnScrollListener {

        @Deprecated
        private boolean isFirstScrolling = true;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                scrollToFullySelectCover();
//                resizeCovers();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (isFirstScrolling) {
                scrollToFullySelectCover();
                isFirstScrolling = false;
            }
            ScrollingData.update(dx);
            resizeCovers();
        }
    }

    private final CoversFlowListMeasurements coversFlowMeasurements;

    public HorizontalCoversFlowView(Context context) {
        this(context, null);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        coversFlowMeasurements = CoversFlowListMeasurements.getInstance();
        init(context);
        addOnScrollListener(new CoverZoomScrollListener());
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new CoversFlowAdapter(context, Collections.<CoverEntity>emptyList(), new CoversFlowAdapter.ICoverClickListener() {
            @Override
            public void onCoverClick(HorizontalCoverView coverView, CoverEntity coverEntity) {
                selectCoverOnClick(coverView, coverEntity);
            }
        }));

        addItemDecoration(new HorizontalCoversFlowEdgeDecorator(context));
        setupCoversHorizontalSpacing((int) App.dpToPixels(HORIZONTAL_SPACING_IN_DP));
    }

    private void setupCoversHorizontalSpacing(final int horizontalSpacingValue) {
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                final int coverAdapterPosition = parent.getChildAdapterPosition(view);
                // take into account right offset fake view
                final boolean isLastItem = coverAdapterPosition == (parent.getAdapter().getItemCount() - 2);
                final int hSpacing = (view instanceof HorizontalCoverView) && !isLastItem ? horizontalSpacingValue : 0;
                outRect.set(0, 0, hSpacing, 0);
            }
        });
    }

    public void swapData(List<CoverEntity> coversData) {
        getAdapter().swapData(coversData);
    }

    public void displayWithScalingAnimation() {
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);

        final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, 0.0f, 1.0f);
        final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.0f, 1.0f);

        final LinearInterpolator interpolator = new LinearInterpolator();
        scaleXAnimator.setInterpolator(interpolator);
        scaleXAnimator.setDuration(SCALING_ANIMATION_DURATION);

        scaleYAnimator.setInterpolator(interpolator);
        scaleYAnimator.setDuration(SCALING_ANIMATION_DURATION);

        final AnimatorSet scalingAnimator = new AnimatorSet();
        scalingAnimator.playTogether(scaleXAnimator, scaleYAnimator);
        scalingAnimator.start();
    }

    /**
     * After scrolling of fling gesture has been done we have to fully select
     * nearest to the edge cover.
     */
    private void scrollToFullySelectCover() {
        final HorizontalCoverView coverClosestToResizingEdge = findCoverClosestToResizingEdge();
        if (coverClosestToResizingEdge != null) {
            final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();
            final float childStartX = coverClosestToResizingEdge.getLeft();
            final float offset = edgeLeftPosition - childStartX;

            final float scrollBy = coversFlowMeasurements.getCoverDefaultWidth() / 2 - offset;

            smoothScrollBy((int) scrollBy, 0);
        }
    }

    // TODO: 25.02.2016 refactor this method.
    private HorizontalCoverView findCoverClosestToResizingEdge() {
        final HorizontalCoverView intersectingCover = findCoverIntersectingWithEdge();
        if (intersectingCover != null) {
            return intersectingCover;
        }

        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();

        View closestToEdgeFromLeft = null;
        int i = 0;
        while (i < getChildCount() && (closestToEdgeFromLeft = getChildAt(i)).getRight() <= edgeLeftPosition) {
            i++;
        }

        final boolean isOffsetCover = !(closestToEdgeFromLeft instanceof HorizontalCoverView);

        if (isOffsetCover) {
            final boolean isLeftOffset = getChildAdapterPosition(closestToEdgeFromLeft) == 0;
            final boolean isRightOffset = getChildAdapterPosition(closestToEdgeFromLeft) == (getAdapter().getItemCount() - 1);

            if (isLeftOffset) {
                return (HorizontalCoverView) getChildAt(1);
            }

            if (isRightOffset) {
                return (HorizontalCoverView) getChildAt(getChildCount() - 2);
            }
        }

        if (closestToEdgeFromLeft != null && !isOffsetCover) {
            return (HorizontalCoverView) closestToEdgeFromLeft;
        }

        return null;
    }

    private void selectCoverOnClick(HorizontalCoverView coverView, CoverEntity coverEntity) {
        final HorizontalCoverView intersectingChild = findCoverIntersectingWithEdge();
        float extraWidthToCompensate = intersectingChild != null ?
                (intersectingChild.getWidth() - coversFlowMeasurements.getCoverDefaultWidth()) : 0;

        final float coverXPos = coverView.getLeft()
                + coversFlowMeasurements.getCoverDefaultWidth() / 2
                - extraWidthToCompensate;
        final float resizingEdgePosition = coversFlowMeasurements.getResizingEdgePosition();

        smoothScrollBy((int)(coverXPos - resizingEdgePosition), 0);
    }

    @Override
    public CoversFlowAdapter getAdapter() {
        return (CoversFlowAdapter) super.getAdapter();
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return (LinearLayoutManager) super.getLayoutManager();
    }

    private void resizeCovers() {
        HorizontalCoverView intersectingChild = findCoverIntersectingWithEdge();
        resizeIntersectingChild(intersectingChild);
        restoreOtherChildrenToInitialSize(intersectingChild);
        requestLayout();
    }

    private void resizeIntersectingChild(HorizontalCoverView intersectingChild) {
        if (intersectingChild != null) {
            final double zoomFactor = getChildZoomingFactor(intersectingChild);

            final int maxHeight = getChildMaxHeight();
            final int initialHeight = coversFlowMeasurements.getCoverDefaultHeight();

            double newChildHeight = initialHeight + (maxHeight - initialHeight) * zoomFactor;
            final int newChildHeightAsInt = (int) newChildHeight;

            final int topMarginValue = (getHeight() - newChildHeightAsInt ) / 2;
            final ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) intersectingChild.getLayoutParams();
            lp.height = newChildHeightAsInt;
            lp.width = (int) (newChildHeightAsInt * CoversFlowListMeasurements.COVER_ASPECT_RATIO);
            lp.topMargin = topMarginValue;
        }
    }

    private void restoreOtherChildrenToInitialSize(HorizontalCoverView intersectingChild) {
        for (int i = 0; i < getChildCount(); i++) {
            final View coverView = getChildAt(i);
            final int topMarginValue = (getHeight() - coversFlowMeasurements.getCoverDefaultHeight()) / 2;
            final MarginLayoutParams coverViewLp = (MarginLayoutParams) coverView.getLayoutParams();
            if (intersectingChild != coverView) {
                coverViewLp.height = coversFlowMeasurements.getCoverDefaultHeight();
                coverViewLp.width = coversFlowMeasurements.getCoverDefaultWidth();
                coverViewLp.leftMargin = coversFlowMeasurements.getCoverDefaultMargins().left;
                coverViewLp.topMargin = topMarginValue;
            }
        }
    }

    private int getChildMaxHeight() {
        return getHeight();
    }

    private HorizontalCoverView findCoverIntersectingWithEdge() {
        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final float childLeftX = child.getLeft();
            final float childRightX = childLeftX + child.getWidth();

            final boolean isFakeChild = !(child instanceof HorizontalCoverView);
            if (!isFakeChild && childLeftX <= edgeLeftPosition && childRightX >= edgeLeftPosition) {
                return (HorizontalCoverView) child;
            }
        }

        return null;
    }

    private double getChildZoomingFactor(HorizontalCoverView childToZoom) {
        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();
        final float childStartX = childToZoom.getLeft();
        final float offset = edgeLeftPosition - childStartX;

        final double zoomFactor;
        final int halfChildWidth = coversFlowMeasurements.getCoverDefaultWidth() / 2;
        if (ScrollingData.Instance.isSwipeToLeft()) {
            if (isZoomUp(offset)) {
                zoomFactor = offset / halfChildWidth;
            } else {
                zoomFactor = 1 - (offset - halfChildWidth) / halfChildWidth;
            }
        } else {
            if (isZoomUp(offset)) {
                zoomFactor = 1 - (offset - halfChildWidth) / halfChildWidth;
            } else {
                zoomFactor = offset / halfChildWidth;
            }
        }

        return zoomFactor;
    }

    private boolean isZoomUp(float childOffset) {
        final int childHalfWidth = coversFlowMeasurements.getCoverDefaultWidth() / 2;
        return ScrollingData.Instance.isSwipeToLeft() ?
                (childOffset < childHalfWidth) : (childOffset > childHalfWidth);
    }

}
