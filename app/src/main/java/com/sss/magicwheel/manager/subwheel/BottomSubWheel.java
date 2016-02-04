package com.sss.magicwheel.manager.subwheel;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;
import com.sss.magicwheel.manager.WheelRotationDirection;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class BottomSubWheel extends BaseSubWheel {

    protected BottomSubWheel(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void doInitialChildrenLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int startLayoutFromAdapterPosition, OnInitialLayoutFinishingListener layoutFinishingListener) {
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }

    @Override
    public double getLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad();
    }

    @Override
    public double getLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();
    }

    /*@Override
    public View getChildClosestToBottomEdge() {
        return wheelLayoutManager.getChildAt(0);
    }

    @Override
    public View getChildClosestToTopEdge() {
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }*/
}
