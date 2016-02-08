package com.sss.magicwheel.manager.wheel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 08.02.2016.
 */
public final class WheelSmoothScroller extends RecyclerView.SmoothScroller {

    private static final String TAG = WheelSmoothScroller.class.getCanonicalName();

    private final AbstractWheelLayoutManager layoutManager;
    private final WheelComputationHelper computationHelper;
    private final double targetSeekScrollDistanceInRad;

    public WheelSmoothScroller(AbstractWheelLayoutManager layoutManager,
                               WheelComputationHelper computationHelper,
                               double targetSeekScrollDistanceInRad) {
        this.layoutManager = layoutManager;
        this.computationHelper = computationHelper;
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
        Log.e(TAG, "");
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        Log.e(TAG, "");
    }
}
