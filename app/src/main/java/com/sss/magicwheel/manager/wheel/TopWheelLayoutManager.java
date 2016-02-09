package com.sss.magicwheel.manager.wheel;

import android.content.Context;

import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class TopWheelLayoutManager extends AbstractWheelLayoutManager {

    /**
     * In order to make wheel infinite we have to set virtual position as start layout position.
     */
    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    public TopWheelLayoutManager(Context context, WheelComputationHelper computationHelper, WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        super(computationHelper, initialLayoutFinishingListener);
    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad();
//                - wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;
    }

    @Override
    protected int getStartLayoutFromAdapterPosition() {
        return START_LAYOUT_FROM_ADAPTER_POSITION;
    }
}
