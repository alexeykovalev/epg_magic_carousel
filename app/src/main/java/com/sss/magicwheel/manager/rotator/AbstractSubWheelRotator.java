package com.sss.magicwheel.manager.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelRotationDirection;
import com.sss.magicwheel.manager.subwheel.BaseSubWheel;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public abstract class AbstractSubWheelRotator {

    private static AbstractSubWheelRotator clockwiseRotator;
    private static AbstractSubWheelRotator antiClockwiseRotator;

    protected final WheelOfFortuneLayoutManager wheelLayoutManager;
    protected final WheelComputationHelper computationHelper;

    public static void initialize(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        if (!isInitialized()) {
            clockwiseRotator = new ClockwiseSubWheelRotator(wheelLayoutManager, computationHelper);
            antiClockwiseRotator = new AnticlockwiseSubWheelRotator(wheelLayoutManager, computationHelper);
        }
    }

    public static boolean isInitialized() {
        return clockwiseRotator != null && antiClockwiseRotator != null;
    }

    public static AbstractSubWheelRotator of(WheelRotationDirection rotationDirection) {
        if (!isInitialized()) {
            throw new IllegalStateException();
        }

        if (rotationDirection == null) {
            throw new NullPointerException();
        }

        return rotationDirection == WheelRotationDirection.Clockwise ? clockwiseRotator : antiClockwiseRotator;
    }

    protected AbstractSubWheelRotator(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        this.wheelLayoutManager = wheelLayoutManager;
        this.computationHelper = computationHelper;
    }


    public abstract void rotateSubWheel(BaseSubWheel subWheelToRotate,
                                        double rotationAngleInRad,
                                        RecyclerView.Recycler recycler,
                                        RecyclerView.State state);

    protected abstract void recycleAndAddSectors(BaseSubWheel subWheel,
                                                 RecyclerView.Recycler recycler,
                                                 RecyclerView.State state);

}
