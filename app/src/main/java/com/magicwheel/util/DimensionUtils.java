package com.magicwheel.util;

import android.view.View;
import android.view.ViewGroup;

import com.magicwheel.App;

public class DimensionUtils {

    private DimensionUtils() {
        throw new AssertionError("No instances.");
    }

    public static float pixelsToSp(float px) {
        float scaledDensity = App.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static float spToPixels(int valueInSp) {
        float scaledDensity = App.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return valueInSp * scaledDensity;
    }

    public static float pixelsToDp(final float px) {
        return px / App.getInstance().getResources().getDisplayMetrics().density;
    }

    public static float dpToPixels(int valueInDp) {
        return (valueInDp * App.getInstance().getResources().getDisplayMetrics().density);
    }

    public static void setMarginsForView(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

}
