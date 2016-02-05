package com.sss.magicwheel.manager.subwheel;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public abstract class BaseSubWheel {

    private static final double ANGLE_DELTA_IN_RAD = WheelComputationHelper.degreeToRadian(2);

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
        if (!isInitialized()) {
            TOP_SUBWHEEL_LAYOUTER = new TopSubWheel(wheelLayoutManager, computationHelper);
            BOTTOM_SUBWHEEL_LAYOUTER = new BottomSubWheel(wheelLayoutManager, computationHelper);
        }
    }

    private static boolean isInitialized() {
        return TOP_SUBWHEEL_LAYOUTER != null && BOTTOM_SUBWHEEL_LAYOUTER != null;
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

            if (isInLayoutAngleRange(sectorViewLp.anglePositionInRad)) {
                subWheelChildren.add(sectorView);
            }

//            if (getUniqueMarker().equals(sectorViewLp.subwheelMarker)) {
//                subWheelChildren.add(sectorView);
//            }
        }

        return unmodifiableSubWheelChildrenWrapper;
    }

    @Deprecated
    private boolean isInLayoutAngleRange(double sectorAngularPosInRad) {
        final double sectorBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorAngularPosInRad);
        final double sectorTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorAngularPosInRad);

        return (sectorBottomEdgeAngularPosInRad <= layoutStartAngleInRad
                    && sectorTopEdgeAngularPosInRad >= layoutEndAngleInRad)
                || isBottomInDeltaRange(sectorAngularPosInRad)
                || isTopInDeltaRange(sectorAngularPosInRad);
    }

    private boolean isBottomInDeltaRange(double sectorAngularPosInRad) {
        final double sectorBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorAngularPosInRad);

        return (layoutStartAngleInRad - ANGLE_DELTA_IN_RAD) <= sectorBottomEdgeAngularPosInRad
                && sectorBottomEdgeAngularPosInRad <= (layoutStartAngleInRad + ANGLE_DELTA_IN_RAD);
    }

    private boolean isTopInDeltaRange(double sectorAngularPosInRad) {
        final double sectorTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorAngularPosInRad);

        return (layoutEndAngleInRad - ANGLE_DELTA_IN_RAD) <= sectorTopEdgeAngularPosInRad
                && sectorTopEdgeAngularPosInRad <= (layoutEndAngleInRad + ANGLE_DELTA_IN_RAD);
    }

}
