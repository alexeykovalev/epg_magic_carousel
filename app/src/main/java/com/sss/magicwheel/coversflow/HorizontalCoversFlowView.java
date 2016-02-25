package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.coversflow.entity.CoverEntity;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class HorizontalCoversFlowView extends RecyclerView {

    private static class ScrollingData {

        private static ScrollingData instance = new ScrollingData();

        private int absScrollingDistance;
        private boolean isSwipeToLeft;

        private ScrollingData() {
        }

        public static ScrollingData update(int deltaX) {
            instance.isSwipeToLeft = deltaX >= 0;
            instance.absScrollingDistance = Math.abs(deltaX);
            return instance;
        }

        public boolean isSwipeToLeft() {
            return instance.isSwipeToLeft;
        }
    }

    private class CoverZoomScrollListener extends OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                resizeCovers();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
                selectCover(coverView, coverEntity);
            }
        }));
        addItemDecoration(new HorizontalCoversFlowEdgeDecorator(context));
    }

    public void swapData(List<CoverEntity> coversData) {
        getAdapter().swapData(coversData);
    }

    private void selectCover(HorizontalCoverView coverView, CoverEntity coverEntity) {

        final HorizontalCoverView intersectingChild = findChildIntersectingWithEdge();
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
        HorizontalCoverView intersectingChild = findChildIntersectingWithEdge();
        resizeIntersectingChild(intersectingChild);
        restoreOtherChildrenToInitialSize(intersectingChild);
        requestLayout();
    }

    private void resizeIntersectingChild(HorizontalCoverView intersectingChild) {
        if (intersectingChild != null) {
            final double zoomFactor = getChildZoomFactor(intersectingChild);

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

    private HorizontalCoverView findChildIntersectingWithEdge() {
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

    private double getChildZoomFactor(HorizontalCoverView childToZoom) {
        final float edgeLeftPosition = coversFlowMeasurements.getResizingEdgePosition();
        final float childStartX = childToZoom.getLeft();
        final float offset = edgeLeftPosition - childStartX;

        final double zoomFactor;
        final int halfChildWidth = coversFlowMeasurements.getCoverDefaultWidth() / 2;
        if (ScrollingData.instance.isSwipeToLeft()) {
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
        return ScrollingData.instance.isSwipeToLeft() ?
                (childOffset < childHalfWidth) : (childOffset > childHalfWidth);
    }

}
