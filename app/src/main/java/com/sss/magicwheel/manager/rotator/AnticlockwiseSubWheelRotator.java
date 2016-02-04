package com.sss.magicwheel.manager.rotator;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;
import com.sss.magicwheel.manager.subwheel.BaseSubWheel;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class AnticlockwiseSubWheelRotator extends AbstractSubWheelRotator {

    protected AnticlockwiseSubWheelRotator(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void rotateSubWheel(BaseSubWheel subWheelToRotate, double rotationAngleInRad, RecyclerView.Recycler recycler, RecyclerView.State state) {
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }

    @Override
    protected void recycleAndAddSectors(BaseSubWheel subWheel, RecyclerView.Recycler recycler, RecyclerView.State state) {
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }
}
