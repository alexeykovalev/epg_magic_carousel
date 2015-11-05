package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.util.MagicCalculationHelper;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class DrawingLayerView extends View {

    private static final String TAG = DrawingLayerView.class.getCanonicalName();

    private static final float OVAL_DELTA = 800;

    private static final float OVAL_LEFT = 50;
    private static final float OVAL_TOP = 20;
    private static final float OVAL_RIGHT = OVAL_LEFT + OVAL_DELTA;
    private static final float OVAL_BOTTOM = OVAL_TOP + OVAL_DELTA;

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

//        Paint p = new Paint();
//        // smooths
//        p.setAntiAlias(true);
//        p.setColor(Color.RED);
//        p.setStyle(Paint.Style.STROKE);
//        p.setStrokeWidth(5);
//        // opacity
//        //p.setAlpha(0x80); //
//
//        RectF rectF = new RectF(OVAL_LEFT, OVAL_TOP, OVAL_RIGHT, OVAL_BOTTOM);
//        canvas.drawOval(rectF, p);
//        p.setColor(Color.GREEN);
//        canvas.drawArc(rectF, 0, 45, true, p);




        final int circleCentreX = calculationHelper.getCircleCenter().x;
        final int circleCentreY = calculationHelper.getCircleCenter().y;

        // draw inner circle
        canvas.drawCircle(circleCentreX, circleCentreY, calculationHelper.getInnerRadius(), paint);

        // draw outer circle
        canvas.drawCircle(circleCentreX, circleCentreY, calculationHelper.getOuterRadius(), paint);

        CoordinatesHolder innerRadInterc = calculationHelper.toScreenCoordinates(
                calculationHelper.getStartIntercectForOuterRadius()
        );

        CoordinatesHolder forIitialAngle = calculationHelper.toScreenCoordinates(
                CoordinatesHolder.ofPolar(calculationHelper.getOuterRadius(), 0)
        );

        CoordinatesHolder forFirstStepAngle = calculationHelper.toScreenCoordinates(
                CoordinatesHolder.ofPolar(calculationHelper.getOuterRadius(),
                        MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD)
        );

        CoordinatesHolder forSecondStepAngle = calculationHelper.toScreenCoordinates(
                CoordinatesHolder.ofPolar(calculationHelper.getOuterRadius(),
                        MagicCalculationHelper.TEST_ANGLE_STEP_IN_RAD * 2)
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

    }

}