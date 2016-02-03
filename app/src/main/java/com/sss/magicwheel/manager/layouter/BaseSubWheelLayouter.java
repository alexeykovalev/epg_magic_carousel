package com.sss.magicwheel.manager.layouter;

import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelBigWrapperView;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelRotationDirection;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public abstract class BaseSubWheelLayouter {

    private static TopSubWheelLayouter TOP_SUBWHEEL_LAYOUTER;
    private static BottomSubWheelLayouter BOTTOM_SUBWHEEL_LAYOUTER;

    protected final WheelOfFortuneLayoutManager wheelLayoutManager;
    protected final WheelComputationHelper computationHelper;
    protected final WheelConfig wheelConfig;

    public static void initialize(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        if (isInitialized()) {
            throw new IllegalStateException("Layouters have been already initialized.");
        }

        TOP_SUBWHEEL_LAYOUTER = new TopSubWheelLayouter(wheelLayoutManager, computationHelper);
        BOTTOM_SUBWHEEL_LAYOUTER = new BottomSubWheelLayouter(wheelLayoutManager, computationHelper);
    }

    private static boolean isInitialized() {
        return TOP_SUBWHEEL_LAYOUTER != null || BOTTOM_SUBWHEEL_LAYOUTER != null;
    }

    public static TopSubWheelLayouter topSubwheel() {
        if (!isInitialized()) {
            throw new IllegalStateException("Layouter has not been inizialized yet.");
        }
        return TOP_SUBWHEEL_LAYOUTER;
    }

    public static BottomSubWheelLayouter bottomSubwheel() {
        if (!isInitialized()) {
            throw new IllegalStateException("Layouter has not been inizialized yet.");
        }
        return BOTTOM_SUBWHEEL_LAYOUTER;
    }


    public interface OnInitialLayoutFinishingListener {
        void onInitialLayoutFinished(int finishedAtAdapterPosition);
    }


    protected BaseSubWheelLayouter(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        this.wheelLayoutManager = wheelLayoutManager;
        this.computationHelper = computationHelper;
        this.wheelConfig = computationHelper.getWheelConfig();
    }

    public abstract void doInitialChildrenLayout(RecyclerView.Recycler recycler,
                                                 RecyclerView.State state,
                                                 int startLayoutFromAdapterPosition,
                                                 OnInitialLayoutFinishingListener layoutFinishingListener);

    public abstract void rotateSubWheel(double rotationAngle, WheelRotationDirection rotationDirection);


    protected final void setupSectorForPosition(RecyclerView.Recycler recycler, int positionIndex, double angularPosition, boolean isAddViewToBottom) {
        final WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) recycler.getViewForPosition(positionIndex);
        measureBigWrapperView(bigWrapperView);

        RectF wrViewCoordsInCircleSystem = computationHelper.getBigWrapperViewCoordsInCircleSystem(bigWrapperView.getMeasuredWidth());
        RectF wrTransformedCoords = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                wrViewCoordsInCircleSystem
        );

        bigWrapperView.layout(
                (int) wrTransformedCoords.left, (int) wrTransformedCoords.top,
                (int) wrTransformedCoords.right, (int) wrTransformedCoords.bottom
        );

        alignBigWrapperViewByAngle(bigWrapperView, -angularPosition);

        WheelOfFortuneLayoutManager.LayoutParams lp = (WheelOfFortuneLayoutManager.LayoutParams) bigWrapperView.getLayoutParams();
        lp.anglePositionInRad = angularPosition;

        if (isAddViewToBottom) {
            wheelLayoutManager.addView(bigWrapperView);
        } else {
            wheelLayoutManager.addView(bigWrapperView, 0);
        }
    }

    protected final void alignBigWrapperViewByAngle(View bigWrapperView, double angleAlignToInRad) {
        bigWrapperView.setPivotX(0);
        bigWrapperView.setPivotY(bigWrapperView.getMeasuredHeight() / 2);
        float angleInDegree = (float) WheelComputationHelper.radToDegree(angleAlignToInRad);

        // TODO: 16.12.2015 ugly bug fix related to central view disappearing while scrolling
//        if (angleInDegree > -0.1f && angleInDegree < 0.1f) {
//            angleInDegree = 0;
//        }

        bigWrapperView.setRotation(angleInDegree);

//        final String text = ((WheelBigWrapperView) bigWrapperView).getText();
//        Log.e(TAG, "alignBigWrapperViewByAngle text [" + text + "], angleInDegree [" + angleInDegree + "]");
    }

    protected final void measureBigWrapperView(View bigWrapperView) {
        final int viewWidth = computationHelper.getBigWrapperViewMeasurements().getWidth();
        final int viewHeight = computationHelper.getBigWrapperViewMeasurements().getHeight();

        final int childWidthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int childHeightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
        bigWrapperView.measure(childWidthSpec, childHeightSpec);
    }

    public void recycleAndAddSectors(WheelRotationDirection rotationDirection,
                                      RecyclerView.Recycler recycler,
                                      RecyclerView.State state) {

        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
            recycleSectorsFromTopIfNeeded(recycler);
            addSectorsToBottomIfNeeded(recycler, state);
        } else if (rotationDirection == WheelRotationDirection.Clockwise) {
            recycleSectorsFromBottomIfNeeded(recycler);
            addSectorsToTopIfNeeded(recycler, state);
        } else {
            throw new IllegalArgumentException("...");
        }
    }

    private void addSectorsToTopIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View firstChild = wheelLayoutManager.getChildClosestToTop();
        final WheelOfFortuneLayoutManager.LayoutParams firstChildLp = (WheelOfFortuneLayoutManager.LayoutParams) firstChild.getLayoutParams();

        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();

        double layoutAngle = firstChildLp.anglePositionInRad + sectorAngleInRad;
        int childPos = wheelLayoutManager.getPosition(firstChild) - 1;
        while (layoutAngle < computationHelper.getWheelLayoutStartAngleInRad() && childPos >= 0) {
//            Log.i(TAG, "addSectorsToTopIfNeeded() " +
//                            "layoutAngle [" + WheelComputationHelper.radToDegree(layoutAngle) + "], " +
//                            "childPos [" + childPos + "]"
//            );
            setupSectorForPosition(recycler, childPos, layoutAngle, false);
            layoutAngle += sectorAngleInRad;
            childPos--;
        }
    }

    private void recycleSectorsFromBottomIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = wheelLayoutManager.getChildCount() - 1; i >= 0; i--) {
            final WheelOfFortuneLayoutManager.LayoutParams childLp
                    = (WheelOfFortuneLayoutManager.LayoutParams) wheelLayoutManager.getChildAt(i).getLayoutParams();
            if (childLp.anglePositionInRad < wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
//                Log.e(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    private void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View lastChild = wheelLayoutManager.getChildClosestToBottom();
        final WheelOfFortuneLayoutManager.LayoutParams lastChildLp = (WheelOfFortuneLayoutManager.LayoutParams) lastChild.getLayoutParams();

        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
        final double bottomLimitAngle = wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();

        double layoutAngle = computationHelper.getSectorAngleBottomEdgeInRad(lastChildLp.anglePositionInRad);
        int childPos = wheelLayoutManager.getPosition(lastChild) + 1;
        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
//            Log.e(TAG, "addSectorsToBottomIfNeeded() " +
//                    "layoutAngle [" + WheelUtils.radToDegree(layoutAngle) + "], " +
//                    "childPos [" + childPos + "]"
//            );
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }
    }

    private void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final WheelOfFortuneLayoutManager.LayoutParams childLp
                    = (WheelOfFortuneLayoutManager.LayoutParams) wheelLayoutManager.getChildAt(i).getLayoutParams();
            if (childLp.anglePositionInRad > computationHelper.getWheelLayoutStartAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
//                Log.i(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }


}
