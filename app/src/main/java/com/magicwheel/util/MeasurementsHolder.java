package com.magicwheel.util;

/**
 * @author Alexey Kovalev
 * @since 03.02.2017
 */
public final class MeasurementsHolder {

    private final int width;
    private final int height;

    public MeasurementsHolder(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
