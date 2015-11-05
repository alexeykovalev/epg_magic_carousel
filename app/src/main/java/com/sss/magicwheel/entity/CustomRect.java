package com.sss.magicwheel.entity;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
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
