package com.magicepg.util;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.DrawableRes;

import com.magicepg.App;
import com.magicepg.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import entity.Color;

/**
 * @author Alexey
 * @since 8/18/17
 */
public class ImageUtils {

    private static final List<Integer> AVAILABLE_IMAGES = Arrays.asList(
            R.drawable.img_1,
            R.drawable.img_2,
            R.drawable.img_3,
            R.drawable.img_4,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_7,
            R.drawable.img_8,
            R.drawable.img_9,
            R.drawable.img_10,
            R.drawable.img_11,
            R.drawable.img_12,
            R.drawable.img_13,
            R.drawable.img_14,
            R.drawable.img_15,
            R.drawable.img_16,
            R.drawable.img_17,
            R.drawable.img_18,
            R.drawable.img_19,
            R.drawable.img_20
    );

    private ImageUtils() {
        throw new AssertionError("No instances.");
    }

    public static Uri getRandomImageUri() {
        return getUriByDrawableResId(getRandomDrawableResId());
    }

    public static Color getRandomColor() {
        Random rnd = new Random();
        return Color.ofArgb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private static Uri getUriByDrawableResId(@DrawableRes int drawableResId) {
        final Resources resources = App.getInstance().getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(drawableResId) + '/' +
                resources.getResourceTypeName(drawableResId) + '/' +
                resources.getResourceEntryName(drawableResId));
    }

    @DrawableRes
    private static int getRandomDrawableResId() {
        final int randomPos = new Random().nextInt(AVAILABLE_IMAGES.size());
        return AVAILABLE_IMAGES.get(randomPos);
    }

}
