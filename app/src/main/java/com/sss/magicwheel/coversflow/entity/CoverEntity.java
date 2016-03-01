package com.sss.magicwheel.coversflow.entity;

import com.sss.magicwheel.R;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoverEntity {

    public static final int NOT_DEFINED_OFFSET_VALUE = Integer.MIN_VALUE;
    private static final int NO_COVER_ID = -1;

    private static final int[] COVERS_LIST_DRAWABLE = new int[] {
            R.drawable.first_cover,
            R.drawable.second_cover
    };

    private static int coverImageCounter;

    private static int createCoverImageDrawable() {
        int index = coverImageCounter % COVERS_LIST_DRAWABLE.length;
        coverImageCounter++;
        return COVERS_LIST_DRAWABLE[index];
    }

    private final String title;
    private final int coverImageDrawableId;
    private final int offsetValue;

    public static CoverEntity dataItem(String title) {
        return new CoverEntity(title, createCoverImageDrawable(), NOT_DEFINED_OFFSET_VALUE);
    }

    public static CoverEntity offsetItem(int offsetValue) {
        return new CoverEntity("", NO_COVER_ID, offsetValue);
    }

    private CoverEntity(String title, int coverImageDrawableId, int offsetValue) {
        this.title = title;
        this.offsetValue = offsetValue;
        this.coverImageDrawableId = coverImageDrawableId;
    }

    public int getImageResource() {
        return coverImageDrawableId;
    }

    public String getTitle() {
        return title;
    }

    public int getOffsetValue() {
        return offsetValue;
    }

    public boolean isOffsetItem() {
        return offsetValue != NOT_DEFINED_OFFSET_VALUE;
    }
}
