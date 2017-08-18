package com.magicepg.wheel.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.magicepg.wheel.layout.AbstractWheelLayoutManager;
import com.magicepg.wheel.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 14.04.2017
 */
public abstract class AbstractClockwiseWheelRotator extends AbstractWheelRotator {

    protected AbstractClockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public final void rotateWheelBy(double rotationAngleInRad) {
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad -= rotationAngleInRad;
            wheelLayoutManager.alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }
    }

    @Override
    public final void recycleSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        recycleSectorsFromBottomIfNeeded(recycler);
    }

    @Override
    public final void addSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        addSectorsToTopInNeeded(recycler, state);
    }

    /**
     * When sectorView's top edge goes outside {@code layoutEndAngle} then recycle this sector.
     */
    protected abstract void recycleSectorsFromBottomIfNeeded(RecyclerView.Recycler recycler);

    /**
     * Adds new sector views until lastly added sectorView's bottom edge be greater than
     * {@code layoutStartEdge}
     */
    protected abstract void addSectorsToTopInNeeded(RecyclerView.Recycler recycler, RecyclerView.State state);

}
