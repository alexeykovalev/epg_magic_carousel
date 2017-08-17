package com.magicwheel.entity;

import android.net.Uri;

import com.magicwheel.func.Optional;

import org.apache.commons.lang3.StringUtils;


/**
 * @author Alexey Kovalev
 * @since 04.12.2016
 */
public final class WheelDataItem {

    public static final Color DEFAULT_LEFT_EDGE_COLOR = Color.WHITE;

    private final String title;
    private final Color leftEdgeColor;
    private final Uri coverUri;

    public WheelDataItem(String title, Color leftEdgeColor, Uri coverUri) {
        this.title = title;
        this.leftEdgeColor = leftEdgeColor;
        this.coverUri = coverUri;
    }

    public String getTitle() {
        return StringUtils.defaultString(title);
    }

    public Color getLeftEdgeColor() {
        return leftEdgeColor;
    }

    public Optional<Uri> getCoverUri() {
        return Optional.fromNullable(coverUri);
    }

    public boolean hasCover() {
        return getCoverUri().isPresent();
    }

}
