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

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
// TODO: 14.12.2015 use lazy initializer here
public final class WheelComputationHelper {

    private static final double DEGREE_TO_RAD_COEF = Math.PI / 180;
    private static final double RAD_TO_DEGREE_COEF = 1 / DEGREE_TO_RAD_COEF;
    private static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

    private static MeasurementsHolder screenDimensions;

    private static WheelComputationHelper instance;

    private final WheelConfig wheelConfig;
    private final MeasurementsHolder sectorWrapperViewMeasurements;
    private final MeasurementsHolder bigWrapperViewMeasurements;
    private final SectorClipAreaDescriptor sectorClipArea;

    // ------------
    // Be careful LAZY initialized fields. Don't access directly, use getters instead.
    // ------------

    private RectF outerCircleEmbracingSquareInSectorWrapperCoordsSystem;
    private RectF innerCircleEmbracingSquareInSectorWrapperCoordsSystem;

    // ------------

    public static WheelComputationHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Has not been initialized yet. Invoke initialize() beforehand.");
        }
        return instance;
    }

    public static void initialize(WheelConfig wheelConfig) {
        if (isInitialized()) {
            throw new IllegalStateException("WheelComputationHelper has been already initialized. Don't invoke this method twice.");
        }
        instance = new WheelComputationHelper(wheelConfig);
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static MeasurementsHolder getScreenDimensions(Context context) {
        if (screenDimensions == null) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Point screenSize = new Point();
            wm.getDefaultDisplay().getSize(screenSize);
            screenDimensions = new MeasurementsHolder(screenSize.x, screenSize.y);
        }
        return screenDimensions;
    }

    public static double degreeToRadian(double angleInDegree) {
        return angleInDegree * DEGREE_TO_RAD_COEF;
    }

    public static double radToDegree(double angleInRad) {
        return angleInRad * RAD_TO_DEGREE_COEF;
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
        final double delta = wheelConfig.getInnerRadius() * Math.cos(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
        return (int) (wheelConfig.getOuterRadius() - delta);
    }

    /**
     * Height of the view which wraps the sector.
     */
    private int computeSectorWrapperViewHeight() {
        final double halfHeight = wheelConfig.getOuterRadius() * Math.sin(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
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

        final double leftBaseDelta = wheelConfig.getInnerRadius() * Math.sin(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
        final double rightBaseDelta = wheelConfig.getOuterRadius() * Math.sin(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);

        final CoordinatesHolder bottomLeftCorner = CoordinatesHolder.ofRect(0, sectorWrapperViewHalfHeight + leftBaseDelta);
        final CoordinatesHolder topLeftCorner = CoordinatesHolder.ofRect(0, sectorWrapperViewHalfHeight - leftBaseDelta);

        final CoordinatesHolder bottomRight = CoordinatesHolder.ofRect(sectorWrapperViewWidth, sectorWrapperViewHalfHeight + rightBaseDelta);
        final CoordinatesHolder topRightCorner = CoordinatesHolder.ofRect(sectorWrapperViewWidth, sectorWrapperViewHalfHeight - rightBaseDelta);

        final SectorClipAreaDescriptor.CircleEmbracingSquaresConfig embracingSquaresConfig =
                new SectorClipAreaDescriptor.CircleEmbracingSquaresConfig(
                        getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem(),
                        getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem()
                );

        final float sectorTopEdgeAngleInDegree = (float) radToDegree(wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
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
     * <p>
     * So the firstly layouted sector's top edge will be aligned by this angle.
     */
    public double getWheelLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
    }

    public double getSectorAngleBottomEdgeInRad(double sectorAnglePosition) {
        return sectorAnglePosition - wheelConfig.getAngularRestrictions().getSectorAngleInRad();
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

    private RectF getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem() {
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

}
