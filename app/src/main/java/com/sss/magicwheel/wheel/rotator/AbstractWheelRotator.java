package com.sss.magicwheel.wheel.rotator;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;
import com.sss.magicwheel.wheel.WheelComputationHelper;
import com.sss.magicwheel.wheel.entity.WheelRotationDirection;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
@Deprecated
public abstract class AbstractWheelRotator {

    private static AbstractWheelRotator clockwiseRotator;
    private static AbstractWheelRotator antiClockwiseRotator;

    protected final AbstractWheelLayoutManager wheelLayoutManager;
    protected final WheelComputationHelper computationHelper;

    public static void initialize(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        if (!isInitialized()) {
//            clockwiseRotator = new ClockwiseWheelRotator(wheelLayoutManager, computationHelper);
//            antiClockwiseRotator = new AnticlockwiseWheelRotator(wheelLayoutManager, computationHelper);
        }
    }

    public static boolean isInitialized() {
        return clockwiseRotator != null && antiClockwiseRotator != null;
    }

    @Deprecated
    public static AbstractWheelRotator of(WheelRotationDirection rotationDirection) {
        if (true) {
            throw new UnsupportedOperationException();
        }
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


    public abstract void rotateWheelBy(double rotationAngleInRad);

    public final void recycleAndAddSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        addSectors(recycler, state);
        recycleSectors(recycler, state);
    }

    public abstract void recycleSectors(RecyclerView.Recycler recycler, RecyclerView.State state);

    public abstract void addSectors(RecyclerView.Recycler recycler, RecyclerView.State state);

}
