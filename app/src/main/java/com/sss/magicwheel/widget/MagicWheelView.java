package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.LinearClipData;
import com.sss.magicwheel.util.MagicCalculationHelper;

import java.util.Random;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class MagicWheelView extends ViewGroup {

    private static final int STUB_VIEW_WIDTH = 400;
    private static final int STUB_VIEW_HEIGHT = 200;
    private static final int[] AVAILABLE_VIEW_COLORS = new int[] {
            Color.BLUE, Color.RED, Color.YELLOW, Color.CYAN
    };

    private final MagicCalculationHelper calculationHelper;
    private final Random randomizer;

    public MagicWheelView(Context context) {
        this(context, null);
    }

    public MagicWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        randomizer = new Random();
        calculationHelper = MagicCalculationHelper.getInstance();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        removeAllViewsInLayout();

        CoordinatesHolder firstChildPosition = getPositionCoordinatesForAngleInScreenCoords(MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
        CoordinatesHolder secondPosition = getPositionCoordinatesForAngleInScreenCoords(2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);
        CoordinatesHolder thirdPosition = getPositionCoordinatesForAngleInScreenCoords(3 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD);

        Log.e("TAG", "First child Coords: " + firstChildPosition.toString());

        ItemView firstChild = (ItemView) createAndMeasureNewView();
        firstChild.setLinearClipData(getClipDataForChild(0, MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD));
        setupChild(firstChild, firstChildPosition);

        ItemView secondChild = (ItemView) createAndMeasureNewView();
        secondChild.setLinearClipData(getClipDataForChild(MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD, 2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD));
        setupChild(secondChild, secondPosition);

        ItemView thirdChild = (ItemView) createAndMeasureNewView();
        thirdChild.setLinearClipData(getClipDataForChild(2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD, 3 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD));
        setupChild(thirdChild, thirdPosition);
    }


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

    private CoordinatesHolder getPositionCoordinatesForAngleInScreenCoords(double angleInRad) {
        return calculationHelper.toScreenCoordinates(calculationHelper.getViewPositionForAngle(angleInRad));
    }

    private View createAndMeasureNewView() {
        View stubView = LayoutInflater.from(getContext()).inflate(R.layout.item_view_layout, this, false);

        final int childWidthSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_WIDTH, MeasureSpec.EXACTLY);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_HEIGHT, MeasureSpec.EXACTLY);
        stubView.measure(childWidthSpec, childHeightSpec);
        stubView.setBackgroundColor(getRandomBackgroundColor());
        stubView.setAlpha(0.3f);
        return stubView;
    }


    private int getRandomBackgroundColor() {
        int index = randomizer.nextInt(AVAILABLE_VIEW_COLORS.length);
        return AVAILABLE_VIEW_COLORS[index];
    }


}