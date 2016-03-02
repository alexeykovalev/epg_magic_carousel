package com.sss.magicwheel.wheel.coversflow.widget;

import android.animation.Animator;
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
import com.sss.magicwheel.wheel.coversflow.CoversFlowAdapter;
import com.sss.magicwheel.wheel.coversflow.CoversFlowListMeasurements;
import com.sss.magicwheel.wheel.coversflow.entity.CoverEntity;

import java.util.Collections;
import java.util.List;

/**
 * Represents horizontally oriented stripe view - horizontal list,
 * consisting of covers of two types:
 *
 * <ol>
 *     <li>{@link HorizontalCoverView} - real cover which is UI presentation
 *     of {@link CoverEntity} data item</li>
 *     <li>{@link HorizontalOffsetView} - plays role of right and left
 *     offsets in horizontal list. It's required for offset first and last covers
 *     from left and right screen edges respectively</li>
 * </ol>
 *
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class HorizontalCoversFlowView extends RecyclerView {

    private static final int HORIZONTAL_SPACING_IN_DP = 15;

    private static final int SCALING_ANIMATION_DURATION = 300;
    private static final int ALPHA_ANIMATION_DURATION = SCALING_ANIMATION_DURATION;

    private class CoverZoomingScrollListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (!isCoversFlowViewInScrollingState()) {
                scrollToFullySelectCover();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            updateScrollingState(dx);
            resizeCovers();
        }
    }

    private final CoversFlowListMeasurements coversFlowMeasurements;
    private CoverEntity lastlyClickedCoverEntity;

    private boolean isSwipeToLeftGesture;
    private boolean isAdapterDataSetChanged = true;

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
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public void onLayoutChildren(Recycler recycler, State state) {
                super.onLayoutChildren(recycler, state);
                // when we swap data in adapter we should select default cover and make it big
                if (isAdapterDataSetChanged) {
                    scrollToFullySelectCover();
                    isAdapterDataSetChanged = false;
                }
            }
        });

        setAdapter(new CoversFlowAdapter(context, Collections.<CoverEntity>emptyList(), new CoversFlowAdapter.ICoverClickListener() {
            @Override
            public void onCoverClick(HorizontalCoverView coverView, CoverEntity coverEntity) {
                selectCoverOnClick(coverView, coverEntity);
            }
        }));
        getAdapter().registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                isAdapterDataSetChanged = true;
            }
        });

        addOnScrollListener(new CoverZoomingScrollListener());

//        drawResizingEdgeInDebug();
        setupCoversHorizontalSpacing((int) App.dpToPixels(HORIZONTAL_SPACING_IN_DP));
    }

    private void drawResizingEdgeInDebug() {
        addItemDecoration(new CoversFlowResizingEdgeItemDecoration());
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

    private boolean isCoversFlowViewInScrollingState() {
        return getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
    }

    private void notifyOnCoverSelectedIfNeeded() {
        if (!isCoversFlowViewInScrollingState()) {
            final HorizontalCoverView intersectingCoverView = findCoverIntersectingWithResizingEdge();
            if (intersectingCoverView != null) {
                intersectingCoverView.onCoverSelected();
            }
        }
    }

    private void updateScrollingState(int deltaX) {
        isSwipeToLeftGesture = deltaX >= 0;
    }

    public void swapData(List<CoverEntity> coversData) {
        goToFirstCover();
        getAdapter().swapData(coversData);
    }

    private void goToFirstCover() {
        scrollToPosition(0);
    }

    public void displayWithScaleUpAnimation() {
        playTogetherAnimations(
                createScalingAnimatorBetweenValues(0.0f, 1.0f),
                createAlphaAnimatorBetweenValues(0.0f, 1.0f)
        );
    }

    // TODO: 29.02.2016 does not work properly because when we call this method displayWithScaleUpAnimation() invoked
    // TODO: 29.02.2016 immediately and interrupts this one
    public void hideWithScaleDownAnimation() {
        playTogetherAnimations(
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

    private void playTogetherAnimations(Animator... animatorsToPlay) {
        AnimatorSet combinedAnimators = new AnimatorSet();
        combinedAnimators.playTogether(animatorsToPlay);
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

        notifyOnCoverSelectedIfNeeded();
    }

    /**
     * // TODO: 25.02.2016 refactor this method.
     *
     * Either intersecting with resizing edge cover or closest from right to resizing
     * edge cover.
     */
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
        // if we do press on same cover - simply ignore it
        if (lastlyClickedCoverEntity == null || !lastlyClickedCoverEntity.equals(coverEntity)) {
            lastlyClickedCoverEntity = coverEntity;

            final float resizingEdgePosition = coversFlowMeasurements.getResizingEdgePosition();
            final HorizontalCoverView intersectingCoverView = findCoverIntersectingWithResizingEdge();
            float extraWidthToCompensate =
                    intersectingCoverView != null ?
                            (intersectingCoverView.getWidth() - coversFlowMeasurements.getCoverDefaultWidth()) : 0;

            final boolean isClickedCoverToRightOfResizingEdge = clickedCoverView.getLeft() >= resizingEdgePosition;
            float scrollByX;
            if (isClickedCoverToRightOfResizingEdge) {
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
        }
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
        HorizontalCoverView intersectingCover = findCoverIntersectingWithResizingEdge();
        resizeIntersectingCover(intersectingCover);
        restoreOtherCoversToInitialSize(intersectingCover);
        requestLayout();
    }

    private void resizeIntersectingCover(HorizontalCoverView intersectingCover) {
        if (intersectingCover != null) {
            final double zoomFactor = getCoverZoomingFactor(intersectingCover);

            final int coverMaxHeight = getCoverMaxHeight();
            final int coverInitialHeight = coversFlowMeasurements.getCoverDefaultHeight();

            double newCoverHeight = coverInitialHeight + (coverMaxHeight - coverInitialHeight) * zoomFactor;
            final int newCoverHeightAsInt = (int) newCoverHeight;

            final int topMarginValue = (getCoverMaxHeight() - newCoverHeightAsInt) / 2;
            final ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) intersectingCover.getLayoutParams();
            lp.height = newCoverHeightAsInt;
            lp.width = (int) (newCoverHeightAsInt * CoversFlowListMeasurements.COVER_ASPECT_RATIO);
            lp.topMargin = topMarginValue;
        }
    }

    private void restoreOtherCoversToInitialSize(HorizontalCoverView intersectingCover) {
        for (int i = 0; i < getChildCount(); i++) {
            final View coverView = getChildAt(i);
            final int topMarginValue = (getCoverMaxHeight() - coversFlowMeasurements.getCoverDefaultHeight()) / 2;
            final MarginLayoutParams coverViewLp = (MarginLayoutParams) coverView.getLayoutParams();
            if (intersectingCover != coverView) {
                coverViewLp.height = coversFlowMeasurements.getCoverDefaultHeight();
                coverViewLp.width = coversFlowMeasurements.getCoverDefaultWidth();
                coverViewLp.leftMargin = coversFlowMeasurements.getCoverDefaultMargins().left;
                coverViewLp.topMargin = topMarginValue;
            }
        }
    }

    private int getCoverMaxHeight() {
        return getHeight();
    }

    private HorizontalCoverView findCoverIntersectingWithResizingEdge() {
        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();

        for (int i = 0; i < getChildCount(); i++) {
            final View coverView = getChildAt(i);
            final IHorizontalCoverView horizontalCoverView = (IHorizontalCoverView) coverView;

            final float coverLeftX = coverView.getLeft();
            final float coverRightX = coverLeftX + coverView.getWidth();

            final boolean isIntersectingWithResizingEdge = coverLeftX <= edgeLeftPosition && coverRightX >= edgeLeftPosition;
            if (!horizontalCoverView.isOffsetCover() && isIntersectingWithResizingEdge) {
                return (HorizontalCoverView) coverView;
            }
        }

        return null;
    }

    private double getCoverZoomingFactor(HorizontalCoverView coverToZoom) {
        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();
        final float coverStartX = coverToZoom.getLeft();
        final float offset = edgeLeftPosition - coverStartX;

        final double zoomFactor;
        final double halfCoverDefaultWidth = coversFlowMeasurements.getCoverDefaultWidth() / 2;
        if (isSwipeToLeftGesture) {
            if (isZoomUp(offset)) {
                zoomFactor = offset / halfCoverDefaultWidth;
            } else {
                zoomFactor = 1 - (offset - halfCoverDefaultWidth) / halfCoverDefaultWidth;
            }
        } else {
            if (isZoomUp(offset)) {
                zoomFactor = 1 - (offset - halfCoverDefaultWidth) / halfCoverDefaultWidth;
            } else {
                zoomFactor = offset / halfCoverDefaultWidth;
            }
        }

        return zoomFactor;
    }

    private boolean isZoomUp(float coverOffset) {
        final float coverHalfDefaultWidth = coversFlowMeasurements.getCoverDefaultWidth() / 2;
        return isSwipeToLeftGesture ? (coverOffset < coverHalfDefaultWidth) : (coverOffset > coverHalfDefaultWidth);
    }

}
