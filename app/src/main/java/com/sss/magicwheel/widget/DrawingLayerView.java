package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.CustomRect;
import com.sss.magicwheel.util.MagicCalculationHelper;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class DrawingLayerView extends View {

    private static final String TAG = DrawingLayerView.class.getCanonicalName();

    private static final float OVAL_DELTA = 800;

    private final MagicCalculationHelper calculationHelper;
    private Paint paint;

    public DrawingLayerView(Context context) {
        this(context, null);
    }

    public DrawingLayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        calculationHelper = MagicCalculationHelper.getInstance();
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final int circleCentreX = calculationHelper.getCircleCenter().x;
        final int circleCentreY = calculationHelper.getCircleCenter().y;

        // draw inner circle
        canvas.drawCircle(circleCentreX, circleCentreY, calculationHelper.getInnerRadius(), paint);

        // draw outer circle
        canvas.drawCircle(circleCentreX, circleCentreY, calculationHelper.getOuterRadius(), paint);

        // ray to top intersection
        CoordinatesHolder innerRadInterc = calculationHelper.toScreenCoordinates(
                calculationHelper.getStartIntercectForOuterRadius()
        );

        CoordinatesHolder forIitialAngle = calculationHelper.toScreenCoordinates(
                calculationHelper.getIntersectForAngle(calculationHelper.getOuterRadius(), 0)
        );

        CoordinatesHolder forFirstStepAngle = calculationHelper.toScreenCoordinates(
                calculationHelper.getIntersectForAngle(calculationHelper.getOuterRadius(), MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD)
        );

        CoordinatesHolder forSecondStepAngle = calculationHelper.toScreenCoordinates(
                calculationHelper.getIntersectForAngle(calculationHelper.getOuterRadius(), 2 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD)
        );

        CoordinatesHolder forThirdStepAngle = calculationHelper.toScreenCoordinates(
                calculationHelper.getIntersectForAngle(calculationHelper.getOuterRadius(), 3 * MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD)
        );

        Log.e(TAG, "innerRadInterc " + innerRadInterc.toString());

        canvas.drawLine(circleCentreX, circleCentreY,
                new Float(innerRadInterc.getX()), new Float(innerRadInterc.getY()), paint);

        canvas.drawLine(circleCentreX, circleCentreY,
                new Float(forIitialAngle.getX()), new Float(forIitialAngle.getY()), paint);

        canvas.drawLine(circleCentreX, circleCentreY,
                new Float(forFirstStepAngle.getX()), new Float(forFirstStepAngle.getY()), paint);

        canvas.drawLine(circleCentreX, circleCentreY,
                new Float(forSecondStepAngle.getX()), new Float(forSecondStepAngle.getY()), paint);

        canvas.drawLine(circleCentreX, circleCentreY,
                new Float(forThirdStepAngle.getX()), new Float(forThirdStepAngle.getY()), paint);

        paint.setColor(Color.RED);
        canvas.drawOval(getOuterOvalCoords(), paint);

    }

    public RectF getOuterOvalCoords() {
        CustomRect outOvalCor = calculationHelper.getOvalCoordsInCircleSystem();
        CoordinatesHolder topLeftInScreen = calculationHelper.toScreenCoordinates(outOvalCor.getTopLeftCorner());
        CoordinatesHolder rightBottomInScreen = calculationHelper.toScreenCoordinates(outOvalCor.getBottomRightCorner());

        final float left = new Float(topLeftInScreen.getX());
        final float top = new Float(topLeftInScreen.getY());
        final float right = new Float(rightBottomInScreen.getX());
        final float bottom = new Float(rightBottomInScreen.getY());

        return new RectF(left, top, right, bottom);
    }
}