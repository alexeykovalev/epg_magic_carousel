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
// todo: needs to be done:
    // 1. Add Fling action
    // 2. Add views recycling
    // 3. Extract views from Adapter
    // 4. Calculate view's height and width depending on sector's configuration
public class MagicWheelView extends ViewGroup implements IScrollable {

    private static final String TAG = MagicWheelView.class.getCanonicalName();

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

    /**
     * Don't use directly (might not be initialized yet).
     * Use {@link #getMiddleRadius()} instead of.
     */
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

        maxAngleInRad = calculationHelper.getStartAngle() + MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD;
        // todo: simple for now due do circle is symmetric
        minAngleInRad = -calculationHelper.getStartAngle() - MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD;
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
        return 0;
    }

    @Override
    public int scrollVerticallyBy(int dy) {
        Log.i(TAG, "scrollVerticallyBy() dy [" + dy + "]");

        double angleDeltaValue = (double)dy / calculationHelper.getOuterRadius();
        currentAngleInRad += angleDeltaValue;

        updateAngles();
        requestLayout();

        // todo:
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

            LinearClipData childClipArea = getChildClipArea(child, angleInRad);
            child.setClipArea(childClipArea);

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
     */
    private LinearClipData getChildClipArea(View child, double currentAngleInRad) {
        CoordinatesHolder childPositionInCircleSystem = getChildPositionOnCircleSystemByLayoutAngle(child, currentAngleInRad);

        double topAngle = getSectorTopEdgeAngle(child, currentAngleInRad);
        double bottomAngle = getSectorBottomEdgeAngle(child, currentAngleInRad);

        CoordinatesHolder pivot = calculationHelper.getIntersectionByAngle(getMiddleRadius(), currentAngleInRad);

        CoordinatesHolder rotatedTopLeftCorner = getRotatedViewTopLeftCornerInCircleSystem(
                childPositionInCircleSystem, pivot, currentAngleInRad
        );

        CoordinatesHolder firstInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), bottomAngle);
        CoordinatesHolder secondInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), bottomAngle);
        CoordinatesHolder thirdInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), topAngle);
        CoordinatesHolder fourthInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), topAngle);

        return new LinearClipData(
                getPointRelativeToRotatedViewInViewSystem(firstInCircle, rotatedTopLeftCorner, currentAngleInRad),
                getPointRelativeToRotatedViewInViewSystem(secondInCircle, rotatedTopLeftCorner, currentAngleInRad),
                getPointRelativeToRotatedViewInViewSystem(thirdInCircle, rotatedTopLeftCorner, currentAngleInRad),
                getPointRelativeToRotatedViewInViewSystem(fourthInCircle, rotatedTopLeftCorner, currentAngleInRad)
        );
    }

    /**
     * Returns sector's top edge angle.
     */
    private double getSectorTopEdgeAngle(View child, double currentAngleInRad) {
        final double halfWidth = (double) child.getMeasuredWidth() / 2;
        final double halfHeight = (double) child.getMeasuredHeight() / 2;
        final double torRightCornerPos = getMiddleRadius() + halfWidth;

        return currentAngleInRad + Math.atan2(halfHeight, torRightCornerPos);
    }

    /**
     * Returns sector's bottom edge angle.
     */
    private double getSectorBottomEdgeAngle(View child, double currentAngleInRad) {
        final double halfWidth = (double) child.getMeasuredWidth() / 2;
        final double halfHeight = (double) child.getMeasuredHeight() / 2;
        final double torRightCornerPos = getMiddleRadius() + halfWidth;

        return currentAngleInRad - Math.atan2(halfHeight, torRightCornerPos);
    }

    private CoordinatesHolder getRotatedViewTopLeftCornerInCircleSystem(CoordinatesHolder cornerInCircleSystem,
                                                                        CoordinatesHolder pivotInCircleSystem,
                                                                        double rotationAngleInRad) {

        CoordinatesHolder cornerCoordRelativeToPivot = CoordinatesHolder.ofRect(
                cornerInCircleSystem.getX() - pivotInCircleSystem.getX(),
                cornerInCircleSystem.getY() - pivotInCircleSystem.getY()
        );

        CoordinatesHolder newCornerPositionInRotationSystem = CoordinatesHolder.ofPolar(
                cornerCoordRelativeToPivot.getRadius(),
                cornerCoordRelativeToPivot.getAngleInRad() + rotationAngleInRad
        );

        return CoordinatesHolder.ofRect(
                newCornerPositionInRotationSystem.getX() + pivotInCircleSystem.getX(),
                newCornerPositionInRotationSystem.getY() + pivotInCircleSystem.getY()
        );
    }


    private CoordinatesHolder getPointRelativeToRotatedViewInViewSystem(CoordinatesHolder pointInCircleSystem,
                                                                        CoordinatesHolder leftTopPointOfRotatedViewInCircleSystem,
                                                                        double rotationAngleInRad) {
        // considers coordinate system transition
        double transitedX = pointInCircleSystem.getX() - leftTopPointOfRotatedViewInCircleSystem.getX();
        double transitedY = pointInCircleSystem.getY() - leftTopPointOfRotatedViewInCircleSystem.getY();

        double x = transitedX * Math.cos(rotationAngleInRad) + transitedY * Math.sin(rotationAngleInRad);
        double y = transitedX * Math.sin(rotationAngleInRad) - transitedY * Math.cos(rotationAngleInRad);

        return CoordinatesHolder.ofRect(x, y);
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
        double childY = middleCoordinates.getY() + childHeight / 2;
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
        child.setPivotY(childHeight / 2);

        double angleToRotate = -MagicCalculationHelper.fromRadToDegree(currentLayoutAngleInRad);
        child.setRotation((float)angleToRotate);
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

}