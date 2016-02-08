package com.sss.magicwheel.manager.wheel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Alexey Kovalev
 * @since 08.02.2016.
 */
public final class WheelSmoothScroller extends RecyclerView.SmoothScroller {

    private final AbstractWheelLayoutManager layoutManager;
    private final double targetSeekScrollDistanceInRad;

    public WheelSmoothScroller(AbstractWheelLayoutManager layoutManager, double targetSeekScrollDistanceInRad) {
        this.layoutManager = layoutManager;
        this.targetSeekScrollDistanceInRad = targetSeekScrollDistanceInRad;
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {

    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {

    }
}
