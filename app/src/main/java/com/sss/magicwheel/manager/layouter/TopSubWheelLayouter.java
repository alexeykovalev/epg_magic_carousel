package com.sss.magicwheel.manager.layouter;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class TopSubWheelLayouter extends BaseSubWheelLayouter {


    protected TopSubWheelLayouter(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void doInitialChildrenLayout(RecyclerView.Recycler recycler,
                                        RecyclerView.State state,
                                        int startLayoutFromAdapterPosition,
                                        OnInitialLayoutFinishingListener layoutFinishingListener) {

        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
        final double bottomLimitAngle = wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();

        double layoutAngle = computationHelper.getWheelLayoutStartAngleInRad();
        int childPos = startLayoutFromAdapterPosition;
        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
            wheelLayoutManager.setupViewForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }
    }


}
