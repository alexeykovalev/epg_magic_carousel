package com.sss.magicwheel.wheel.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;
import com.sss.magicwheel.wheel.misc.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class ClockwiseWheelRotator extends AbstractWheelRotator {

    public ClockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void rotateWheelBy(double rotationAngleInRad) {
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad -= rotationAngleInRad;
            wheelLayoutManager.alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }
    }

    @Override
    public void recycleSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        recycleSectorsFromLayoutEndEdge(recycler);
    }

    @Override
    public void addSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        addSectorsToLayoutStartEdge(recycler, state);
    }

    /**
     * When sectorView's top edge goes outside {@code layoutEndAngle} then recycle this sector.
     */
    private void recycleSectorsFromLayoutEndEdge(RecyclerView.Recycler recycler) {
        for (int i = wheelLayoutManager.getChildCount() - 1; i >= 0; i--) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewTopEdgeAngularPosInRad < wheelLayoutManager.getLayoutEndAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
            }
        }
    }

    /**
     * Adds new sector views until lastly added sectorView's bottom edge be greater than
     * {@code layoutStartEdge}
     */
    private void addSectorsToLayoutStartEdge(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToStartSectorView = wheelLayoutManager.getChildClosestToLayoutStartEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToStartSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad + sectorAngleInRad;
        double newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = wheelLayoutManager.getPosition(closestToStartSectorView) - 1;
        int alreadyLayoutedChildrenCount = 0;

        while (newSectorViewBottomEdgeAngularPosInRad < wheelLayoutManager.getLayoutStartAngleInRad()
                && alreadyLayoutedChildrenCount < state.getItemCount()) {
            wheelLayoutManager.setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, false);
            newSectorViewLayoutAngle += sectorAngleInRad;
            newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos--;
            alreadyLayoutedChildrenCount++;
        }
    }
}
