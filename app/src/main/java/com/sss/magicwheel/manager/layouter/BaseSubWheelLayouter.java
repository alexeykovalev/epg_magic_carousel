package com.sss.magicwheel.manager.layouter;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public abstract class BaseSubWheelLayouter {

    private static TopSubWheelLayouter TOP_SUBWHEEL_LAYOUTER;
    private static BottomSubWheelLayouter BOTTOM_SUBWHEEL_LAYOUTER;

    protected final WheelOfFortuneLayoutManager wheelLayoutManager;

    public static void initialize(WheelOfFortuneLayoutManager wheelLayoutManager) {
        if (isInitialized()) {
            throw new IllegalStateException("Layouters have been already initialized.");
        }

        TOP_SUBWHEEL_LAYOUTER = new TopSubWheelLayouter(wheelLayoutManager);
        BOTTOM_SUBWHEEL_LAYOUTER = new BottomSubWheelLayouter(wheelLayoutManager);
    }

    private static boolean isInitialized() {
        return TOP_SUBWHEEL_LAYOUTER != null || BOTTOM_SUBWHEEL_LAYOUTER != null;
    }

    public static TopSubWheelLayouter forTopPart() {
        if (!isInitialized()) {
            throw new IllegalStateException("Layouter has not been inizialized yet.");
        }
        return TOP_SUBWHEEL_LAYOUTER;
    }

    public static BottomSubWheelLayouter forBottomPart() {
        if (!isInitialized()) {
            throw new IllegalStateException("Layouter has not been inizialized yet.");
        }
        return BOTTOM_SUBWHEEL_LAYOUTER;
    }

    protected BaseSubWheelLayouter(WheelOfFortuneLayoutManager wheelLayoutManager) {
        this.wheelLayoutManager = wheelLayoutManager;
    }

    public abstract void doInitialChildrenLayout(RecyclerView.Recycler recycler,
                                                 RecyclerView.State state,
                                                 int startLayoutFromAdapterPosition);

}
