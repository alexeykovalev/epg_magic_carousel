package com.magicwheel.entity;

import android.graphics.RectF;

import com.magicwheel.widget.WheelSectorWrapperView;

/**
 * Stores data required for proper cutting circle's sector like shape from
 * usual rectangle (standard view shape).
 *
 * @author Alexey Kovalev
 * @since 05.11.2016
 */
public final class SectorClipAreaDescriptor {

    public static final class WheelEmbracingSquaresConfig {

        /**
         * Square embracing circle with {@code outerRadius} defined in
         * {@link WheelSectorWrapperView}
         * coordinates system.
         */
        private RectF outerCircleEmbracingSquareInSectorWrapperCoordsSystem;

        /**
         * Square embracing circle with {@code innerRadius} defined in
         * {@link WheelSectorWrapperView}
         * coordinates system.
         */
        private RectF innerCircleEmbracingSquareInSectorWrapperCoordsSystem;

        public WheelEmbracingSquaresConfig(RectF outerCircleEmbracingSquareInSectorWrapperCoordsSystem,
                                           RectF innerCircleEmbracingSquareInSectorWrapperCoordsSystem) {
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
            return "WheelEmbracingSquaresConfig{" +
                    "outerCircleEmbracingSquareInSectorWrapperCoordsSystem=" + outerCircleEmbracingSquareInSectorWrapperCoordsSystem.toShortString() +
                    ", innerCircleEmbracingSquareInSectorWrapperCoordsSystem=" + innerCircleEmbracingSquareInSectorWrapperCoordsSystem.toShortString() +
                    '}';
        }
    }

    // --- Defines sector TRAPEZE coordinates in WheelSectorWrapperView coordinates system ---

    // first
    private final CoordinatesHolder bottomLeftCorner;
    // second
    private final CoordinatesHolder bottomRightCorner;
    // third
    private final CoordinatesHolder topLeftCorner;
    // fourth
    private final CoordinatesHolder topRightCorner;

    private final WheelEmbracingSquaresConfig wheelEmbracingSquaresConfig;
    private final float sectorTopEdgeAngleInDegree;
    private final float sectorSweepAngleInDegree;

    public SectorClipAreaDescriptor(CoordinatesHolder bottomLeftCorner,
                                    CoordinatesHolder bottomRightCorner,
                                    CoordinatesHolder topLeftCorner,
                                    CoordinatesHolder topRightCorner,
                                    WheelEmbracingSquaresConfig wheelEmbracingSquaresConfig,
                                    float sectorTopEdgeAngleInDegree,
                                    float sectorSweepAngleInDegree) {
        this.bottomLeftCorner = bottomLeftCorner;
        this.bottomRightCorner = bottomRightCorner;
        this.topLeftCorner = topLeftCorner;
        this.topRightCorner = topRightCorner;
        this.wheelEmbracingSquaresConfig = wheelEmbracingSquaresConfig;
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

    public WheelEmbracingSquaresConfig getWheelEmbracingSquaresConfig() {
        return wheelEmbracingSquaresConfig;
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
                ", wheelEmbracingSquaresConfig=" + wheelEmbracingSquaresConfig +
                ", sectorTopEdgeAngleInDegree=" + sectorTopEdgeAngleInDegree +
                ", sectorSweepAngleInDegree=" + sectorSweepAngleInDegree +
                '}';
    }
}
