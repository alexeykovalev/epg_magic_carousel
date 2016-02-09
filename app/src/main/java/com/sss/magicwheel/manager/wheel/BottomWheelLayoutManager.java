package com.sss.magicwheel.manager.wheel;

import android.content.Context;

import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class BottomWheelLayoutManager extends AbstractWheelLayoutManager {

    // TODO: 05.02.2016 for testing
    @Deprecated
    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    public BottomWheelLayoutManager(Context context, WheelComputationHelper computationHelper, WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        super(computationHelper, initialLayoutFinishingListener);
    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad()
                + wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();
    }
}
