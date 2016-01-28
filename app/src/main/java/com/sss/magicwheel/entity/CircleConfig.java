package com.sss.magicwheel.entity;

import android.graphics.PointF;
import android.graphics.Rect;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class CircleConfig {

    public static final class AngularRestrictions {

        private final double sectorAngleInRad;
        private final double topEdgeAngleRestrictionInRad;
        private final double bottomEdgeAngleRestrictionInRad;

        public AngularRestrictions(double sectorAngleInRad,
                                   double topEdgeAngleRestrictionInRad,
                                   double bottomEdgeAngleRestrictionInRad) {
            this.sectorAngleInRad = sectorAngleInRad;
            this.topEdgeAngleRestrictionInRad = topEdgeAngleRestrictionInRad;
            this.bottomEdgeAngleRestrictionInRad = bottomEdgeAngleRestrictionInRad;
        }

        public double getSectorAngleInRad() {
            return sectorAngleInRad;
        }

        public double getTopEdgeAngleRestrictionInRad() {
            return topEdgeAngleRestrictionInRad;
        }

        public double getBottomEdgeAngleRestrictionInRad() {
            return bottomEdgeAngleRestrictionInRad;
        }

        @Override
        public String toString() {
            return "AngularRestrictions{" +
                    "sectorAngleInRad=" + sectorAngleInRad +
                    ", topEdgeAngleRestrictionInRad=" + topEdgeAngleRestrictionInRad +
                    ", bottomEdgeAngleRestrictionInRad=" + bottomEdgeAngleRestrictionInRad +
                    '}';
        }
    }


    /**
     * Relative to Recycler view top left corner.
     */
    private final PointF circleCenter;

    private final Rect circleBoundaries;

    private final int outerRadius;

    private final int innerRadius;

    private final AngularRestrictions angularRestrictions;

    public CircleConfig(PointF circleCenter, int outerRadius, int innerRadius, AngularRestrictions angularRestrictions) {
        this.circleCenter = circleCenter;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.angularRestrictions = angularRestrictions;
        this.circleBoundaries = computeCircleBoundariesRelativeToCircleCenter();
    }

    private Rect computeCircleBoundariesRelativeToCircleCenter() {
        return new Rect(-outerRadius, outerRadius, outerRadius, -outerRadius);
    }

    public PointF getCircleCenterRelToRecyclerView() {
        return safePointCopy(circleCenter);
    }

    public int getOuterRadius() {
        return outerRadius;
    }

    public int getInnerRadius() {
        return innerRadius;
    }

    public AngularRestrictions getAngularRestrictions() {
        return angularRestrictions;
    }

    @Deprecated
//    public Rect getCircleBoundariesRelativeToCircleCenter() {
//        return safeRectCopy(circleBoundaries);
//    }

    private static PointF safePointCopy(PointF source) {
        PointF res = new PointF();
        res.x = source.x;
        res.y = source.y;
        return res;
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
                ", angularRestrictions=" + angularRestrictions.toString() +
                '}';
    }
}
