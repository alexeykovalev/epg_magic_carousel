package com.sss.magicwheel.entity;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelDataItem {

    private static int colorCounter = 0;

    private static final int[] COLORS = new int[] {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.WHITE,
            Color.CYAN,
            Color.MAGENTA
    };

    private static final Map<Integer, String> COLOR_TO_STRING_PRESENTATION = new HashMap<>();

    static {
        COLOR_TO_STRING_PRESENTATION.put(Color.RED, "RED");
        COLOR_TO_STRING_PRESENTATION.put(Color.GREEN, "GREEN");
        COLOR_TO_STRING_PRESENTATION.put(Color.BLUE, "BLUE");
        COLOR_TO_STRING_PRESENTATION.put(Color.WHITE, "WHITE");
        COLOR_TO_STRING_PRESENTATION.put(Color.CYAN, "CYAN");
        COLOR_TO_STRING_PRESENTATION.put(Color.MAGENTA, "MAGENTA");
    }

    // for debug purposes
    public static String colorToString(int colorAsInt) {
        return COLOR_TO_STRING_PRESENTATION.get(colorAsInt);
    }

    private final String title;
    private final int sectorLeftEdgeColor;
    private final Random colorRandomizer = new Random();

    public WheelDataItem(String title) {
        this.title = title;
        this.sectorLeftEdgeColor = getColor();
    }

    private static int getColor() {
//        final int colorIndex = colorRandomizer.nextInt(COLORS.length);
//        return COLORS[colorIndex];
        final int res = COLORS[colorCounter % COLORS.length];
        colorCounter++;
        return res;
    }

    public String getTitle() {
        return title;
    }

    public int getSectorLeftEdgeColor() {
        return sectorLeftEdgeColor;
    }
}
