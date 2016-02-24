package com.sss.magicwheel.coversflow.entity;

import com.sss.magicwheel.R;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoverEntity {

    public enum CoverType {
        DataItem, LeftOffset, RightOffset
    }

    private final String title;
    private final CoverType coverType;

    public CoverEntity(String title, CoverType coverType) {
        this.title = title;
        this.coverType = coverType;
    }

    public int getImageResource() {
        return R.drawable.second_cover;
    }

    public String getTitle() {
        return title;
    }

    public boolean isOffsetItem() {
        return coverType != CoverType.DataItem;
    }
}
