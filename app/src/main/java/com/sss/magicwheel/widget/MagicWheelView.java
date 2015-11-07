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

            LinearClipData childClipArea = getChildClipArea(
                    child,
                    angleInRad,
                    angleInRad + MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD
            );
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
     * @param previousAngleInRad - previous layout angle
     */
    private LinearClipData getChildClipArea(View child, double currentAngleInRad, double previousAngleInRad) {
        CoordinatesHolder childPositionInCircleSystem = getChildPositionOnCircleSystemByLayoutAngle(child, currentAngleInRad);
        CoordinatesHolder pivot = calculationHelper.getIntersectionByAngle(getMiddleRadius(), currentAngleInRad);
        CoordinatesHolder rotatedTopLeftCorner = getRotatedViewTopLeftCornerInCircleSystem(
                childPositionInCircleSystem, pivot, currentAngleInRad
        );

        CoordinatesHolder firstInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), currentAngleInRad);
        CoordinatesHolder secondInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), currentAngleInRad);
        CoordinatesHolder thirdInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getInnerRadius(), previousAngleInRad);
        CoordinatesHolder fourthInCircle = calculationHelper.getIntersectionByAngle(calculationHelper.getOuterRadius(), previousAngleInRad);

        return new LinearClipData(
                getPointRelativeToRotatedViewInViewSystem(firstInCircle, rotatedTopLeftCorner, currentAngleInRad),
                getPointRelativeToRotatedViewInViewSystem(secondInCircle, rotatedTopLeftCorner, currentAngleInRad),
                getPointRelativeToRotatedViewInViewSystem(thirdInCircle, rotatedTopLeftCorner, currentAngleInRad),
                getPointRelativeToRotatedViewInViewSystem(fourthInCircle, rotatedTopLeftCorner, currentAngleInRad)
        );
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