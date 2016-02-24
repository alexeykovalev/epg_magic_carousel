package com.sss.magicwheel.coversflow.entity;

import com.sss.magicwheel.R;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoverEntity {

    public static final int NOT_DEFINED_OFFSET_VALUE = Integer.MIN_VALUE;

    private final String title;
    private final int offsetValue;

    public static CoverEntity dataItem(String title) {
        return new CoverEntity(title, NOT_DEFINED_OFFSET_VALUE);
    }

    public static CoverEntity offsetItem(int offsetValue) {
        return new CoverEntity("", offsetValue);
    }

    private CoverEntity(String title, int offsetValue) {
        this.title = title;
        this.offsetValue = offsetValue;
    }

    public int getImageResource() {
        return R.drawable.second_cover;
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
