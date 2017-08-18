package com.magicwheel.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.magicwheel.manager.AbstractWheelLayoutManager;
import com.magicwheel.util.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 03.02.2017
 */
public final class TopAnticlockwiseWheelRotator extends AbstractAnticlockwiseWheelRotator {

    public TopAnticlockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    protected void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewBottomEdgeAngularPosInRad > wheelLayoutManager.getLayoutEndAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
            }
        }
    }

    @Override
    protected void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToStartSectorView = wheelLayoutManager.getChildClosestToLayoutStartEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToStartSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
        final double bottomLayoutAngleInRad = wheelLayoutManager.getLayoutStartAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad - sectorAngleInRad;
        double newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = wheelLayoutManager.getPosition(closestToStartSectorView) - 1;
        int alreadyLayoutedChildrenCount = 0;

        while (newSectorViewTopEdgeAngularPosInRad > bottomLayoutAngleInRad
                && alreadyLayoutedChildrenCount < state.getItemCount()) {
            wheelLayoutManager.setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, false);
            newSectorViewLayoutAngle -= sectorAngleInRad;
            newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos--;
            alreadyLayoutedChildrenCount++;
        }
    }
}