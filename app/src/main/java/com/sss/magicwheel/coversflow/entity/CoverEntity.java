package com.sss.magicwheel.coversflow.entity;

import com.sss.magicwheel.R;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoverEntity {

    private final String title;

    public CoverEntity(String title) {
        this.title = title;
    }

    public int getImageResource() {
        return R.drawable.second_cover;
    }

    public String getTitle() {
        return title;
    }
}
