package com.sss.magicwheel.coversflow.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

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
    private static final int ALPHA_ANIMATION_DURATION = SCALING_ANIMATION_DURATION;

    private class CoverZoomScrollListener extends OnScrollListener {
        private boolean isFirstLaunch = true;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                scrollToFullySelectCover();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (isFirstLaunch) {
                scrollToFullySelectCover();
                isFirstLaunch = false;
            }
            updateScrollingState(dx);
            resizeCovers();
        }
    }

    private final CoversFlowListMeasurements coversFlowMeasurements;
    private boolean isSwipeToLeft;

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
                final IHorizontalCoverView horizontalCoverView = (IHorizontalCoverView) view;
                final int hSpacing = !horizontalCoverView.isOffsetCover() && !isLastItem ? horizontalSpacingValue : 0;
                outRect.set(0, 0, hSpacing, 0);
            }
        });
    }

    private void updateScrollingState(int deltaX) {
        isSwipeToLeft = deltaX >= 0;
    }

    public void swapData(List<CoverEntity> coversData) {
        scrollToPosition(0);
        getAdapter().swapData(coversData);
    }

    public void displayWithScaleUpAnimation() {
        playTogetherAnimations(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scrollToFullySelectCover();
                    }
                },
                createScalingAnimatorBetweenValues(0.0f, 1.0f),
                createAlphaAnimatorBetweenValues(0.0f, 1.0f)
        );
    }

    // TODO: 29.02.2016 does not work properly because when we call this method displayWithScaleUpAnimation() invoked
    // immediately and interrupts this one
    public void hideWithScaleDownAnimation() {
        playTogetherAnimations(
                null,
                createScalingAnimatorBetweenValues(1.0f, 0.0f),
                createAlphaAnimatorBetweenValues(1.0f, 0.0f)
        );
    }

    private Animator createScalingAnimatorBetweenValues(float startScalingValue, float endScalingValue) {
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);

        final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this, View.SCALE_X, startScalingValue, endScalingValue);
        final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this, View.SCALE_Y, startScalingValue, endScalingValue);

        final TimeInterpolator interpolator = new AccelerateDecelerateInterpolator();
        scaleXAnimator.setInterpolator(interpolator);
        scaleXAnimator.setDuration(SCALING_ANIMATION_DURATION);

        scaleYAnimator.setInterpolator(interpolator);
        scaleYAnimator.setDuration(SCALING_ANIMATION_DURATION);

        final AnimatorSet scalingAnimator = new AnimatorSet();
        scalingAnimator.playTogether(scaleXAnimator, scaleYAnimator);
        return scalingAnimator;
    }

    private Animator createAlphaAnimatorBetweenValues(float startAlphaValue, float endAlphaValue) {
        final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, View.ALPHA, startAlphaValue, endAlphaValue);
        alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimator.setDuration(ALPHA_ANIMATION_DURATION);
        return alphaAnimator;
    }

    private void playTogetherAnimations(Animator.AnimatorListener animatorListener, Animator... animatorsToCombine) {
        AnimatorSet combinedAnimators = new AnimatorSet();
        combinedAnimators.playTogether(animatorsToCombine);
        if (animatorListener != null) {
            combinedAnimators.addListener(animatorListener);
        }
        combinedAnimators.start();
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
        final HorizontalCoverView intersectingCover = findCoverIntersectingWithResizingEdge();
        if (intersectingCover != null) {
            return intersectingCover;
        }

        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();

        View closestToEdgeFromLeft = null;
        int i = 0;
        while (i < getChildCount() && (closestToEdgeFromLeft = getChildAt(i)).getRight() <= edgeLeftPosition) {
            i++;
        }

        final IHorizontalCoverView horizontalCoverView = (IHorizontalCoverView) closestToEdgeFromLeft;
        final boolean isOffsetCover = closestToEdgeFromLeft != null && horizontalCoverView.isOffsetCover();

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

    private void selectCoverOnClick(HorizontalCoverView clickedCoverView, CoverEntity coverEntity) {
        final float resizingEdgePosition = coversFlowMeasurements.getResizingEdgePosition();
        final HorizontalCoverView intersectingCoverView = findCoverIntersectingWithResizingEdge();
        float extraWidthToCompensate =
                intersectingCoverView != null ?
                        (intersectingCoverView.getWidth() - coversFlowMeasurements.getCoverDefaultWidth()) : 0;

        final boolean isClickedCoverToRightOfEdge =
                clickedCoverView.getLeft() >= resizingEdgePosition;

        float scrollByX;
        if (isClickedCoverToRightOfEdge) {
            scrollByX = clickedCoverView.getLeft()
                    - resizingEdgePosition
                    + coversFlowMeasurements.getCoverDefaultWidth() / 2
                    - extraWidthToCompensate;
        } else {
            scrollByX = resizingEdgePosition
                    - clickedCoverView.getRight()
                    + coversFlowMeasurements.getCoverDefaultWidth() / 2;

            scrollByX = -scrollByX;
        }

        smoothScrollBy((int) scrollByX, 0);

       /* Log.e("TAG", "extraWidthToCompensate [" + extraWidthToCompensate + "], " +
                "intersectingCoverView.getWidth() [" + (intersectingCoverView != null ? intersectingCoverView.getWidth() : 0) + "], " +
                "getCoverDefaultWidth() [" + coversFlowMeasurements.getCoverDefaultWidth() + "], " +
                " ------------ " +
                "resizingEdgePosition [" + resizingEdgePosition + "], " +
                "clickedCoverView.getLeft() [" + clickedCoverView.getLeft() + "]");
        */
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
        HorizontalCoverView intersectingChild = findCoverIntersectingWithResizingEdge();
        resizeIntersectingChild(intersectingChild);
        restoreOtherChildrenToInitialSize(intersectingChild);
        requestLayout();
    }

    private void resizeIntersectingChild(HorizontalCoverView intersectingChild) {
        if (intersectingChild != null) {
            final double zoomFactor = getChildZoomingFactor(intersectingChild);

            final int maxHeight = coversFlowMeasurements.getCoverMaxHeight();
            final int initialHeight = coversFlowMeasurements.getCoverDefaultHeight();

            double newChildHeight = initialHeight + (maxHeight - initialHeight) * zoomFactor;
            final int newChildHeightAsInt = (int) newChildHeight;

            final int topMarginValue = (getHeight() - newChildHeightAsInt) / 2;
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

    private HorizontalCoverView findCoverIntersectingWithResizingEdge() {
        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final IHorizontalCoverView horizontalCoverView = (IHorizontalCoverView) child;

            final float childLeftX = child.getLeft();
            final float childRightX = childLeftX + child.getWidth();

            final boolean isIntersectingWithResizingEdge = childLeftX <= edgeLeftPosition && childRightX >= edgeLeftPosition;
            if (!horizontalCoverView.isOffsetCover() && isIntersectingWithResizingEdge) {
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
        final double halfChildWidth = coversFlowMeasurements.getCoverDefaultWidth() / 2;
        if (isSwipeToLeft) {
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
        final float childHalfWidth = coversFlowMeasurements.getCoverDefaultWidth() / 2;
        return isSwipeToLeft ? (childOffset < childHalfWidth) : (childOffset > childHalfWidth);
    }

}
