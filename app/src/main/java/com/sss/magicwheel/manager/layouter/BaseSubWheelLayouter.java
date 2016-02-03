package com.sss.magicwheel.manager.layouter;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.manager.WheelComputationHelper;

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

}
