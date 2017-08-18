package com.magicwheel.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.magicwheel.manager.AbstractWheelLayoutManager;
import com.magicwheel.util.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 03.02.2017
 */
public final class TopClockwiseWheelRotator extends AbstractClockwiseWheelRotator {

    public TopClockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    protected void recycleSectorsFromBottomIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = wheelLayoutManager.getChildCount() - 1; i >= 0; i--) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewTopEdgeAngularPosInRad < wheelLayoutManager.getLayoutStartAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
            }
        }
    }

    @Override
    protected void addSectorsToTopInNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToEndSectorView = wheelLayoutManager.getChildClosestToLayoutEndEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToEndSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad + sectorAngleInRad;
        double newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = wheelLayoutManager.getPosition(closestToEndSectorView) + 1;
        int alreadyLayoutedChildrenCount = 0;

        while (newSectorViewBottomEdgeAngularPosInRad < wheelLayoutManager.getLayoutEndAngleInRad()
                && alreadyLayoutedChildrenCount < state.getItemCount()) {
            wheelLayoutManager.setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, true);
            newSectorViewLayoutAngle += sectorAngleInRad;
            newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos++;
            alreadyLayoutedChildrenCount++;
        }
    }
}
