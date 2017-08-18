package com.magicepg.wheel.entity;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Retains general wheel restriction (measurements) configuration.
 *
 * @author Alexey Kovalev
 * @since 04.12.2016
 */
public final class WheelConfig {

    /**
     * Angular restrictions imposed to wheel.
     */
    public static final class AngularRestrictions {

        private final double sectorAngleInRad;
        private final double sectorHalfAngleInRad;

        private final double wheelTopEdgeAngleRestrictionInRad;
        private final double wheelBottomEdgeAngleRestrictionInRad;

        private final double gapAreaTopEdgeAngleRestrictionInRad;
        private final double gapAreaBottomEdgeAngleRestrictionInRad;

        public static Builder builder(double sectorAngleInRad) {
            return new Builder(sectorAngleInRad);
        }

        private AngularRestrictions(Builder builder) {
            this.sectorAngleInRad = builder.sectorAngleInRad;
            this.sectorHalfAngleInRad = sectorAngleInRad / 2;
            this.wheelTopEdgeAngleRestrictionInRad = builder.wheelTopEdgeAngleRestrictionInRad;
            this.wheelBottomEdgeAngleRestrictionInRad = builder.wheelBottomEdgeAngleRestrictionInRad;
            this.gapAreaTopEdgeAngleRestrictionInRad = builder.gapAreaTopEdgeAngleRestrictionInRad;
            this.gapAreaBottomEdgeAngleRestrictionInRad = builder.gapAreaBottomEdgeAngleRestrictionInRad;
        }

        public double getSectorAngleInRad() {
            return sectorAngleInRad;
        }

        public double getSectorHalfAngleInRad() {
            return sectorHalfAngleInRad;
        }

        public double getWheelTopEdgeAngleRestrictionInRad() {
            return wheelTopEdgeAngleRestrictionInRad;
        }

        public double getWheelBottomEdgeAngleRestrictionInRad() {
            return wheelBottomEdgeAngleRestrictionInRad;
        }

        public double getGapAreaTopEdgeAngleRestrictionInRad() {
            return gapAreaTopEdgeAngleRestrictionInRad;
        }

        public double getGapAreaBottomEdgeAngleRestrictionInRad() {
            return gapAreaBottomEdgeAngleRestrictionInRad;
        }

        // TODO: WheelOfFortune 03.02.2016 Guava toStringHelper() should be here
        @Override
        public String toString() {
            return "AngularRestrictions{" +
                    "sectorAngleInRad=" + sectorAngleInRad +
                    ", wheelTopEdgeAngleRestrictionInRad=" + wheelTopEdgeAngleRestrictionInRad +
                    ", wheelBottomEdgeAngleRestrictionInRad=" + wheelBottomEdgeAngleRestrictionInRad +
                    ", gapAreaTopEdgeAngleRestrictionInRad=" + gapAreaTopEdgeAngleRestrictionInRad +
                    ", gapAreaBottomEdgeAngleRestrictionInRad=" + gapAreaBottomEdgeAngleRestrictionInRad +
                    '}';
        }

        public static final class Builder {

            private final double sectorAngleInRad;

            private double wheelTopEdgeAngleRestrictionInRad = Math.PI;
            private double wheelBottomEdgeAngleRestrictionInRad = -Math.PI;

            private double gapAreaTopEdgeAngleRestrictionInRad = Math.PI / 6;
            private double gapAreaBottomEdgeAngleRestrictionInRad = -Math.PI / 6;

            private Builder(double sectorAngleInRad) {
                this.sectorAngleInRad = sectorAngleInRad;
            }

            public AngularRestrictions build() {
                return new AngularRestrictions(this);
            }

            public Builder wheelEdgesAngularRestrictions(double topEdgeAngleRestrictionInRad, double bottomEdgeAngleRestrictionInRad) {
                ensureAngleInBounds(topEdgeAngleRestrictionInRad);
                ensureAngleInBounds(bottomEdgeAngleRestrictionInRad);
                this.wheelTopEdgeAngleRestrictionInRad = topEdgeAngleRestrictionInRad;
                this.wheelBottomEdgeAngleRestrictionInRad = bottomEdgeAngleRestrictionInRad;
                return this;
            }

            public Builder gapEdgesAngularRestrictions(double topEdgeAngleRestrictionInRad, double bottomEdgeAngleRestrictionInRad) {
                ensureAngleInBounds(topEdgeAngleRestrictionInRad);
                ensureAngleInBounds(bottomEdgeAngleRestrictionInRad);
                this.gapAreaTopEdgeAngleRestrictionInRad = topEdgeAngleRestrictionInRad;
                this.gapAreaBottomEdgeAngleRestrictionInRad = bottomEdgeAngleRestrictionInRad;
                return this;
            }

            private static void ensureAngleInBounds(double angleToCheck) {
                if (angleToCheck < -Math.PI || angleToCheck > Math.PI) {
                    throw new IllegalArgumentException("You have to specify angle in bounds [-PI; +PI] but " +
                            "actually passed value is [" + angleToCheck + "]");
                }
            }
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
        return circleCenter;
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
