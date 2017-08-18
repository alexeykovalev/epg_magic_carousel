package com.magicwheel;

import com.magicwheel.entity.WheelDataItem;

/**
 * @author Alexey Kovalev
 * @since 26.02.2017
 */
public interface WheelListener {

    enum WheelRotationState {
        InRotation, RotationStopped
    }

    /**
     * Triggers when wheel's sector has been selected, and informs interested
     * in this event side about data item associated with this sector.
     */
    void onDataItemSelected(WheelDataItem selectedDataItem);

    void onWheelRotationStateChange(WheelRotationState wheelRotationState);
}
