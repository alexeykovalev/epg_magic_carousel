package com.sss.magicwheel.manager;

import android.graphics.Point;
import android.graphics.Rect;

import com.sss.magicwheel.entity.CoordinatesHolder;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class CircleConfig {

    /**
     * Relative to Recycler view top left corner.
     */
    private final Point circleCenter;

    private final Rect circleBoundaries;

    private final int outerRadius;

    private final int innerRadius;

    private final int sectorAngleInDegree;


    public CircleConfig(Point circleCenter, int outerRadius, int innerRadius, int sectorAngleInDegree) {
        this.circleCenter = circleCenter;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.sectorAngleInDegree = sectorAngleInDegree;
        this.circleBoundaries = computeCircleBoundariesRelativeToCircleCenter();
    }

    private Rect computeCircleBoundariesRelativeToCircleCenter() {
        return new Rect(-outerRadius, outerRadius, outerRadius, -outerRadius);
    }

    public Point getCircleCenterRelToRecyclerView() {
        return safePointCopy(circleCenter);
    }

    public int getOuterRadius() {
        return outerRadius;
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public double getSectorAngleInRad() {
        return WheelUtils.degreeToRadian(sectorAngleInDegree);
    }

    public Rect getCircleBoundariesRelativeToCircleCenter() {
        return circleBoundaries;
    }

    private static Point safePointCopy(Point source) {
        return new Point(source);
    }

    private static Rect safeRectCopy(Rect source) {
        return new Rect(source);
    }

    @Override
    public String toString() {
        return "CircleConfig{" +
                "circleCenter=" + circleCenter +
                ", circleBoundaries=" + circleBoundaries.toShortString() +
                ", outerRadius=" + outerRadius +
                ", innerRadius=" + innerRadius +
                ", sectorAngleInDegree=" + sectorAngleInDegree +
                '}';
    }
}
