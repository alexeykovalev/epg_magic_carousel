package com.sss.magicwheel.manager.second;

import android.graphics.Point;
import android.graphics.Rect;

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.LinearClipData;
import com.sss.magicwheel.manager.CircleConfig;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
// TODO: 14.12.2015 use lazy initializer here
final class WheelComputationHelper {

    private static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

    private final CircleConfig circleConfig;

    private int sectorWrapperViewWidth = NOT_DEFINED_VALUE;
    private int sectorWrapperViewHeight = NOT_DEFINED_VALUE;

    private LinearClipData sectorClipData;

    private double layoutStartAngle;

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

    public LinearClipData createSectorClipArea() {
        if (sectorClipData == null) {
            final int viewWidth = getSectorWrapperViewWidth();
            final int viewHalfHeight = getSectorWrapperViewHeight() / 2;

            final double leftBaseDelta = circleConfig.getInnerRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);
            final double rightBaseDelta = circleConfig.getOuterRadius() * Math.sin(circleConfig.getAngularRestrictions().getSectorAngleInRad() / 2);

            final CoordinatesHolder first = CoordinatesHolder.ofRect(0, viewHalfHeight + leftBaseDelta);
            final CoordinatesHolder third = CoordinatesHolder.ofRect(0, viewHalfHeight - leftBaseDelta);

            final CoordinatesHolder second = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight + rightBaseDelta);
            final CoordinatesHolder forth = CoordinatesHolder.ofRect(viewWidth, viewHalfHeight - rightBaseDelta);

            sectorClipData = new LinearClipData(first, second, third, forth);
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
            return res;
        }
        return layoutStartAngle;
    }

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
