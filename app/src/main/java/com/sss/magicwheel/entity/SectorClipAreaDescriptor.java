package com.sss.magicwheel.entity;

import android.graphics.RectF;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
public final class SectorClipAreaDescriptor {

    public static final class CircleEmbracingSquaresConfig {

        private RectF outerCircleEmbracingSquareInSectorWrapperCoordsSystem;
        private RectF innerCircleEmbracingSquareInSectorWrapperCoordsSystem;

        public CircleEmbracingSquaresConfig(RectF outerCircleEmbracingSquareInSectorWrapperCoordsSystem, RectF innerCircleEmbracingSquareInSectorWrapperCoordsSystem) {
            this.outerCircleEmbracingSquareInSectorWrapperCoordsSystem = outerCircleEmbracingSquareInSectorWrapperCoordsSystem;
            this.innerCircleEmbracingSquareInSectorWrapperCoordsSystem = innerCircleEmbracingSquareInSectorWrapperCoordsSystem;
        }

        public RectF getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem() {
            return outerCircleEmbracingSquareInSectorWrapperCoordsSystem;
        }

        public RectF getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem() {
            return innerCircleEmbracingSquareInSectorWrapperCoordsSystem;
        }

        @Override
        public String toString() {
            return "CircleEmbracingSquaresConfig{" +
                    "outerCircleEmbracingSquareInSectorWrapperCoordsSystem=" + outerCircleEmbracingSquareInSectorWrapperCoordsSystem.toShortString() +
                    ", innerCircleEmbracingSquareInSectorWrapperCoordsSystem=" + innerCircleEmbracingSquareInSectorWrapperCoordsSystem.toShortString() +
                    '}';
        }
    }

    private final CoordinatesHolder first;
    private final CoordinatesHolder second;
    private final CoordinatesHolder third;
    private final CoordinatesHolder fourth;
    private final CircleEmbracingSquaresConfig circleEmbracingSquaresConfig;
    private final float sectorTopEdgeAngleInDegree;
    private final float sectorSweepAngleInDegree;

    public SectorClipAreaDescriptor(CoordinatesHolder first,
                                    CoordinatesHolder second,
                                    CoordinatesHolder third,
                                    CoordinatesHolder fourth,
                                    CircleEmbracingSquaresConfig circleEmbracingSquaresConfig,
                                    float sectorTopEdgeAngleInDegree,
                                    float sectorSweepAngleInDegree) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.circleEmbracingSquaresConfig = circleEmbracingSquaresConfig;
        this.sectorTopEdgeAngleInDegree = sectorTopEdgeAngleInDegree;
        this.sectorSweepAngleInDegree = sectorSweepAngleInDegree;
    }

    public CoordinatesHolder getFirst() {
        return first;
    }

    public CoordinatesHolder getSecond() {
        return second;
    }

    public CoordinatesHolder getThird() {
        return third;
    }

    public CoordinatesHolder getFourth() {
        return fourth;
    }

    public CircleEmbracingSquaresConfig getCircleEmbracingSquaresConfig() {
        return circleEmbracingSquaresConfig;
    }

    public float getSectorTopEdgeAngleInDegree() {
        return sectorTopEdgeAngleInDegree;
    }

    public float getSectorSweepAngleInDegree() {
        return sectorSweepAngleInDegree;
    }

    @Override
    public String toString() {
        return "SectorClipAreaDescriptor{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                ", fourth=" + fourth +
                ", circleEmbracingSquaresConfig=" + circleEmbracingSquaresConfig +
                ", sectorTopEdgeAngleInDegree=" + sectorTopEdgeAngleInDegree +
                ", sectorSweepAngleInDegree=" + sectorSweepAngleInDegree +
                '}';
    }
}
