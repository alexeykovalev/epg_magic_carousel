package com.magicepg.coversflow;

import android.net.Uri;

import com.magicepg.func.Optional;

import org.apache.commons.lang3.StringUtils;

import entity.Color;


/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoverEntity {

    private final String title;
    private final Uri coverImageUri;
    private final Color coverColor;

    public CoverEntity(String title, Uri coverImageUri, Color coverColor) {
        this.title = title;
        this.coverImageUri = coverImageUri;
        this.coverColor = coverColor;
    }

    public String getTitle() {
        return StringUtils.defaultString(title);
    }

    public Optional<Uri> getCoverImageUri() {
        return Optional.fromNullable(coverImageUri);
    }

    public Color getCoverColor() {
        return coverColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoverEntity that = (CoverEntity) o;

        if (!title.equals(that.title)) return false;
        return coverImageUri.equals(that.coverImageUri);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + coverImageUri.hashCode();
        return result;
    }

}
