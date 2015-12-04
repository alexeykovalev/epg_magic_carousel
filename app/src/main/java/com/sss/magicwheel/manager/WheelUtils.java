package com.sss.magicwheel.manager;

import android.graphics.Point;
import android.graphics.Rect;

import com.sss.magicwheel.entity.CoordinatesHolder;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelUtils {

    private static final double DEGREE_TO_RAD_COEF = Math.PI / 180;
    private static final double RAD_TO_DEGREE_COEF = 1 / DEGREE_TO_RAD_COEF;

    private WheelUtils() {
    }

    public static double degreeToRadian(int angleInDegree) {
        return angleInDegree * DEGREE_TO_RAD_COEF;
    }

    public static int radToDegree(double angleInRad) {
        return (int) (angleInRad * RAD_TO_DEGREE_COEF);
    }

    public static Rect fromCircleCoordsSystemToRecyclerViewCoordsSystem(Point circleCenterRelToRecyclerView,
                                                                        Rect coorditanesToTransform) {

        final Point leftTopCorner = fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleCenterRelToRecyclerView,
                new Point(coorditanesToTransform.left, coorditanesToTransform.top)
        );

        final Point rightBottomCorner = fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleCenterRelToRecyclerView,
                new Point(coorditanesToTransform.right, coorditanesToTransform.bottom)
        );

        return new Rect(leftTopCorner.x, leftTopCorner.y, rightBottomCorner.x, rightBottomCorner.y);
    }

    public static Point fromCircleCoordsSystemToRecyclerViewCoordsSystem(Point circleCenterRelToRecyclerView,
                                                                         Point pointToTransform) {
        return new Point(
                circleCenterRelToRecyclerView.x + pointToTransform.x,
                circleCenterRelToRecyclerView.y - pointToTransform.y
        );
    }
}
