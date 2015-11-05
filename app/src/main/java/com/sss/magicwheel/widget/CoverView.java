package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.util.MagicCalculationHelper;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class CoverView extends View {

    private static final float OVAL_DELTA = 800;

    private static final float OVAL_LEFT = 50;
    private static final float OVAL_TOP = 20;
    private static final float OVAL_RIGHT = OVAL_LEFT + OVAL_DELTA;
    private static final float OVAL_BOTTOM = OVAL_TOP + OVAL_DELTA;

    private final MagicCalculationHelper calculationHelper;


    public CoverView(Context context) {
        this(context, null);

    }

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        calculationHelper = MagicCalculationHelper.getInstance();
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


        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.GREEN);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);

        canvas.drawCircle(calculationHelper.getCircleCenter().x,
                calculationHelper.getCircleCenter().y, calculationHelper.getInnerRadius(), p
        );

        canvas.drawCircle(calculationHelper.getCircleCenter().x,
                calculationHelper.getCircleCenter().y, calculationHelper.getOuterRadius(), p
        );

        MagicCalculationHelper.CoordinateHolder innerRadInterc = calculationHelper.toScreenCoordinates(
                calculationHelper.getStartIntercectForInnerRadius());

        canvas.drawLine(calculationHelper.getCircleCenter().x, calculationHelper.getCircleCenter().y,
                new Float(innerRadInterc.getX()), new Float(innerRadInterc.getY()), p);
    }

}