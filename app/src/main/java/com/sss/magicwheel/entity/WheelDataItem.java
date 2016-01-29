package com.sss.magicwheel.entity;

import android.graphics.Color;

import java.util.Random;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelDataItem {

    private static final int[] COLORS = new int[] {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.WHITE
    };

    private final String title;
    private final int sectorLeftEdgeColor;
    private final Random colorRandomizer = new Random();

    public WheelDataItem(String title) {
        this.title = title;
        this.sectorLeftEdgeColor = getRandomColor();
    }

    private int getRandomColor() {
        final int colorIndex = colorRandomizer.nextInt(COLORS.length);
        return COLORS[colorIndex];
    }

    public String getTitle() {
        return title;
    }

    public int getSectorLeftEdgeColor() {
        return sectorLeftEdgeColor;
    }
}
