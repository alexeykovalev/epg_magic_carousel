package com.sss.magicwheel.entity;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelConfig {

    public static final class AngularRestrictions {

        public static final double NOT_DEFINED_ANGLE = Double.MAX_VALUE;

        private final double sectorAngleInRad;
        private final double sectorHalfAngleInRad;

        private final double wheelTopEdgeAngleRestrictionInRad;
        private final double wheelBottomEdgeAngleRestrictionInRad;

        private final double gapAreaTopEdgeAngleRestrictionInRad;
        private final double gapAreaBottomEdgeAngleRestrictionInRad;

        /**
         * Angle from which we have to actually start sectors layout.
         * Does not equal to {@link #wheelTopEdgeAngleRestrictionInRad}
         */
        private final double wheelLayoutStartAngle;

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
            this.wheelLayoutStartAngle = computeWheelLayoutStartAngle();
        }

        private double computeWheelLayoutStartAngle() {
            double res = gapAreaTopEdgeAngleRestrictionInRad;
            while (res <= wheelTopEdgeAngleRestrictionInRad) {
                res += sectorAngleInRad;
            }
            return res;
        }

        public double getSectorAngleInRad() {
            return sectorAngleInRad;
        }

        public double getSectorHalfAngleInRad() {
            return sectorHalfAngleInRad;
        }

        /**
         * Be careful of using this. Don't confuse with {@link #getWheelLayoutStartAngleInRad}
         * which most probably has to be used instead of this.
         */
        public double getWheelTopEdgeAngleRestrictionInRad() {
            return wheelTopEdgeAngleRestrictionInRad;
        }

        /**
         * Layout will be performed from top to bottom direction. And we should have sector
         * positioned parallel to central diameter. So taking into account imposed angular restrictions
         * we should compute actual layout start angle.
         * <p/>
         * So the firstly layouted sector's top edge will be aligned by this angle.
         */
        public double getWheelLayoutStartAngleInRad() {
            return wheelLayoutStartAngle;
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

        // TODO: 03.02.2016 Guava toStringHelper() should be here
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

    // TODO: 03.02.2016 do we need safe copy or not is an open question
    public PointF getCircleCenterRelToRecyclerView() {
        return circleCenter;
//        return safePointCopy(circleCenter);
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
