package com.sss.magicwheel.entity;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelConfig {

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

    private final RectF circleBoundaries;

    private final int outerRadius;

    private final int innerRadius;

    private final AngularRestrictions angularRestrictions;

    public WheelConfig(PointF circleCenter, int outerRadius, int innerRadius, AngularRestrictions angularRestrictions) {
        this.circleCenter = circleCenter;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.angularRestrictions = angularRestrictions;
        this.circleBoundaries = computeCircleBoundariesRelativeToCircleCenter();
    }

    private RectF computeCircleBoundariesRelativeToCircleCenter() {
        return new RectF(-outerRadius, outerRadius, outerRadius, -outerRadius);
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

    @Override
    public String toString() {
        return "WheelConfig{" +
                "circleCenter=" + circleCenter +
                ", circleBoundaries=" + circleBoundaries.toShortString() +
                ", outerRadius=" + outerRadius +
                ", innerRadius=" + innerRadius +
                ", angularRestrictions=" + angularRestrictions.toString() +
                '}';
    }
}
