package com.sss.magicwheel.manager.layouter;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class BottomSubWheelLayouter extends BaseSubWheelLayouter {

    protected BottomSubWheelLayouter(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void doInitialChildrenLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int startLayoutFromAdapterPosition, OnInitialLayoutFinishingListener layoutFinishingListener) {

    }
}
