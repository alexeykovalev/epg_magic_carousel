package com.sss.magicwheel.util;

import android.graphics.Point;
import android.util.Log;
import android.view.Display;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class MagicCalculationHelper {

    public static final int SEGMENT_ANGULAR_HEIGHT = 30;

    private static final String TAG = MagicCalculationHelper.class.getCanonicalName();
    private static MagicCalculationHelper instance;

    private int screenWidth;
    private int screenHeight;

    private int innerRadius;
    private int outerRadius;

    public static MagicCalculationHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("initialize() has not been invoked yet.");
        }
        return instance;
    }

    private MagicCalculationHelper(Display display) {
        calcScreenSize(display);
        calcCircleDimens();
        instance = this;
    }

    public static void initialize(Display display) {
        instance = new MagicCalculationHelper(display);
    }

    private void calcScreenSize(Display display) {
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        Log.e(TAG, String.format("screenWidth [%s], screenHeight [%s]", screenWidth, screenHeight));
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public int getOuterRadius() {
        return outerRadius;
    }

    public int getAngularHeight() {
        return SEGMENT_ANGULAR_HEIGHT;
    }

    private void calcCircleDimens() {
        innerRadius = screenHeight / 2 + 50;
        outerRadius = innerRadius + 200;
    }

    public Point getCircleCenter() {
        return new Point(0, screenHeight / 2);
    }

    public double getStartAngle() {
        int halfScreen = screenHeight / 2;
        double x = Math.sqrt(innerRadius * innerRadius - halfScreen * halfScreen);

        return Math.atan(halfScreen/x);
    }

    public CoordinateHolder getStartIntercectForInnerRadius() {
        return CoordinateHolder.ofPolar(innerRadius, getStartAngle());
    }

//    public CoordinateHolder getStartIntercectForOuterRadius() {
//
//    }

    public CoordinateHolder toScreenCoordinates(CoordinateHolder from) {
        return new CoordinateHolder(from.getX(), from.getY() - screenHeight / 2);
    }

    public static class CoordinateHolder {

        private final double x;
        private final double y;

        public CoordinateHolder(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public static CoordinateHolder ofPolar(double radius, double angle) {
            return new CoordinateHolder(radius * Math.cos(angle), radius * Math.sin(angle));
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}