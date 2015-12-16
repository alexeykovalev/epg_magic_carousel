package com.sss.magicwheel.manager;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sss.magicwheel.entity.CircleConfig;
import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.SectorClipAreaDescriptor;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
// TODO: 14.12.2015 use lazy initializer here
public final class WheelComputationHelper {

    private static final double DEGREE_TO_RAD_COEF = Math.PI / 180;
    private static final double RAD_TO_DEGREE_COEF = 1 / DEGREE_TO_RAD_COEF;
    private static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

    private final CircleConfig circleConfig;

    private int sectorWrapperViewWidth = NOT_DEFINED_VALUE;
    private int sectorWrapperViewHeight = NOT_DEFINED_VALUE;

    private RectF outerCircleEmbracingSquareInSectorWrapperCoordsSystem;
    private RectF innerCircleEmbracingSquareInSectorWrapperCoordsSystem;

    private SectorClipAreaDescriptor sectorClipData;

    private double layoutStartAngle;

    public static double degreeToRadian(double angleInDegree) {
        return angleInDegree * DEGREE_TO_RAD_COEF;
    }

    public static double radToDegree(double angleInRad) {
        return angleInRad * RAD_TO_DEGREE_COEF;
    }

    public WheelComputationHelper(CircleConfig circleConfig) {
        this.circleConfig = circleConfig;
    }

    /**
     * Width of the view which wraps the sector.
     */
    public int getSectorWrapperViewWidth() {
        if (sectorWrapperViewWidth == NOT_DEFINED_VALUE) {
            sectorWrapperViewWidth = computeViewWidth();
        }
        return sectorWrapperViewWidth;
    }

    private int computeViewWidth() {
        final double delta = circleConfig.getInnerRadius() * Math.cos(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
        return (int) (circleConfig.getOuterRadius() - delta);
    }

    /**
     * Height of the view which wraps the sector.
     */
    public int getSectorWrapperViewHeight() {
        if (sectorWrapperViewHeight == NOT_DEFINED_VALUE) {
            sectorWrapperViewHeight = computeViewHeight();
        }
        return sectorWrapperViewHeight;
    }

    private int computeViewHeight() {
        final double halfHeight = circleConfig.getOuterRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
        return (int) (2 * halfHeight);
    }

    /**
     * @param wrapperViewWidth - depends on inner and outer radius values
     */
    public Rect getWrapperViewCoordsInCircleSystem(int wrapperViewWidth) {
        final int topEdge = getSectorWrapperViewHeight() / 2;
        return new Rect(0, topEdge, wrapperViewWidth, -topEdge);
    }

    public RectF getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem() {
        if (outerCircleEmbracingSquareInSectorWrapperCoordsSystem == null) {
            Rect embracingSquare = getOuterCircleEmbracingSquareInCircleCoordsSystem();
            Point leftCorner = getSectorWrapperViewLeftCornerInCircleSystem();
            outerCircleEmbracingSquareInSectorWrapperCoordsSystem = new RectF(
                    embracingSquare.left - leftCorner.x,
                    leftCorner.y - embracingSquare.top,
                    embracingSquare.right - leftCorner.x,
                    leftCorner.y - embracingSquare.bottom
            );
        }
        return outerCircleEmbracingSquareInSectorWrapperCoordsSystem;
    }

    public RectF getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem() {
        if (innerCircleEmbracingSquareInSectorWrapperCoordsSystem == null) {
            Rect embracingSquare = getInnerCircleEmbracingSquareInCircleCoordsSystem();
            Point leftCorner = getSectorWrapperViewLeftCornerInCircleSystem();
            innerCircleEmbracingSquareInSectorWrapperCoordsSystem = new RectF(
                    embracingSquare.left - leftCorner.x,
                    leftCorner.y - embracingSquare.top,
                    embracingSquare.right - leftCorner.x,
                    leftCorner.y - embracingSquare.bottom
            );
        }
        return innerCircleEmbracingSquareInSectorWrapperCoordsSystem;
    }

    private Rect getOuterCircleEmbracingSquareInCircleCoordsSystem() {
        final int outerRadius = circleConfig.getOuterRadius();
        return new Rect(-outerRadius, outerRadius, outerRadius, -outerRadius);
    }

    private Rect getInnerCircleEmbracingSquareInCircleCoordsSystem() {
        final int innerRadius = circleConfig.getInnerRadius();
        return new Rect(-innerRadius, innerRadius, innerRadius, -innerRadius);
    }

    private Point getSectorWrapperViewLeftCornerInCircleSystem() {
        final int x = circleConfig.getOuterRadius() - getSectorWrapperViewWidth();
        final int y = getSectorWrapperViewHeight() / 2;
        return new Point(x, y);
    }

    public SectorClipAreaDescriptor createSectorClipArea() {
        if (sectorClipData == null) {
            final int viewWidth = getSectorWrapperViewWidth();
            final int viewHalfHeight = getSectorWrapperViewHeight() / 2;

            final double leftBaseDelta = circleConfig.getInnerRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
            final double rightBaseDelta = circleConfig.getOuterRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);

            final CoordinatesHolder first = CoordinatesHolder.ofRect(0, viewHalfHeight + leftBaseDelta);
            final CoordinatesHolder third = CoordinatesHolder.ofRect(0, viewHalfHeight - leftBaseDelta);

            final CoordinatesHolder second = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight + rightBaseDelta);
            final CoordinatesHolder forth = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight - rightBaseDelta);

            sectorClipData = new SectorClipAreaDescriptor(first, second, third, forth);
        }

        return sectorClipData;
    }


    /**
     * Layout will be performed from top to bottom direction. And we should have sector
     * positioned parallel to central diameter. So taking into account imposed angular restrictions
     * we should compute actual layout start angle.
     * <p/>
     * So the firstly layouted sector's top edge will be aligned by this angle.
     */
    public double getLayoutStartAngle() {
        if (layoutStartAngle == 0) {
            double res = 0;
            final double topEdgeAngularRestrictionInRad = circleConfig.getAngularRestrictions().getTopEdgeAngleRestrictionInRad();
            while (res < topEdgeAngularRestrictionInRad) {
                res += circleConfig.getAngularRestrictions().getSectorAngleInRad();
            }
            layoutStartAngle = res;
        }
        return layoutStartAngle;
    }

    public double getSectorAngleBottomEdge(double sectorAnglePosition) {
        return sectorAnglePosition - circleConfig.getAngularRestrictions().getSectorAngleInRad();
    }

    // TODO: 16.12.2015 to many objects allocation - reduce this amount in future
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
