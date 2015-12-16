package com.sss.magicwheel.manager;

/**
 * @author Alexey Kovalev
 * @since 16.12.2015.
 */
public enum WheelRotationDirection {

    Clockwise(-1), Anticlockwise(1);

    /**
     * For swipe up gesture direction (delta value) will be positive and
     * negative for swipe down -> i.e. dy > 0 for swipe up (anticlockwise wheel rotation)
     */
    final int direction;

    /**
     * When we move from circle's HEAD to TAIL (anticlockwise) - we increase
     * adapter position, and decrease it when scrolling clockwise.
     */
    final int adapterPositionIncrementation;

    WheelRotationDirection(int directionSignum) {
        this.direction = directionSignum;
        this.adapterPositionIncrementation = directionSignum;
    }

    public static WheelRotationDirection of(int directionAsInt) {
        return directionAsInt < 0 ? Clockwise : Anticlockwise;
    }
}
