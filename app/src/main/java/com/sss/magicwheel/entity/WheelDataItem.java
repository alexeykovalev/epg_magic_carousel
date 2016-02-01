package com.sss.magicwheel.entity;

import android.graphics.Color;

import com.sss.magicwheel.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelDataItem {

    private static final int[] COVERS_LIST_DRAWABLE = new int[] {
            R.drawable.first_cover,
            R.drawable.second_cover
    };

    private static final int[] COLORS = new int[] {
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.WHITE,
            Color.CYAN,
            Color.MAGENTA
    };

    private static int coverImageCounter = 0;
    private static int colorCounter = 0;


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
    private final int sectorImageDrawable;

    public WheelDataItem(String title) {
        this.title = title;
        this.sectorLeftEdgeColor = createSectorLeftEdgeColor();
        this.sectorImageDrawable = createSectorImageDrawable();
    }

    private static int createSectorLeftEdgeColor() {
        final int res = COLORS[colorCounter % COLORS.length];
        colorCounter++;
        return res;
    }

    private static int createSectorImageDrawable() {
        int index = coverImageCounter % COVERS_LIST_DRAWABLE.length;
        coverImageCounter++;
        return COVERS_LIST_DRAWABLE[index];
    }

    public String getTitle() {
        return title;
    }

    public int getSectorLeftEdgeColor() {
        return sectorLeftEdgeColor;
    }

    public int getSectorImageDrawable() {
        return sectorImageDrawable;
    }
}
