package com.sss.magicwheel.manager;

import android.graphics.PointF;
import android.graphics.RectF;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.SectorClipAreaDescriptor;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
// TODO: 14.12.2015 use lazy initializer here
// TODO: 28.01.2016 replace all Rect and Point class usages to respective RectF and PointF ones
public final class WheelComputationHelper {

    private static final double DEGREE_TO_RAD_COEF = Math.PI / 180;
    private static final double RAD_TO_DEGREE_COEF = 1 / DEGREE_TO_RAD_COEF;
    private static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

    private static WheelComputationHelper instance;

    private final WheelConfig wheelConfig;

    private int sectorWrapperViewWidth = NOT_DEFINED_VALUE;
    private int sectorWrapperViewHeight = NOT_DEFINED_VALUE;

    private RectF outerCircleEmbracingSquareInSectorWrapperCoordsSystem;
    private RectF innerCircleEmbracingSquareInSectorWrapperCoordsSystem;

    private SectorClipAreaDescriptor sectorClipData;

    private double layoutStartAngle;

    public static WheelComputationHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Has not been initialized yet. Invoke initialize() beforehand.");
        }
        return instance;
    }

    public static void initialize(WheelConfig wheelConfig) {
        instance = new WheelComputationHelper(wheelConfig);
    }

    private WheelComputationHelper(WheelConfig wheelConfig) {
        this.wheelConfig = wheelConfig;
    }

    public static double degreeToRadian(double angleInDegree) {
        return angleInDegree * DEGREE_TO_RAD_COEF;
    }

    public static double radToDegree(double angleInRad) {
        return angleInRad * RAD_TO_DEGREE_COEF;
    }

    public WheelConfig getWheelConfig() {
        return wheelConfig;
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
        final double delta = wheelConfig.getInnerRadius() * Math.cos(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
        return (int) (wheelConfig.getOuterRadius() - delta);
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
        final double halfHeight = wheelConfig.getOuterRadius() * Math.sin(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
        return (int) (2 * halfHeight);
    }

    /**
     * @param wrapperViewWidth - depends on inner and outer radius values
     */
    public RectF getWrapperViewCoordsInCircleSystem(int wrapperViewWidth) {
        final int topEdge = getSectorWrapperViewHeight() / 2;
        return new RectF(0, topEdge, wrapperViewWidth, -topEdge);
    }

    public RectF getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem() {
        if (outerCircleEmbracingSquareInSectorWrapperCoordsSystem == null) {
            RectF embracingSquare = getOuterCircleEmbracingSquareInCircleCoordsSystem();
            PointF leftCorner = getSectorWrapperViewLeftCornerInCircleCoordsSystem();
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
            RectF embracingSquare = getInnerCircleEmbracingSquareInCircleCoordsSystem();
            PointF leftCorner = getSectorWrapperViewLeftCornerInCircleCoordsSystem();
            innerCircleEmbracingSquareInSectorWrapperCoordsSystem = new RectF(
                    embracingSquare.left - leftCorner.x,
                    leftCorner.y - embracingSquare.top,
                    embracingSquare.right - leftCorner.x,
                    leftCorner.y - embracingSquare.bottom
            );
        }
        return innerCircleEmbracingSquareInSectorWrapperCoordsSystem;
    }

    public RectF getOuterCircleEmbracingSquareInCircleCoordsSystem() {
        final int outerRadius = wheelConfig.getOuterRadius();
        return new RectF(-outerRadius, outerRadius, outerRadius, -outerRadius);
    }

    public RectF getInnerCircleEmbracingSquareInCircleCoordsSystem() {
        final int innerRadius = wheelConfig.getInnerRadius();
        return new RectF(-innerRadius, innerRadius, innerRadius, -innerRadius);
    }

    private PointF getSectorWrapperViewLeftCornerInCircleCoordsSystem() {
        final float x = wheelConfig.getOuterRadius() - getSectorWrapperViewWidth();
        final float y = getSectorWrapperViewHeight() / 2f;
        return new PointF(x, y);
    }

    public SectorClipAreaDescriptor createSectorClipArea() {
        if (sectorClipData == null) {
            final int viewWidth = getSectorWrapperViewWidth();
            final int viewHalfHeight = getSectorWrapperViewHeight() / 2;

            final double leftBaseDelta = wheelConfig.getInnerRadius() * Math.sin(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
            final double rightBaseDelta = wheelConfig.getOuterRadius() * Math.sin(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);

            final CoordinatesHolder bottomLeftCorner = CoordinatesHolder.ofRect(0, viewHalfHeight + leftBaseDelta);
            final CoordinatesHolder topLeftCorner = CoordinatesHolder.ofRect(0, viewHalfHeight - leftBaseDelta);

            final CoordinatesHolder bottomRight = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight + rightBaseDelta);
            final CoordinatesHolder topRightCorner = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight - rightBaseDelta);

            final SectorClipAreaDescriptor.CircleEmbracingSquaresConfig embracingSquaresConfig =
                    new SectorClipAreaDescriptor.CircleEmbracingSquaresConfig(
                            getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem(),
                            getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem()
                    );

            final float sectorTopEdgeAngleInDegree = (float) radToDegree(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
            final float sectorSweepAngleInDegree = (float) radToDegree(wheelConfig.getAngularRestrictions().getSectorAngleInRad());
            sectorClipData = new SectorClipAreaDescriptor(
                    bottomLeftCorner, bottomRight, topLeftCorner, topRightCorner, embracingSquaresConfig,
                    sectorTopEdgeAngleInDegree, sectorSweepAngleInDegree
            );
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
    public double getLayoutStartAngleInRad() {
        if (layoutStartAngle == 0) {
            double res = 0;
            final double topEdgeAngularRestrictionInRad = wheelConfig.getAngularRestrictions().getTopEdgeAngleRestrictionInRad();
            while (res < topEdgeAngularRestrictionInRad) {
                res += wheelConfig.getAngularRestrictions().getSectorAngleInRad();
            }
            layoutStartAngle = res;
        }
        return layoutStartAngle;
    }

    public double getSectorAngleBottomEdgeInRad(double sectorAnglePosition) {
        return sectorAnglePosition - wheelConfig.getAngularRestrictions().getSectorAngleInRad();
    }

    // TODO: 16.12.2015 to many objects allocation - reduce this amount in future
    // TODO: 29.01.2016 it's seems that first parameter might be encapsulated inside the method
    public static RectF fromCircleCoordsSystemToRecyclerViewCoordsSystem(PointF circleCenterRelToRecyclerView,
                                                                        RectF coordinatesToTransform) {

        final PointF leftTopCorner = fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleCenterRelToRecyclerView,
                new PointF(coordinatesToTransform.left, coordinatesToTransform.top)
        );

        final PointF rightBottomCorner = fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                circleCenterRelToRecyclerView,
                new PointF(coordinatesToTransform.right, coordinatesToTransform.bottom)
        );

        return new RectF(leftTopCorner.x, leftTopCorner.y, rightBottomCorner.x, rightBottomCorner.y);
    }

    public static PointF fromCircleCoordsSystemToRecyclerViewCoordsSystem(PointF circleCenterRelToRecyclerView,
                                                                          PointF pointToTransform) {
        return new PointF(
                circleCenterRelToRecyclerView.x + pointToTransform.x,
                circleCenterRelToRecyclerView.y - pointToTransform.y
        );
    }

}
