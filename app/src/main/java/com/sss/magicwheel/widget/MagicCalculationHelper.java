package com.sss.magicwheel.widget;

import android.graphics.Point;
import android.util.Log;
import android.view.Display;

import com.sss.magicwheel.wheel.entity.CoordinatesHolder;

/**
 * @author Alexey
 * @since 05.11.2015
 */
@Deprecated
public class MagicCalculationHelper {

    public static final int SEGMENT_ANGULAR_HEIGHT = 30;

    public static final double FROM_RADIAN_TO_GRAD_COEF = 180 / Math.PI;

    public static final double TEST_ANGLE_STEP_IN_RAD = Math.PI / 16;

    private static final String TAG = MagicCalculationHelper.class.getCanonicalName();
    private static MagicCalculationHelper instance;

    private int screenWidth;
    private int screenHeight;

    private int innerRadius;
    private int outerRadius;

    private MagicCalculationHelper(Display display) {
        calcScreenSize(display);
        calcCircleDimens();
        instance = this;
    }

    public static MagicCalculationHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("initialize() has not been invoked yet.");
        }
        return instance;
    }

    public static void initialize(Display display) {
        instance = new MagicCalculationHelper(display);
    }

    // ----------------------------------------------------

    public CoordinatesHolder toViewCoordinateSystem(CoordinatesHolder pointInCircleSystemCoord,
                                                    CoordinatesHolder leftTopCornerViewCoordInCircleSystem) {

        double inViewSystemX = pointInCircleSystemCoord.getX() - leftTopCornerViewCoordInCircleSystem.getX();
        double inViewSystemY = leftTopCornerViewCoordInCircleSystem.getY() - pointInCircleSystemCoord.getY();
        return CoordinatesHolder.ofRect(inViewSystemX, inViewSystemY);
    }

    public static double fromRadToDegree(double valInRad) {
        return valInRad * FROM_RADIAN_TO_GRAD_COEF;
    }

    public CoordinatesHolder toScreenCoordinates(CoordinatesHolder from) {
        return CoordinatesHolder.ofRect(from.getX(), screenHeight / 2 - from.getY());
    }

    private void calcScreenSize(Display display) {
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        Log.e(TAG, String.format("screenWidth [%s], screenHeight [%s]", screenWidth, screenHeight));
    }

    private void calcCircleDimens() {
        innerRadius = screenHeight / 2 + 50;
        outerRadius = innerRadius + 200;
    }

    public CustomRect getOvalCoordsInCircleSystem() {
        CoordinatesHolder topLeft = CoordinatesHolder.ofRect(-outerRadius, outerRadius);
        CoordinatesHolder bottomRight = CoordinatesHolder.ofRect(outerRadius, -outerRadius);
        return new CustomRect(topLeft, bottomRight);
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

    public Point getCircleCenter() {
        return new Point(0, screenHeight / 2);
    }

    public double getStartAngle() {
        int halfScreen = screenHeight / 2;
        double x = Math.sqrt(innerRadius * innerRadius - halfScreen * halfScreen);
        return Math.atan(halfScreen / x);
    }

    public CoordinatesHolder getStartIntersectForInnerRadius() {
        return getIntersectionByAngle(innerRadius, getStartAngle());
    }

    public CoordinatesHolder getStartIntercectForOuterRadius() {
        return getIntersectionByAngle(outerRadius, getStartAngle());
    }

    public CoordinatesHolder getIntersectionByAngle(int radius, double angleInRad) {
        return CoordinatesHolder.ofPolar(radius, angleInRad);
    }


    public CoordinatesHolder getViewPositionForAngle(double angleInRad) {
        CoordinatesHolder innerIntersection = getIntersectionByAngle(innerRadius, angleInRad);
        CoordinatesHolder outerIntersection = getIntersectionByAngle(outerRadius, angleInRad);
        return CoordinatesHolder.ofRect(innerIntersection.getX(), outerIntersection.getY());
    }

}