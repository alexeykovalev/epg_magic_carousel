package com.sss.magicwheel.manager.layouter;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public class BottomSubWheelLayouter extends BaseSubWheelLayouter {


    protected BottomSubWheelLayouter(WheelOfFortuneLayoutManager wheelLayoutManager) {
        super(wheelLayoutManager);
    }

    @Override
    public void doInitialChildrenLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int startLayoutFromAdapterPosition) {

    }
}
