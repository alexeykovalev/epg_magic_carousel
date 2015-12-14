package com.sss.magicwheel.manager.second;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.manager.CircleConfig;
import com.sss.magicwheel.manager.WheelBigWrapperView;
import com.sss.magicwheel.manager.WheelUtils;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
public final class WheelOfFortuneLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = WheelOfFortuneLayoutManager.class.getCanonicalName();

    private final CircleConfig circleConfig;
    private final WheelComputationHelper computationHelper;
    private final WheelScroller scroller;

    public WheelOfFortuneLayoutManager(Context context, CircleConfig circleConfig) {
        this.circleConfig = circleConfig;
        this.computationHelper = new WheelComputationHelper(circleConfig);
        this.scroller = new WheelScroller();
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
            setupViewForPosition(recycler, childPos, layoutAngle);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }
    }

    private void setupViewForPosition(RecyclerView.Recycler recycler, int positionIndex, double angularPosition) {
        final WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) recycler.getViewForPosition(positionIndex);
        measureBigWrapperView(bigWrapperView);

        Rect wrViewCoordsInCircleSystem = computationHelper.getWrapperViewCoordsInCircleSystem(bigWrapperView.getMeasuredWidth());
        Rect wrTransformedCoords = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleConfig.getCircleCenterRelToRecyclerView(),
                wrViewCoordsInCircleSystem
        );

        bigWrapperView.layout(wrTransformedCoords.left, wrTransformedCoords.top, wrTransformedCoords.right, wrTransformedCoords.bottom);

        alignBigWrapperViewByAngle(bigWrapperView, angularPosition);

        bigWrapperView.setSectorWrapperViewSize(
                computationHelper.getSectorWrapperViewWidth(),
                computationHelper.getSectorWrapperViewHeight(),
                computationHelper.createSectorClipArea()
        );

        LayoutParams lp = (LayoutParams) bigWrapperView.getLayoutParams();
        lp.anglePositionInRad = angularPosition;

        addView(bigWrapperView);
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
        bigWrapperView.setRotation((float) WheelUtils.radToDegree(angleAlignToInRad));
    }


    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int childCount = getChildCount();
        Log.i(TAG, "scrollVerticallyBy dy [" + dy + "], childCount [" + childCount + "]");
        if (childCount == 0) {
            // we cannot scroll if we don't have views
            return 0;
        }

        return scroller.scrollVerticallyBy(dy, recycler);
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
         * Defines top edge sector's position on circle.
         * Effectively it equals to view's rotation angle.
         */
        double anglePositionInRad;

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
