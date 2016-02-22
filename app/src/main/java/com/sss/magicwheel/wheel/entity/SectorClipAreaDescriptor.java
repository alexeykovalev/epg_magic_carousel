package com.sss.magicwheel.wheel.entity;

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

    // first
    private final CoordinatesHolder bottomLeftCorner;
    // second
    private final CoordinatesHolder bottomRightCorner;
    // third
    private final CoordinatesHolder topLeftCorner;
    // fourth
    private final CoordinatesHolder topRightCorner;

    private final CircleEmbracingSquaresConfig circleEmbracingSquaresConfig;
    private final float sectorTopEdgeAngleInDegree;
    private final float sectorSweepAngleInDegree;

    public SectorClipAreaDescriptor(CoordinatesHolder bottomLeftCorner,
                                    CoordinatesHolder bottomRightCorner,
                                    CoordinatesHolder topLeftCorner,
                                    CoordinatesHolder topRightCorner,
                                    CircleEmbracingSquaresConfig circleEmbracingSquaresConfig,
                                    float sectorTopEdgeAngleInDegree,
                                    float sectorSweepAngleInDegree) {
        this.bottomLeftCorner = bottomLeftCorner;
        this.bottomRightCorner = bottomRightCorner;
        this.topLeftCorner = topLeftCorner;
        this.topRightCorner = topRightCorner;
        this.circleEmbracingSquaresConfig = circleEmbracingSquaresConfig;
        this.sectorTopEdgeAngleInDegree = sectorTopEdgeAngleInDegree;
        this.sectorSweepAngleInDegree = sectorSweepAngleInDegree;
    }

    public CoordinatesHolder getBottomLeftCorner() {
        return bottomLeftCorner;
    }

    public CoordinatesHolder getBottomRightCorner() {
        return bottomRightCorner;
    }

    public CoordinatesHolder getTopLeftCorner() {
        return topLeftCorner;
    }

    public CoordinatesHolder getTopRightCorner() {
        return topRightCorner;
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
                "bottomLeftCorner=" + bottomLeftCorner +
                ", bottomRightCorner=" + bottomRightCorner +
                ", topLeftCorner=" + topLeftCorner +
                ", topRightCorner=" + topRightCorner +
                ", circleEmbracingSquaresConfig=" + circleEmbracingSquaresConfig +
                ", sectorTopEdgeAngleInDegree=" + sectorTopEdgeAngleInDegree +
                ", sectorSweepAngleInDegree=" + sectorSweepAngleInDegree +
                '}';
    }
}
