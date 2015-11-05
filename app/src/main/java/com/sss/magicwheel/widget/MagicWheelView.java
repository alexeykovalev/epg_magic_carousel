package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    private static final int STUB_VIEW_WIDTH = 400;
    private static final int STUB_VIEW_HEIGHT = 200;
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

        double angle = layoutStartAngleInRad;
        while (angle > minAngleInRad) {
            ItemView child = (ItemView) createAndMeasureNewView();
            child.setLinearClipData(getClipDataForChild(angle, angle + MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD));
            CoordinatesHolder childPositionOnScreen = getPositionCoordinatesForAngleOnScreenCoords(angle, child);
            setupChild(child, childPositionOnScreen);
            angle -= MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD;
        }


        isInLayoutStage = false;
    }

//    CoordinatesHolder firstChildPosition = getPositionCoordinatesForAngleOnScreenCoords(MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
//    CoordinatesHolder secondPosition = getPositionCoordinatesForAngleOnScreenCoords(2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
//    CoordinatesHolder thirdPosition = getPositionCoordinatesForAngleOnScreenCoords(3 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
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

        CoordinatesHolder first = calculationHelper.toViewCoordinate(
                calculationHelper.getIntersectForAngle(calculationHelper.getInnerRadius(), prevAngleInRad),
                viewPosInCircCoords
        );

        CoordinatesHolder second = calculationHelper.toViewCoordinate(
                calculationHelper.getIntersectForAngle(calculationHelper.getOuterRadius(), prevAngleInRad),
                viewPosInCircCoords
        );

        CoordinatesHolder third = calculationHelper.toViewCoordinate(
                calculationHelper.getIntersectForAngle(calculationHelper.getInnerRadius(), newAngleInRad),
                viewPosInCircCoords
        );

        CoordinatesHolder four = calculationHelper.toViewCoordinate(
                calculationHelper.getIntersectForAngle(calculationHelper.getOuterRadius(), newAngleInRad),
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

    private CoordinatesHolder getPositionCoordinatesForAngleOnScreenCoords(double angleInRad, View child) {
        if (angleInRad > 0) {
            return calculationHelper.toScreenCoordinates(calculationHelper.getViewPositionForAngle(angleInRad));
        } else {
            return getPosOnScreenForNegativeAngle(angleInRad, child);
        }
    }

    private CoordinatesHolder getPosOnScreenForNegativeAngle(double angleInRad, View child) {
//        angleInRad -= MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD;

        CoordinatesHolder innerIntersection = calculationHelper.getIntersectForAngle(calculationHelper.getInnerRadius(), angleInRad);
        CoordinatesHolder outerIntersection = calculationHelper.getIntersectForAngle(calculationHelper.getOuterRadius(), angleInRad);
        CoordinatesHolder viewCircleCoord = CoordinatesHolder.ofRect(innerIntersection.getX(), outerIntersection.getY() + child.getMeasuredHeight());
        return calculationHelper.toScreenCoordinates(viewCircleCoord);
//        return CoordinatesHolder.ofRect(innerIntersection.getX(), outerIntersection.getY());
    }

    private View createAndMeasureNewView() {
        ItemView stubView = (ItemView) LayoutInflater.from(getContext()).inflate(R.layout.item_view_layout, this, false);

        final int childWidthSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_WIDTH, MeasureSpec.EXACTLY);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_HEIGHT, MeasureSpec.EXACTLY);
        stubView.measure(childWidthSpec, childHeightSpec);

        stubView.setImageDrawable(new ColorDrawable(getRandomBackgroundColor()));
//        stubView.setBackgroundColor(getRandomBackgroundColor());
        stubView.setAlpha(0.3f);
        return stubView;
    }


    private int getRandomBackgroundColor() {
//        int index = randomizer.nextInt(AVAILABLE_VIEW_COLORS.length);
//        return AVAILABLE_VIEW_COLORS[index];

        return Color.BLUE;
    }


}