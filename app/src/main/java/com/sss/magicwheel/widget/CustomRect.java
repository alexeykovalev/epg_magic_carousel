package com.sss.magicwheel.widget;

import com.sss.magicwheel.entity.CoordinatesHolder;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
@Deprecated
public class CustomRect {

    private final CoordinatesHolder topLeftCorner;
    private final CoordinatesHolder bottomRightCorner;

    public CustomRect(CoordinatesHolder topLeftCorner, CoordinatesHolder bottomRightCorner) {
        this.topLeftCorner = topLeftCorner;
        this.bottomRightCorner = bottomRightCorner;
    }

    public CoordinatesHolder getTopLeftCorner() {
        return topLeftCorner;
    }

    public CoordinatesHolder getBottomRightCorner() {
        return bottomRightCorner;
    }
}
