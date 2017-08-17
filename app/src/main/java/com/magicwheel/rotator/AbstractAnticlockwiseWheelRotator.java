package com.magicwheel.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.magicwheel.manager.AbstractWheelLayoutManager;
import com.magicwheel.util.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 14.04.2017
 */
public abstract class AbstractAnticlockwiseWheelRotator extends AbstractWheelRotator {

    protected AbstractAnticlockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public final void rotateWheelBy(double rotationAngleInRad) {
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad += rotationAngleInRad;
            wheelLayoutManager.alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }
    }

    @Override
    public final void recycleSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        recycleSectorsFromTopIfNeeded(recycler);
    }

    @Override
    public final void addSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        addSectorsToBottomIfNeeded(recycler, state);
    }

    /**
     * When sectorView's bottom edge goes outside {@code layoutStartAngle}
     * then recycle this sector.
     */
    protected abstract void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler);

    /**
     * Adds new sector views until lastly added sectorView's top edge be greater than
     * {@code layoutEndEdge}
     */
    protected abstract void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state);
}
