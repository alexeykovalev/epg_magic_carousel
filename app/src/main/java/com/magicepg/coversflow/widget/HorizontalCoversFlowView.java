package com.magicepg.coversflow.widget;

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
import android.view.animation.AccelerateDecelerateInterpolator;

import com.magicepg.coversflow.CoverEntity;
import com.magicepg.coversflow.CoversFlowAdapter;
import com.magicepg.coversflow.CoversFlowComputationHelper;
import com.magicepg.func.Preconditions;
import com.magicepg.util.DimensionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents horizontally oriented stripe view - horizontal list,
 * consisting of covers of two types:
 *
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class HorizontalCoversFlowView extends RecyclerView {

    private static final float FLING_GESTURE_VELOCITY_SCALE_DOWN_FACTOR = 0.5f;
    private static final int HORIZONTAL_SPACING_IN_DP = 15;

    private static final int SCALING_ANIMATION_DURATION = CoversFlowComputationHelper.ALPHA_ANIMATION_DURATION;

    private enum SwipeGestureType {
        FromRightToLeft(+1.0f), FromLeftToRight(-1.0f), NotDefined(0.0f);

        private final float deltaXSignum;

        SwipeGestureType(float deltaXSignum) {
            this.deltaXSignum = deltaXSignum;
        }

        public static SwipeGestureType fromDeltaX(int deltaX) {
            for (SwipeGestureType swipeGestureType : SwipeGestureType.values()) {
                if (swipeGestureType.deltaXSignum == deltaX) {
                    return swipeGestureType;
                }
            }
            return NotDefined;
        }
    }

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

    private final CoversFlowComputationHelper coversFlowMeasurements;
    private final int horizontalSpacingValue;

    private CoverEntity lastlyClickedCoverEntity;

    private SwipeGestureType swipeGestureType;
    private boolean isAdapterDataSetChanged = true;

    private final List<OnCoverSelectionListener> coverSelectedListeners = new ArrayList<>();
    private final List<OnCoverPlayButtonClickListener> coverClickListeners = new ArrayList<>();

    private final CoverView.OnPlayButtonClickListener coverPlayButtonClickListener = new CoverView.OnPlayButtonClickListener() {
        @Override
        public void onCoverClick(CoverEntity coverEntity) {
            for (OnCoverPlayButtonClickListener coverClickListener : coverClickListeners) {
                coverClickListener.onCoverClicked(coverEntity);
            }
        }
    };

    public HorizontalCoversFlowView(Context context) {
        this(context, null);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        coversFlowMeasurements = CoversFlowComputationHelper.getInstance();
        horizontalSpacingValue = (int) DimensionUtils.dpToPixels(HORIZONTAL_SPACING_IN_DP);
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

        setAdapter(new CoversFlowAdapter(
                context,
                Collections.<CoverEntity>emptyList(),
                coverPlayButtonClickListener,
                new CoversFlowAdapter.ICoverClickListener() {
                    @Override
                    public void onCoverClick(CoverView coverView, CoverEntity coverEntity) {
                        selectCoverOnClick(coverView, coverEntity);
                    }
                })
        );
        getAdapter().registerAdapterDataObserver(new AdapterDataObserver() {
            @Override
            public void onChanged() {
                isAdapterDataSetChanged = true;
            }
        });

        addOnScrollListener(new CoverZoomingScrollListener());

//        drawResizingEdgeInDebug();
        setupCoversHorizontalSpacing(horizontalSpacingValue);
    }

    public void addCoverSelectionListener(OnCoverSelectionListener coverSelectedListener) {
        coverSelectedListeners.add(Preconditions.checkNotNull(coverSelectedListener));
    }

    public void addCoverPlayButtonClickListener(OnCoverPlayButtonClickListener coverClickListener) {
        coverClickListeners.add(Preconditions.checkNotNull(coverClickListener));
    }

    public void dispose() {
        coverSelectedListeners.clear();
        coverClickListeners.clear();
    }

    private void drawResizingEdgeInDebug() {
        addItemDecoration(new CoversFlowResizingEdgeItemDecoration(coversFlowMeasurements.getResizingEdgePosition()));
    }

    private void setupCoversHorizontalSpacing(final int horizontalSpacingValue) {
        addItemDecoration(new ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                // TODO: 16.03.2016 extract to MeasurementsHolder
                final int leftOffsetValue = CoversFlowComputationHelper.getInstance().getLeftOffset();
                final int rightOffsetValue = CoversFlowComputationHelper.getInstance().getRightOffset();

                final int coverAdapterPosition = parent.getChildAdapterPosition(view);
                final boolean isFirstCover = coverAdapterPosition == 0;
                final boolean isLastCover = coverAdapterPosition == (parent.getAdapter().getItemCount() - 1);

                if (isFirstCover) {
                    outRect.set(leftOffsetValue, 0, horizontalSpacingValue, 0);
                } else if (isLastCover) {
                    outRect.set(0, 0, rightOffsetValue, 0);
                } else {
                    outRect.set(0, 0, horizontalSpacingValue, 0);
                }
            }
        });
    }

    private boolean isCoversFlowViewInScrollingState() {
        return getScrollState() != RecyclerView.SCROLL_STATE_IDLE;
    }

    private void notifyOnCoverSelectedIfNeeded() {
        if (!isCoversFlowViewInScrollingState()) {
            final CoverView intersectingCoverView = findCoverIntersectingWithResizingEdge();
            if (intersectingCoverView != null) {
                lastlyClickedCoverEntity = intersectingCoverView.getAssociatedData();
                intersectingCoverView.onCoverSelected();
            }
            if (lastlyClickedCoverEntity != null) {
                for (OnCoverSelectionListener selectedListener : coverSelectedListeners) {
                    selectedListener.onCoverSelected(lastlyClickedCoverEntity);
                }
            }
        }
    }

    private void updateScrollingState(int deltaX) {
        swipeGestureType = SwipeGestureType.fromDeltaX(deltaX);
    }

    public void bind(List<CoverEntity> coversFlowData) {
        goToFirstCover();
        getAdapter().swapData(coversFlowData);
    }

    public void unbind() {
        dispose();
    }

    private void goToFirstCover() {
        goToCoverAtPosition(0);
    }

    public void goToCoverAtPosition(int coverPositionToSelect) {
        scrollToPosition(coverPositionToSelect);
    }

    public void displayWithScaleUpAnimation() {
        playTogetherAnimations(
                createScalingAnimatorBetweenValues(0.0f, 1.0f),
                CoversFlowComputationHelper.createAlphaAnimatorBetweenValues(this, 0.0f, 1.0f)
        );
    }

    // TODO: WheelOfFortune 29.02.2016 does not work properly because when we call this method displayWithScaleUpAnimation() invoked
    // TODO: WheelOfFortune 29.02.2016 immediately and interrupts this one
    public void hideWithScaleDownAnimation() {
        playTogetherAnimations(
                createScalingAnimatorBetweenValues(1.0f, 0.0f),
                CoversFlowComputationHelper.createAlphaAnimatorBetweenValues(this, 1.0f, 0.0f)
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
        final CoverView coverClosestToResizingEdge = findCoverClosestToResizingEdge();
        if (coverClosestToResizingEdge != null) {
            final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();
            final float childStartX = coverClosestToResizingEdge.getLeft();
            final float offset = edgeLeftPosition - childStartX;

            final float scrollBy = coversFlowMeasurements.getCoverMaxWidth() / 2 - offset;

            smoothScrollBy((int) scrollBy, 0);
        }

        notifyOnCoverSelectedIfNeeded();
    }

    /**
     * Either intersecting with resizing edge cover or closest from right to resizing
     * edge cover.
     */
    private CoverView findCoverClosestToResizingEdge() {
        if (getAdapter().getItemCount() == 0) {
            return null;
        }

        final CoverView intersectingCover = findCoverIntersectingWithResizingEdge();
        if (intersectingCover != null) {
            return intersectingCover;
        }

        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();

        View closestToEdgeFromLeft = null;
        int i = 0;
        while (i < getChildCount() && (closestToEdgeFromLeft = getChildAt(i)).getRight() <= edgeLeftPosition) {
            i++;
        }

        return (CoverView) closestToEdgeFromLeft;
    }

    private CoverView findCoverIntersectingWithResizingEdge() {
        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();

        for (int i = 0; i < getChildCount(); i++) {
            final View coverView = getChildAt(i);
            final float coverLeftX = coverView.getLeft();
            final float coverRightX = coverLeftX + coverView.getWidth();

            final boolean isIntersectingWithResizingEdge = coverLeftX <= edgeLeftPosition && coverRightX >= edgeLeftPosition;
            if (isIntersectingWithResizingEdge) {
                return (CoverView) coverView;
            }
        }

        return null;
    }

    private void selectCoverOnClick(CoverView clickedCoverView, CoverEntity coverEntity) {
        // if we do press on same cover - simply ignore it
        if (lastlyClickedCoverEntity == null || !lastlyClickedCoverEntity.equals(coverEntity)) {
            lastlyClickedCoverEntity = coverEntity;

            final float resizingEdgePosition = coversFlowMeasurements.getResizingEdgePosition();
            final CoverView intersectingCoverView = findCoverIntersectingWithResizingEdge();
            float extraWidthToCompensate =
                    intersectingCoverView != null ?
                            (intersectingCoverView.getWidth() - coversFlowMeasurements.getCoverDefaultWidth()) : 0;

            final boolean isClickedCoverToRightOfResizingEdge = clickedCoverView.getLeft() >= resizingEdgePosition;
            float scrollByX;
            if (isClickedCoverToRightOfResizingEdge) {
                scrollByX = clickedCoverView.getLeft()
                        - resizingEdgePosition
                        + coversFlowMeasurements.getCoverMaxWidth() / 2
                        - extraWidthToCompensate;
            } else {
                scrollByX = resizingEdgePosition
                        - clickedCoverView.getRight()
                        + coversFlowMeasurements.getCoverMaxWidth() / 2
                        - extraWidthToCompensate;

                scrollByX = -scrollByX;
            }

            smoothScrollBy((int) scrollByX, 0);
        }
    }

    /**
     * Slows down usual fling gesture for RecyclerView.
     */
    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= FLING_GESTURE_VELOCITY_SCALE_DOWN_FACTOR;
        return super.fling(velocityX, velocityY);
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
        if (getChildCount() == 0) {
            return;
        }

        final int coverMaxWidth = coversFlowMeasurements.getCoverMaxWidth();
        final int maxCoverHalfWidth = coverMaxWidth / 2;

        final int coverDefaultWidth = coversFlowMeasurements.getCoverDefaultWidth();

        final float resizingEdgePos = coversFlowMeasurements.getResizingEdgePosition();
        float startX = getChildAt(0).getLeft();

        for (int i = 0; i < getChildCount(); i++) {
            final CoverView coverView = (CoverView) getChildAt(i);

            if (startX < resizingEdgePos) { // for covers which are placed before resizing edge
                if (startX + coverMaxWidth < resizingEdgePos) {
                    coverView.setDefaultSize(getHeight());
                    coverView.updateScalingData(0, CoverView.CoverScalingData.ScalingType.NotDefined);

                    // TODO: 16.03.2016 hSpacing only if not last item
                    startX += coverDefaultWidth + horizontalSpacingValue;
                } else { // cover fit in resizing area - wrapper fake view (with coverMaxWidth) intersect with resizing edge
                    final float maxCoverCenterPos = startX + maxCoverHalfWidth;
                    final float centerShiftDelta = resizingEdgePos - maxCoverCenterPos;

                    // changed from [0; 1]
                    // 0 - when cover center placed directly on resizingEdge should be coverMaxWidth
                    // 1 - when resizing edge close_icon to cover left or right edge - should be defaultCoverWidth
                    final float centerShiftDeltaCoefficient = Math.abs(centerShiftDelta) / maxCoverHalfWidth;
                    final float currentCoverWidth = coverMaxWidth * (1 - centerShiftDeltaCoefficient)
                            + coverDefaultWidth * centerShiftDeltaCoefficient;

                    setCoverViewSizeByWidth(coverView, currentCoverWidth);

                    updateCoverScalingData(coverView, centerShiftDelta, 1 - centerShiftDeltaCoefficient);
                    startX += currentCoverWidth + horizontalSpacingValue;
                }
            } else {
                coverView.setDefaultSize(getHeight());
                coverView.updateScalingData(0, CoverView.CoverScalingData.ScalingType.NotDefined);

                // TODO: 16.03.2016 hSpacing only if not last item
                startX += coverDefaultWidth + horizontalSpacingValue;
            }
        }

        requestLayout();
    }

    /**
     * @param coverCenterResizingEdgeRelativeShiftDelta -
     *                                                  {@code > 0} when resizing edge position placed after max cover center position
     * @param coverViewScalingFactor                    - range of changes [0; 1]
     *                                                  </p> {@code 0} - when cover view scaled to default size
     *                                                  </p> {@code 1} - when cover view scaled to max size
     */
    private void updateCoverScalingData(CoverView coverView,
                                        float coverCenterResizingEdgeRelativeShiftDelta,
                                        float coverViewScalingFactor) {
        final CoverView.CoverScalingData.ScalingType scaleType;
        if (coverCenterResizingEdgeRelativeShiftDelta > 0) { // resizingEdgePos > maxCoverCenterPos
            if (swipeGestureType == SwipeGestureType.FromRightToLeft) { //scaling down
                scaleType = CoverView.CoverScalingData.ScalingType.ScaleDown;
            } else if (swipeGestureType == SwipeGestureType.FromLeftToRight) { // scaling up
                scaleType = CoverView.CoverScalingData.ScalingType.ScaleUp;
            } else {
                scaleType = CoverView.CoverScalingData.ScalingType.ScaleUp;
            }
        } else {
            if (swipeGestureType == SwipeGestureType.FromRightToLeft) { // scaling up
                scaleType = CoverView.CoverScalingData.ScalingType.ScaleUp;
            } else if (swipeGestureType == SwipeGestureType.FromLeftToRight) { // scaling down
                scaleType = CoverView.CoverScalingData.ScalingType.ScaleDown;
            } else {
                scaleType = CoverView.CoverScalingData.ScalingType.ScaleUp;
            }
        }
        coverView.updateScalingData(coverViewScalingFactor, scaleType);
    }

    private void setCoverViewSizeByWidth(CoverView coverView, float coverWidth) {
        final double coverNewHeight = coverWidth / CoversFlowComputationHelper.COVER_ASPECT_RATIO;
        coverView.setCoverViewSize(getHeight(), (int) coverWidth, (int) coverNewHeight);
    }

    public interface OnCoverSelectionListener {
        void onCoverSelected(CoverEntity coverEntity);
    }

    public interface OnCoverPlayButtonClickListener {
        void onCoverClicked(CoverEntity clickedCover);
    }
}
