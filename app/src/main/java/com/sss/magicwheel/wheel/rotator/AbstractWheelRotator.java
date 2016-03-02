package com.sss.magicwheel.wheel.rotator;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;
import com.sss.magicwheel.wheel.WheelComputationHelper;
import com.sss.magicwheel.wheel.entity.WheelRotationDirection;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public abstract class AbstractWheelRotator {

    protected final AbstractWheelLayoutManager wheelLayoutManager;
    protected final WheelComputationHelper computationHelper;

    protected AbstractWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        this.wheelLayoutManager = wheelLayoutManager;
        this.computationHelper = computationHelper;
    }

    /**
     * Does wheel rotation into appropriate direction {@code clockwise}
     * or {@code anticlockwise} depending of implementation.
     */
    public abstract void rotateWheelBy(double rotationAngleInRad);

    /**
     * Convenient method for combining invocation of {@link #addSectors(RecyclerView.Recycler, RecyclerView.State)}
     * and {@link #recycleSectors(RecyclerView.Recycler, RecyclerView.State)}
     */
    public final void recycleAndAddSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        addSectors(recycler, state);
        recycleSectors(recycler, state);
    }

    /**
     * Does wheel's sectors recycling which go outside wheel angular restriction area.
     */
    public abstract void recycleSectors(RecyclerView.Recycler recycler, RecyclerView.State state);

    /**
     * Adds additional sectors to the wheel in order to compensate appearing gap after
     * wheel rotation.
     */
    public abstract void addSectors(RecyclerView.Recycler recycler, RecyclerView.State state);

}
