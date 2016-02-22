package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.WindowManager;

import com.sss.magicwheel.entity.MeasurementsHolder;
import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.SectorClipAreaDescriptor;

import static java.lang.Math.*;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
public final class WheelComputationHelper {

    public static final int VISIBLE_SECTORS_AMOUNT_AT_TOP = 4;
    public static final int VISIBLE_SECTORS_AMOUNT_AT_BOTTOM = 4;
    public static final int HIDDEN_SECTORS_AMOUNT_IN_GAP_AREA = 3;
    public static final int TOTAL_SECTORS_AMOUNT =
            VISIBLE_SECTORS_AMOUNT_AT_TOP + VISIBLE_SECTORS_AMOUNT_AT_BOTTOM + HIDDEN_SECTORS_AMOUNT_IN_GAP_AREA;

    public static final double TOP_EDGE_ANGLE_RESTRICTION_IN_RAD = PI / 2;
    public static final double BOTTOM_EDGE_ANGLE_RESTRICTION_IN_RAD = -PI / 2;

    private static final double DEGREE_TO_RAD_COEF = PI / 180;
    private static final double RAD_TO_DEGREE_COEF = 1 / DEGREE_TO_RAD_COEF;

    private static WheelComputationHelper instance;

    private final WheelConfig wheelConfig;
    private final MeasurementsHolder sectorWrapperViewMeasurements;
    private final MeasurementsHolder bigWrapperViewMeasurements;
    private final SectorClipAreaDescriptor sectorClipArea;


    public static WheelComputationHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Has not been initialized yet. Invoke initialize() beforehand.");
        }
        return instance;
    }

    public static void initialize(WheelConfig wheelConfig) {
        instance = new WheelComputationHelper(wheelConfig);
    }

    public static MeasurementsHolder getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        wm.getDefaultDisplay().getSize(screenSize);
        return new MeasurementsHolder(screenSize.x, screenSize.y);
    }

    public static double degreeToRadian(double angleInDegree) {
        return angleInDegree * DEGREE_TO_RAD_COEF;
    }

    public static double radToDegree(double angleInRad) {
        return angleInRad * RAD_TO_DEGREE_COEF;
    }

    public static boolean isEqualWithEpsilon(double first, double second, double epsilon) {
        return Math.abs(first / second - 1) < epsilon;
    }

    // TODO: 16.12.2015 to many objects allocation - reduce this amount in future
    public static RectF fromCircleCoordsSystemToRecyclerViewCoordsSystem(RectF coordinatesToTransform) {

        final PointF leftTopCorner = fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                new PointF(coordinatesToTransform.left, coordinatesToTransform.top)
        );

        final PointF rightBottomCorner = fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                new PointF(coordinatesToTransform.right, coordinatesToTransform.bottom)
        );

        return new RectF(leftTopCorner.x, leftTopCorner.y, rightBottomCorner.x, rightBottomCorner.y);
    }

    public static PointF fromCircleCoordsSystemToRecyclerViewCoordsSystem(PointF pointToTransform) {
        final PointF circleCenterRelToRecyclerView = getInstance().getWheelConfig().getCircleCenterRelToRecyclerView();

        return new PointF(
                circleCenterRelToRecyclerView.x + pointToTransform.x,
                circleCenterRelToRecyclerView.y - pointToTransform.y
        );
    }

    private WheelComputationHelper(WheelConfig wheelConfig) {
        this.wheelConfig = wheelConfig;
        this.sectorWrapperViewMeasurements = new MeasurementsHolder(
                computeSectorWrapperViewWidth(),
                computeSectorWrapperViewHeight()
        );
        this.bigWrapperViewMeasurements = createBigWrapperViewMeasurements();
        // don't change order of this line - has to be last
        this.sectorClipArea = createSectorClipArea();
    }


    /**
     * Width of the view which wraps the sector.
     */
    private int computeSectorWrapperViewWidth() {
        final double delta = wheelConfig.getInnerRadius() * cos(wheelConfig.getAngularRestrictions().getSectorHalfAngleInRad());
        return (int) (wheelConfig.getOuterRadius() - delta);
    }

    /**
     * Height of the view which wraps the sector.
     */
    private int computeSectorWrapperViewHeight() {
        final double halfHeight = wheelConfig.getOuterRadius() * sin(wheelConfig.getAngularRestrictions().getSectorHalfAngleInRad());
        return (int) (2 * halfHeight);
    }

    private MeasurementsHolder createBigWrapperViewMeasurements() {
        final int viewWidth = wheelConfig.getOuterRadius();
        // big wrapper view has the same height as the sector wrapper view
        final int viewHeight = sectorWrapperViewMeasurements.getHeight();
        return new MeasurementsHolder(viewWidth, viewHeight);
    }

    private SectorClipAreaDescriptor createSectorClipArea() {
        final int sectorWrapperViewWidth = sectorWrapperViewMeasurements.getWidth();
        final int sectorWrapperViewHalfHeight = sectorWrapperViewMeasurements.getHeight() / 2;

        final double sectorHalfAngleInRad = wheelConfig.getAngularRestrictions().getSectorHalfAngleInRad();
        final double leftBaseDelta = wheelConfig.getInnerRadius() * sin(sectorHalfAngleInRad);
        final double rightBaseDelta = wheelConfig.getOuterRadius() * sin(sectorHalfAngleInRad);

        final CoordinatesHolder bottomLeftCorner = CoordinatesHolder.ofRect(0, sectorWrapperViewHalfHeight + leftBaseDelta);
        final CoordinatesHolder topLeftCorner = CoordinatesHolder.ofRect(0, sectorWrapperViewHalfHeight - leftBaseDelta);

        final CoordinatesHolder bottomRight = CoordinatesHolder.ofRect(sectorWrapperViewWidth, sectorWrapperViewHalfHeight + rightBaseDelta);
        final CoordinatesHolder topRightCorner = CoordinatesHolder.ofRect(sectorWrapperViewWidth, sectorWrapperViewHalfHeight - rightBaseDelta);

        final SectorClipAreaDescriptor.CircleEmbracingSquaresConfig embracingSquaresConfig =
                new SectorClipAreaDescriptor.CircleEmbracingSquaresConfig(
                        getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem(),
                        getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem()
                );

        final float sectorTopEdgeAngleInDegree = (float) radToDegree(sectorHalfAngleInRad);
        final float sectorSweepAngleInDegree = (float) radToDegree(wheelConfig.getAngularRestrictions().getSectorAngleInRad());
        return new SectorClipAreaDescriptor(
                bottomLeftCorner, bottomRight, topLeftCorner, topRightCorner, embracingSquaresConfig,
                sectorTopEdgeAngleInDegree, sectorSweepAngleInDegree
        );
    }

    public WheelConfig getWheelConfig() {
        return wheelConfig;
    }

    public MeasurementsHolder getSectorWrapperViewMeasurements() {
        return sectorWrapperViewMeasurements;
    }

    public MeasurementsHolder getBigWrapperViewMeasurements() {
        return bigWrapperViewMeasurements;
    }

    public SectorClipAreaDescriptor getSectorClipArea() {
        return sectorClipArea;
    }

    /**
     * Layout will be performed from top to bottom direction. And we should have sector
     * positioned parallel to central diameter. So taking into account imposed angular restrictions
     * we should compute actual layout start angle.
     * <p/>
     * So the firstly layouted sector's top edge will be aligned by this angle.
     */
    public double getWheelLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
    }

    public double getSectorAngleBottomEdgeInRad(double sectorAnglePosition) {
        return sectorAnglePosition - wheelConfig.getAngularRestrictions().getSectorHalfAngleInRad();
    }

    public double getSectorAngleTopEdgeInRad(double sectorAnglePosition) {
        return sectorAnglePosition + wheelConfig.getAngularRestrictions().getSectorHalfAngleInRad();
    }

    public double getSectorAlignmentAngleInRadBySectorTopEdge(double sectorTopEdgeAngleInRad) {
        return sectorTopEdgeAngleInRad - wheelConfig.getAngularRestrictions().getSectorHalfAngleInRad();
    }

    /**
     * @param wrapperViewWidth - depends on inner and outer radius values
     */
    public RectF getBigWrapperViewCoordsInCircleSystem(int wrapperViewWidth) {
        final int topEdge = sectorWrapperViewMeasurements.getHeight() / 2;
        return new RectF(0, topEdge, wrapperViewWidth, -topEdge);
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
        final float x = wheelConfig.getOuterRadius() - sectorWrapperViewMeasurements.getWidth();
        final float y = sectorWrapperViewMeasurements.getHeight() / 2f;
        return new PointF(x, y);
    }


    private RectF getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem() {
        RectF embracingSquare = getOuterCircleEmbracingSquareInCircleCoordsSystem();
        PointF leftCorner = getSectorWrapperViewLeftCornerInCircleCoordsSystem();
        return new RectF(
                embracingSquare.left - leftCorner.x,
                leftCorner.y - embracingSquare.top,
                embracingSquare.right - leftCorner.x,
                leftCorner.y - embracingSquare.bottom
        );
    }

    private RectF getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem() {
        RectF embracingSquare = getInnerCircleEmbracingSquareInCircleCoordsSystem();
        PointF leftCorner = getSectorWrapperViewLeftCornerInCircleCoordsSystem();
        return new RectF(
                embracingSquare.left - leftCorner.x,
                leftCorner.y - embracingSquare.top,
                embracingSquare.right - leftCorner.x,
                leftCorner.y - embracingSquare.bottom
        );
    }

    /**
     * Transforms swipe gesture's travelled distance {@code scrollDelta} into relevant
     * wheel rotation angle.
     */
    public double fromTraveledDistanceToWheelRotationAngle(int scrollDelta) {
        return (double) scrollDelta / wheelConfig.getOuterRadius();
    }

    public double fromWheelRotationAngleToTraveledDistance(double rotationAngleInRad) {
        return rotationAngleInRad * wheelConfig.getOuterRadius();
    }
}
