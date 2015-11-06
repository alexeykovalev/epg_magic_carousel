package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.LinearClipData;
import com.sss.magicwheel.motion.TouchHandler;
import com.sss.magicwheel.motion.IScrollable;
import com.sss.magicwheel.motion.ITouchHandler;
import com.sss.magicwheel.util.MagicCalculationHelper;

import java.util.Random;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class MagicWheelView extends ViewGroup implements IScrollable {

    private static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

    // todo: has to be calculated based on sector dimensions. Don't hardcode this values.
    private static final int STUB_VIEW_WIDTH = 400;
    private static final int STUB_VIEW_HEIGHT = 250;

    private static final int[] AVAILABLE_VIEW_COLORS = new int[] {
            Color.BLUE, Color.RED, Color.YELLOW, Color.CYAN
    };

    private final ITouchHandler touchHandler;
    private final MagicCalculationHelper calculationHelper;
    private final Random randomizer;

    private final double maxAngleInRad;
    private final double minAngleInRad;
    private double layoutStartAngleInRad;
    private double currentAngleInRad;

    private boolean isInLayoutStage;

    private int middleRadius = NOT_DEFINED_VALUE;


    public MagicWheelView(Context context) {
        this(context, null);
    }

    public MagicWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        randomizer = new Random();
        calculationHelper = MagicCalculationHelper.getInstance();
        touchHandler = new TouchHandler(context, this);

        maxAngleInRad = calculationHelper.getStartAngle();
        // todo: simply for now due do circle is symmetric
        minAngleInRad = -calculationHelper.getStartAngle();
        layoutStartAngleInRad = maxAngleInRad;
        currentAngleInRad = layoutStartAngleInRad;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return touchHandler.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler.onTouchEvent(event);
    }

    @Override
    public int scrollHorizontallyBy(int dx) {
//        Log.e("TAG", "scrollHorizontallyBy() dx [" + dx + "]");
        return 0;
    }

    @Override
    public int scrollVerticallyBy(int dy) {
        Log.e("TAG", "scrollVerticallyBy() dy [" + dy + "]");

        double coef = (double)dy / calculationHelper.getOuterRadius();
        currentAngleInRad += coef;

        Log.e("TAG", "scrollVerticallyBy() [" + dy + "], " +
                "currentAngleInRad [" + MagicCalculationHelper.fromRadToDegree(currentAngleInRad) + "], " +
                "coef [" + coef + "]");

        updateAngles();

        Log.e("TAG", "currentAngleInRad [" + MagicCalculationHelper.fromRadToDegree(currentAngleInRad) + "]");

        requestLayout();

        return 0;
    }

    private void updateAngles() {
        double calculatedAngle = currentAngleInRad;
        while (calculatedAngle <= maxAngleInRad) {
            calculatedAngle += MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD;
        }

        layoutStartAngleInRad = calculatedAngle;

        if (currentAngleInRad < minAngleInRad) {
            currentAngleInRad = layoutStartAngleInRad;
        }
    }

    @Override
    public View getContentView() {
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isInLayoutStage) {
            return;
        }
        isInLayoutStage = true;
        removeAllViewsInLayout();

        double angleInRad = layoutStartAngleInRad;
        while (angleInRad > minAngleInRad) {
            // todo: no direct view cast. Use interface instead of.
            ItemView child = (ItemView) createAndMeasureNewView();

//            LinearClipData childClipData = getClipDataForChild(
//                    angleInRad - MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD, angleInRad /*+ MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD*/);
//            child.setLinearClipData(childClipData);

//            LinearClipData childClipArea = getClipArea(child, angleInRad, angleInRad + MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
//            child.setLinearClipData(childClipArea);

            CoordinatesHolder childPositionOnScreen = getChildPositionOnScreenByLayoutAngle(child, angleInRad);
            setupChild(child, childPositionOnScreen);
            rotateChild(angleInRad, child);
            angleInRad -= MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD;
        }

        isInLayoutStage = false;
    }


    /**
     * @param currentAngleInRad - current layout angle. Also on this angle child will be rotated
     *                          so we should rotate the clip area on this angle, but in revers direction
     * @param previousAngleInRad - previous layout angle
     */
    private LinearClipData getClipArea(View child, double currentAngleInRad, double previousAngleInRad) {
        CoordinatesHolder childPositionInCircleSystem = getChildPositionOnCircleSystemByLayoutAngle(child, currentAngleInRad);

        // for bottom of the child view (bottom edge of the sector)
        CoordinatesHolder first = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), currentAngleInRad),
                childPositionInCircleSystem
        );
        CoordinatesHolder second = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), currentAngleInRad),
                childPositionInCircleSystem
        );

        // top edge of the sector
        CoordinatesHolder third = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), previousAngleInRad),
                childPositionInCircleSystem
        );
        CoordinatesHolder four = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), previousAngleInRad),
                childPositionInCircleSystem
        );

        return new LinearClipData(first, second, third, four);
    }

//    CoordinatesHolder firstChildPosition = getChildPositionOnScreenByLayoutAngle(MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
//    CoordinatesHolder secondPosition = getChildPositionOnScreenByLayoutAngle(2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
//    CoordinatesHolder thirdPosition = getChildPositionOnScreenByLayoutAngle(3 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
//
//    Log.e("TAG", "First child Coords: " + firstChildPosition.toString());
//
//    ItemView firstChild = (ItemView) createAndMeasureNewView();
//    firstChild.setLinearClipData(getClipDataForChild(0, MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD));
//    setupChild(firstChild, firstChildPosition);
//
//    ItemView secondChild = (ItemView) createAndMeasureNewView();
//    secondChild.setLinearClipData(getClipDataForChild(MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD, 2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD));
//    setupChild(secondChild, secondPosition);
//
//    ItemView thirdChild = (ItemView) createAndMeasureNewView();
//    thirdChild.setLinearClipData(getClipDataForChild(2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD, 3 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD));
//    setupChild(thirdChild, thirdPosition);


    private LinearClipData getClipDataForChild(double prevAngleInRad, double newAngleInRad) {

        CoordinatesHolder viewPosInCircCoords = calculationHelper.getViewPositionForAngle(newAngleInRad);

        CoordinatesHolder first = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), prevAngleInRad),
                viewPosInCircCoords
        );

        CoordinatesHolder second = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), prevAngleInRad),
                viewPosInCircCoords
        );

        CoordinatesHolder third = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), newAngleInRad),
                viewPosInCircCoords
        );

        CoordinatesHolder four = calculationHelper.toViewCoordinateSystem(
                calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), newAngleInRad),
                viewPosInCircCoords
        );

        return new LinearClipData(first, second, third, four);
    }

    private void setupChild(View child, CoordinatesHolder childPosition) {
        int curLeft = (int) childPosition.getX();
        int curTop = (int) childPosition.getY();
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();
        child.layout(curLeft, curTop, curLeft + childWidth, curTop + childHeight);
        addView(child);
    }

    private CoordinatesHolder getChildPositionOnScreenByLayoutAngle(View child, double angleInRad) {
        return calculationHelper.toScreenCoordinates(getChildPositionOnCircleSystemByLayoutAngle(child, angleInRad));
    }

    // todo: calculations has to be cached in layout level Map and cleared on next layout stage
    private CoordinatesHolder getChildPositionOnCircleSystemByLayoutAngle(View child, double angleInRad) {
        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();
        int middleRadius = getMiddleRadius();
        CoordinatesHolder middleCoordinates = calculationHelper.getIntersectionByAngle(middleRadius, angleInRad);

        double childX = middleCoordinates.getX() - childWidth / 2;
        double childY = middleCoordinates.getY() + childHeight;
        return CoordinatesHolder.ofRect(childX, childY);
    }

    private int getMiddleRadius() {
        if (middleRadius == NOT_DEFINED_VALUE) {
            middleRadius = calculationHelper.getInnerRadius()
                    + (calculationHelper.getOuterRadius() - calculationHelper.getInnerRadius()) / 2;
        }
        return middleRadius;
    }

    private void rotateChild(double currentLayoutAngleInRad, View child) {
        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();

        child.setPivotX(childWidth / 2);
        child.setPivotY(childHeight);


        double angleToRotate = -MagicCalculationHelper.fromRadToDegree(currentLayoutAngleInRad);
        child.setRotation((float)angleToRotate);
    }

    private CoordinatesHolder getPosOnScreenForNegativeAngle(double angleInRad, View child) {
//        angleInRad -= MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD;

        CoordinatesHolder innerIntersection = calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), angleInRad);
        CoordinatesHolder outerIntersection = calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), angleInRad);
        CoordinatesHolder viewCircleCoord = CoordinatesHolder.ofRect(innerIntersection.getX(), outerIntersection.getY() + child.getMeasuredHeight());
        return calculationHelper.toScreenCoordinates(viewCircleCoord);
//        return CoordinatesHolder.ofRect(innerIntersection.getX(), outerIntersection.getY());
    }

    private View createAndMeasureNewView() {
        ItemView stubView = (ItemView) LayoutInflater.from(getContext()).inflate(R.layout.item_view_layout, this, false);

        final int childWidthSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_WIDTH, MeasureSpec.EXACTLY);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_HEIGHT, MeasureSpec.EXACTLY);
        stubView.measure(childWidthSpec, childHeightSpec);

//        stubView.setImageDrawable(new ColorDrawable(getRandomBackgroundColor()));
        stubView.setImageDrawable(getResources().getDrawable(R.drawable.second_cover));
//        stubView.setBackgroundColor(getRandomBackgroundColor());
//        stubView.setAlpha(0.3f);
        return stubView;
    }


    private int getRandomBackgroundColor() {
//        int index = randomizer.nextInt(AVAILABLE_VIEW_COLORS.length);
//        return AVAILABLE_VIEW_COLORS[index];

        return Color.BLUE;
    }


    /**
     * In order to compensate view's rotation we have
     * to rotate clip area in opposite direction.
     */
    private static class ChildViewCanvasRotationCompensator {

        /**
         * Base on this line child's bottom will be aligned.
         */
        LineBetween bottomRay;

        LineBetween topRay;

        public ChildViewCanvasRotationCompensator(LineBetween bottomRay, LineBetween topRay) {
            this.bottomRay = bottomRay;
            this.topRay = topRay;
        }

        public LineBetween getCompensationForBottomRay(double compensationAngleInRad) {
            return null;
        }

        public LineBetween getCompensationForTopRay(double compensationAngleInRad) {
            return null;
        }

        private static class LineBetween {

            /**
             * The one closer to the circle's center.
             */
            CoordinatesHolder pointOne;

            /**
             * The one farther of the circle's center.
             */
            CoordinatesHolder pointTwo;

            public LineBetween(CoordinatesHolder pointOne, CoordinatesHolder pointTwo) {
                this.pointOne = pointOne;
                this.pointTwo = pointTwo;
            }
        }
    }


}