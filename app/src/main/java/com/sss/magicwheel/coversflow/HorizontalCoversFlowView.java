package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.App;
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

        private ScrollingData() {}

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
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ScrollingData.update(dx);
            resizeCovers();
        }
    }

    public HorizontalCoversFlowView(Context context) {
        this(context, null);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        addOnScrollListener(new CoverZoomScrollListener());
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new CoversFlowAdapter(context, Collections.<CoverEntity>emptyList()));
        addItemDecoration(new HorizontalEdgesDecorator(context));
    }

    public void swapData(List<CoverEntity> coversData) {
        getAdapter().swapData(coversData);
    }

    @Override
    public CoversFlowAdapter getAdapter() {
        return (CoversFlowAdapter) super.getAdapter();
    }

    public void resizeCoverOnClick() {
        final View firstCover = findChildIntersectingWithEdge();

//        firstCover.setPivotX(firstCover.getWidth() / 2);
//        firstCover.setPivotY(firstCover.getHeight() / 2);

//        firstCover.setScaleX(2);
//        firstCover.setScaleY(2);

        final ViewGroup.LayoutParams lp = firstCover.getLayoutParams();
        lp.width *= 2;
        lp.height *= 2;

        firstCover.setLayoutParams(lp);

//        firstCover.setPivotX(firstCover.getWidth() / 2);
//        firstCover.setPivotY(firstCover.getHeight() / 2);
//
//        firstCover.setScaleX(2);
//        firstCover.setScaleY(2);
    }

    private void resizeCovers() {
        HorizontalCoverView intersectingChild = findChildIntersectingWithEdge();
        if (intersectingChild != null) {
            final double zoomFactor = getChildZoomFactor(intersectingChild);
            Log.e("TAG", "zoomFactor [" + zoomFactor + "]");

            final int maxHeight = getChildMaxHeight();
            final int initialHeight = intersectingChild.getInitialHeight();

            double newChildHeight = initialHeight + (maxHeight - initialHeight) * zoomFactor;
            final int newChildHeightAsInt = (int) Math.round(newChildHeight);

            final ViewGroup.LayoutParams lp = intersectingChild.getLayoutParams();
            lp.height = newChildHeightAsInt;
            lp.width = (int) (newChildHeightAsInt * HorizontalCoverView.ASPECT_RATIO);

            intersectingChild.setLayoutParams(lp);

        }
    }

    private int getChildMaxHeight() {
        return getHeight();
    }

    private HorizontalCoverView findChildIntersectingWithEdge() {
        final float edgeLeftPosition = App.dpToPixels(
                HorizontalEdgesDecorator.START_LEFT_EDGE_DRAW_FROM_IN_DP
        );

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final float childLeftX = child.getX();
            final float childRightX = childLeftX + child.getWidth();

            if (childLeftX <= edgeLeftPosition && childRightX >= edgeLeftPosition) {
                return (HorizontalCoverView) child;
            }
        }

        return null;
    }

    private double getChildZoomFactor(HorizontalCoverView childToZoom) {
        final float edgeLeftPosition = App.dpToPixels(
                HorizontalEdgesDecorator.START_LEFT_EDGE_DRAW_FROM_IN_DP
        );
        final float childStartX = childToZoom.getX();
        final float offset = edgeLeftPosition - childStartX;

        double zoomFactor = 1;
        if (isZoomUp(childToZoom, offset)) {
            final int halfWidth = childToZoom.getInitialWidth() / 2;
            zoomFactor = offset / halfWidth;
        } else {
            final int halfWidth = childToZoom.getInitialWidth() / 2;
            zoomFactor = 1 - (offset - halfWidth) / halfWidth;
        }

        return zoomFactor;
    }

    private boolean isZoomUp(HorizontalCoverView childToZoom, float childOffset) {
        if (ScrollingData.instance.isSwipeToLeft()) {
            return childOffset < (childToZoom.getInitialWidth() / 2);
        }

        return false;
    }
}
