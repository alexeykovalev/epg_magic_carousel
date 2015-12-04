package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.LinearClipData;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = WheelLayoutManager.class.getCanonicalName();

    private final Context context;
    private final CircleConfig circleConfig;
    private final ComputationHelper computationHelper;

    public WheelLayoutManager(Context context, CircleConfig circleConfig) {
        this.context = context;
        this.circleConfig = circleConfig;
        this.computationHelper = new ComputationHelper(circleConfig);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        removeAndRecycleAllViews(recycler);

        addViewForPosition(recycler, 0, -circleConfig.getSectorAngleInRad());
        addViewForPosition(recycler, 1, 0);
        addViewForPosition(recycler, 2, circleConfig.getSectorAngleInRad());


    }

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

        rotateBigWraperViewToAngle(bigWrapperView, angleInRad);

        bigWrapperView.setSectorWrapperViewSize(
                computationHelper.getSectorWrapperViewWidth(),
                computationHelper.getSectorWrapperViewHeight()
        );

        bigWrapperView.setSectorClipArea(computationHelper.createSectorClipArea());

        addView(bigWrapperView);
    }

    private Rect getWrapperViewCoordsInCircleSystem(int wrapperViewWidth) {
        final int topEdge = computationHelper.getSectorWrapperViewHeight() / 2;
        return new Rect(0, topEdge, wrapperViewWidth, -topEdge);
    }

    private void rotateBigWraperViewToAngle(View bigWrapperView, double angleToRotateInRad) {
        bigWrapperView.setPivotX(0);
        bigWrapperView.setPivotY(bigWrapperView.getMeasuredHeight() / 2);
        bigWrapperView.setRotation(WheelUtils.radToDegree(angleToRotateInRad));
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
        return dy;
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
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );
    }



    private static final class ComputationHelper {

        private static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

        private final CircleConfig circleConfig;

        private int sectorWrapperViewWidth = NOT_DEFINED_VALUE;
        private int sectorWrapperViewHeight = NOT_DEFINED_VALUE;

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
            final double delta = circleConfig.getInnerRadius() * Math.cos(circleConfig.getSectorAngleInRad() / 2);
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
            final double halfHeight = circleConfig.getOuterRadius() * Math.sin(circleConfig.getSectorAngleInRad() / 2);
            return (int) (2 * halfHeight);
        }


        public LinearClipData createSectorClipArea() {
            final int viewWidth = getSectorWrapperViewWidth();
            final int viewHalfHeight = getSectorWrapperViewHeight() / 2;

            final double leftBaseDelta = circleConfig.getInnerRadius() * Math.sin(circleConfig.getSectorAngleInRad() / 2);
            final double rightBaseDelta = circleConfig.getOuterRadius() * Math.sin(circleConfig.getSectorAngleInRad() / 2);

            final CoordinatesHolder first = CoordinatesHolder.ofRect(0, viewHalfHeight + leftBaseDelta);
            final CoordinatesHolder third = CoordinatesHolder.ofRect(0, viewHalfHeight - leftBaseDelta);

            final CoordinatesHolder second = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight + rightBaseDelta);
            final CoordinatesHolder forth = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight - rightBaseDelta);

            return new LinearClipData(first, second, third, forth);
        }
    }
}
