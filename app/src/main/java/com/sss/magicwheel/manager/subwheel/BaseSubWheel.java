package com.sss.magicwheel.manager.subwheel;

import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelBigWrapperView;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;
import com.sss.magicwheel.manager.WheelRotationDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public abstract class BaseSubWheel {

    private static TopSubWheel TOP_SUBWHEEL_LAYOUTER;
    private static BottomSubWheel BOTTOM_SUBWHEEL_LAYOUTER;

    protected final WheelOfFortuneLayoutManager wheelLayoutManager;
    protected final WheelComputationHelper computationHelper;
    protected final WheelConfig wheelConfig;

    protected final double layoutStartAngleInRad;
    protected final double layoutEndAngleInRad;

    /**
     * Make as field in order to eliminate extra objects allocation.
     */
    private final List<View> subWheelChildren = new ArrayList<>();
    private final List<View> unmodifiableSubWheelChildrenWrapper = Collections.unmodifiableList(subWheelChildren);

    public static void initialize(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        if (isInitialized()) {
            throw new IllegalStateException("Layouters have been already initialized.");
        }

        TOP_SUBWHEEL_LAYOUTER = new TopSubWheel(wheelLayoutManager, computationHelper);
        BOTTOM_SUBWHEEL_LAYOUTER = new BottomSubWheel(wheelLayoutManager, computationHelper);
    }

    private static boolean isInitialized() {
        return TOP_SUBWHEEL_LAYOUTER != null || BOTTOM_SUBWHEEL_LAYOUTER != null;
    }

    public static TopSubWheel topSubwheel() {
        if (!isInitialized()) {
            throw new IllegalStateException("Layouter has not been inizialized yet.");
        }
        return TOP_SUBWHEEL_LAYOUTER;
    }

    public static BottomSubWheel bottomSubwheel() {
        if (!isInitialized()) {
            throw new IllegalStateException("Layouter has not been inizialized yet.");
        }
        return BOTTOM_SUBWHEEL_LAYOUTER;
    }


    public interface OnInitialLayoutFinishingListener {
        void onInitialLayoutFinished(int finishedAtAdapterPosition);
    }


    protected BaseSubWheel(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        this.wheelLayoutManager = wheelLayoutManager;
        this.computationHelper = computationHelper;
        this.wheelConfig = computationHelper.getWheelConfig();

        this.layoutStartAngleInRad = getLayoutStartAngleInRad();
        this.layoutEndAngleInRad = getLayoutEndAngleInRad();
    }


    public abstract void doInitialChildrenLayout(RecyclerView.Recycler recycler,
                                                 RecyclerView.State state,
                                                 int startLayoutFromAdapterPosition,
                                                 OnInitialLayoutFinishingListener layoutFinishingListener);

    public abstract String getUniqueMarker();

    public abstract double getLayoutStartAngleInRad();

    public abstract double getLayoutEndAngleInRad();



    // TODO: 04.02.2016 can cause IndexOutOfBoundsException
    public final View getChildClosestToLayoutStartEdge() {
        return getChildren().get(0);
    }

    // TODO: 04.02.2016 can cause IndexOutOfBoundsException
    public final View getChildClosestToLayoutEndEdge() {
        return getChildren().get(getChildren().size() - 1);
    }

    public final List<View> getChildren() {
        subWheelChildren.clear();
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final WheelOfFortuneLayoutManager.LayoutParams sectorViewLp = WheelOfFortuneLayoutManager.getChildLayoutParams(sectorView);

//            if (isInLayoutAngleRange(sectorViewLp.anglePositionInRad)) {
//                subWheelChildren.add(sectorView);
//            }

            if (getUniqueMarker().equals(sectorViewLp.subwheelMarker)) {
                subWheelChildren.add(sectorView);
            }
        }

        return unmodifiableSubWheelChildrenWrapper;
    }

    private boolean isInLayoutAngleRange(double sectorAngularPosInRad) {
        final double sectorBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorAngularPosInRad);
        final double sectorTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorAngularPosInRad);

        return sectorBottomEdgeAngularPosInRad <= layoutStartAngleInRad
                && sectorTopEdgeAngularPosInRad >= layoutEndAngleInRad;
    }


//    private void recycleAndAddSectors(WheelRotationDirection rotationDirection,
//                                      RecyclerView.Recycler recycler,
//                                      RecyclerView.State state) {
//
//        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
//            recycleSectorsFromTopIfNeeded(recycler);
//            addSectorsToBottomIfNeeded(recycler, state);
//        } else if (rotationDirection == WheelRotationDirection.Clockwise) {
////            recycleSectorsFromBottomIfNeeded(recycler);
////            addSectorsToTopIfNeeded(recycler, state);
//        } else {
//            throw new IllegalArgumentException("...");
//        }
//    }


    // TODO: 04.02.2016 Move to Anticlockwise rotator

//    private void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        final View lastChild = wheelLayoutManager.getChildClosestToBottom();
//        final WheelOfFortuneLayoutManager.LayoutParams lastChildLp = (WheelOfFortuneLayoutManager.LayoutParams) lastChild.getLayoutParams();
//
//        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
//        final double bottomLimitAngle = wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();
//
//        double layoutAngle = computationHelper.getSectorAngleBottomEdgeInRad(lastChildLp.anglePositionInRad);
//        int childPos = wheelLayoutManager.getPosition(lastChild) + 1;
//        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
////            Log.e(TAG, "addSectorsToBottomIfNeeded() " +
////                    "layoutAngle [" + WheelUtils.radToDegree(layoutAngle) + "], " +
////                    "childPos [" + childPos + "]"
////            );
//            wheelLayoutManager.setupSectorForPosition(recycler, childPos, layoutAngle, true);
//            layoutAngle -= sectorAngleInRad;
//            childPos++;
//        }
//    }
//
//    private void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler) {
//        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
//            final WheelOfFortuneLayoutManager.LayoutParams childLp
//                    = (WheelOfFortuneLayoutManager.LayoutParams) wheelLayoutManager.getChildAt(i).getLayoutParams();
//            if (childLp.anglePositionInRad > computationHelper.getWheelLayoutStartAngleInRad()) {
//                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
////                Log.i(TAG, "Recycle view at index [" + i + "]");
//            }
//        }
//    }


}
