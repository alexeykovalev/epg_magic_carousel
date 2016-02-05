package com.sss.magicwheel.manager.rotator;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.entity.WheelRotationDirection;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public abstract class AbstractWheelRotator {

    private static AbstractWheelRotator clockwiseRotator;
    private static AbstractWheelRotator antiClockwiseRotator;

    protected final AbstractWheelLayoutManager wheelLayoutManager;
    protected final WheelComputationHelper computationHelper;

    public static void initialize(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        if (!isInitialized()) {
            clockwiseRotator = new ClockwiseWheelRotator(wheelLayoutManager, computationHelper);
            antiClockwiseRotator = new AnticlockwiseWheelRotator(wheelLayoutManager, computationHelper);
        }
    }

    public static boolean isInitialized() {
        return clockwiseRotator != null && antiClockwiseRotator != null;
    }

    public static AbstractWheelRotator of(WheelRotationDirection rotationDirection) {
        if (!isInitialized()) {
            throw new IllegalStateException();
        }

        if (rotationDirection == null) {
            throw new NullPointerException();
        }

        return rotationDirection == WheelRotationDirection.Clockwise ? clockwiseRotator : antiClockwiseRotator;
    }

    protected AbstractWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        this.wheelLayoutManager = wheelLayoutManager;
        this.computationHelper = computationHelper;
    }


    public abstract void rotateWheel(double rotationAngleInRad, RecyclerView.Recycler recycler, RecyclerView.State state);

    protected abstract void recycleAndAddSectors(RecyclerView.Recycler recycler, RecyclerView.State state);

}
