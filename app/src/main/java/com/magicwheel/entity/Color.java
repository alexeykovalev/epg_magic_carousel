package com.magicwheel.entity;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;

import com.magicwheel.func.Optional;

/**
 * @author Alexey
 * @since 5/1/17
 */
public final class Color {

    public static final int MAX_COMPONENT_VALUE = 255;

    public static final Color WHITE = ofRgb(0xFF, 0xFF, 0xFF);
    public static final Color BLACK = ofRgb(0, 0, 0);
    public static final Color TRANSPARENT = WHITE.fullyTransparent();

    public static final class ColorComponents {

        private final int alpha;
        private final int red;
        private final int green;
        private final int blue;

        ColorComponents(@IntRange(from = 0, to = MAX_COMPONENT_VALUE) int alpha,
                        @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int red,
                        @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int green,
                        @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int blue) {
            this.alpha = alpha;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        static ColorComponents ofPackedColor(@ColorInt int packedColor) {
            return new ColorComponents(
                    android.graphics.Color.alpha(packedColor),
                    android.graphics.Color.red(packedColor),
                    android.graphics.Color.green(packedColor),
                    android.graphics.Color.blue(packedColor)
            );
        }

        ColorComponents withAlpha(int alphaValue) {
            return new ColorComponents(alphaValue, red, green, blue);
        }

        ColorComponents withRed(int redValue) {
            return new ColorComponents(alpha, redValue, green, blue);
        }

        ColorComponents withGreen(int greenValue) {
            return new ColorComponents(alpha, red, greenValue, blue);
        }

        ColorComponents withBlue(int blueValue) {
            return new ColorComponents(alpha, red, green, blueValue);
        }

        Color toColor() {
            return new Color(this);
        }

        public int getAlpha() {
            return alpha;
        }

        public int getRed() {
            return red;
        }

        public int getGreen() {
            return green;
        }

        public int getBlue() {
            return blue;
        }

        @ColorInt
        public int toPackedInt() {
            return android.graphics.Color.argb(
                    getAlpha(),
                    getRed(),
                    getGreen(),
                    getBlue()
            );
        }
    }

    private final ColorComponents colorComponents;

    private Color(ColorComponents colorComponents) {
        this.colorComponents = colorComponents;
    }

    public static Color ofArgb(@IntRange(from = 0, to = MAX_COMPONENT_VALUE) int alpha,
                               @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int red,
                               @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int green,
                               @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int blue) {
        return new ColorComponents(alpha, red, green, blue).toColor();
    }

    public static Color ofRgb(@IntRange(from = 0, to = MAX_COMPONENT_VALUE) int red,
                              @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int green,
                              @IntRange(from = 0, to = MAX_COMPONENT_VALUE) int blue) {
        return ofArgb(MAX_COMPONENT_VALUE, red, green, blue);
    }

    /**
     * Creates color from {@code AARRGGBB} notation.
     */
    public static Color ofPackedColor(@ColorInt int packedColor) {
        return new Color(ColorComponents.ofPackedColor(packedColor));
    }

    public static Optional<Color> ofHexString(String colorHexPresentation) {
        try {
            int packedColor = android.graphics.Color.parseColor(colorHexPresentation);
            return Optional.of(ofPackedColor(packedColor));
        } catch (Exception e) {
            return Optional.absent();
        }
    }

    /**
     * Creates {@link Color} instance from provided color's resource Id.
     */
    public static Color ofResId(Context context, @ColorRes int colorResId) {
        return ofPackedColor(ContextCompat.getColor(context, colorResId));
    }

    public Color fullyTransparent() {
        return colorComponents.withAlpha(0).toColor();
    }

    public Color fullyOpaque() {
        return colorComponents.withAlpha(MAX_COMPONENT_VALUE).toColor();
    }

    public Color withAlphaComponent(@IntRange(from = 0, to = MAX_COMPONENT_VALUE) int alpha) {
        return colorComponents.withAlpha(alpha).toColor();
    }

    public Color withRedComponent(@IntRange(from = 0, to = MAX_COMPONENT_VALUE) int red) {
        return colorComponents.withRed(red).toColor();
    }

    public Color withGreenComponent(@IntRange(from = 0, to = MAX_COMPONENT_VALUE) int green) {
        return colorComponents.withGreen(green).toColor();
    }

    public Color withBlueComponent(@IntRange(from = 0, to = MAX_COMPONENT_VALUE) int blue) {
        return colorComponents.withBlue(blue).toColor();
    }

    public ColorComponents getColorComponents() {
        return colorComponents;
    }

    public Color getContrastColor() {
        // Magic code with magic constants here :)
        final int colorAsInt = toPackedInt();
        final double y = (
                299 * android.graphics.Color.red(colorAsInt) +
                        587 * android.graphics.Color.green(colorAsInt) +
                        114 * android.graphics.Color.blue(colorAsInt)
        ) / 1000;
        return y >= 128 ? BLACK : WHITE;
    }

    /**
     * Returns color in {@code AARRGGBB} notation.
     */
    @ColorInt
    public int toPackedInt() {
        return colorComponents.toPackedInt();
    }

}
