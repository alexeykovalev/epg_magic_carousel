package com.magicepg.wheel.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.magicepg.wheel.layout.AbstractWheelLayoutManager;
import com.magicepg.wheel.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 14.04.2017
 */
public final class BottomAnticlockwiseWheelRotator extends AbstractAnticlockwiseWheelRotator {

    public BottomAnticlockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    protected void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewBottomEdgeAngularPosInRad > wheelLayoutManager.getLayoutStartAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
            }
        }
    }

    @Override
    protected void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToEndSectorView = wheelLayoutManager.getChildClosestToLayoutEndEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToEndSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
        final double bottomEndLayoutAngleInRad = wheelLayoutManager.getLayoutEndAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad - sectorAngleInRad;
        double newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = wheelLayoutManager.getPosition(closestToEndSectorView) - 1;
        int alreadyLayoutedChildrenCount = 0;

        while (newSectorViewTopEdgeAngularPosInRad > bottomEndLayoutAngleInRad
                && alreadyLayoutedChildrenCount < state.getItemCount()) {
            wheelLayoutManager.setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, true);
            newSectorViewLayoutAngle -= sectorAngleInRad;
            newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos--;
            alreadyLayoutedChildrenCount++;
        }
    }
}
