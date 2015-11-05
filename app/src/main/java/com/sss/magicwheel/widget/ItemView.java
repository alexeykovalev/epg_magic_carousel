package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.sss.magicwheel.entity.CoordinatesHolder;
import com.sss.magicwheel.entity.CustomRect;
import com.sss.magicwheel.entity.LinearClipData;
import com.sss.magicwheel.util.MagicCalculationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
public class ItemView extends ImageView {

    private static final String TAG = ItemView.class.getCanonicalName();

    private final Paint paint;
    private final MagicCalculationHelper calculationHelper;
    private Path path;
    private LinearClipData linearClipData;


    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        calculationHelper = MagicCalculationHelper.getInstance();

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        paint.setColor(Color.RED);

        path = new Path();
    }


    public void setLinearClipData(LinearClipData linearClipData) {
        this.linearClipData = linearClipData;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (linearClipData == null) {
            super.onDraw(canvas);
            return;
        }
        Log.e("TAG", "clipData is NOT NULL");

        Path pathToClip = createPathForClip(linearClipData, canvas);
        canvas.clipPath(pathToClip);

        super.onDraw(canvas);
    }


    private Path createPathForClip(LinearClipData clipData, Canvas canvas) {
        path.reset();

        CoordinatesHolder first = linearClipData.getFirst();
        CoordinatesHolder second = linearClipData.getSecond();
        CoordinatesHolder third = linearClipData.getThird();
        CoordinatesHolder four = linearClipData.getFourth();

        path.moveTo(first.getXAsFloat(), first.getYAsFloat());
        path.lineTo(second.getXAsFloat(), second.getYAsFloat());
        path.lineTo(four.getXAsFloat(), four.getYAsFloat());
        path.lineTo(third.getXAsFloat(), third.getYAsFloat());
        path.close();

        return path;

    }

    @Deprecated // todo: fuck knows why but does not work
    public RectF getOuterOvalCoords() {
        CustomRect outOvalCor = calculationHelper.getOvalCoordsInCircleSystem();
        // [590.4327388027447; 395.5375617430651]


        CoordinatesHolder topLeftInScreen = calculationHelper.toScreenCoordinates(outOvalCor.getTopLeftCorner());
        CoordinatesHolder rightBottomInScreen = calculationHelper.toScreenCoordinates(outOvalCor.getBottomRightCorner());

        CoordinatesHolder topLeftInView = toViewCoords(topLeftInScreen);
        CoordinatesHolder rightBotInView = toViewCoords(rightBottomInScreen);


        final float left = new Float(topLeftInView.getX());
        final float top = new Float(topLeftInView.getY());
        final float right = new Float(rightBotInView.getX());
        final float bottom = new Float(rightBotInView.getY());

        return new RectF(left, top, right, bottom);
    }

    private CoordinatesHolder toViewCoords(CoordinatesHolder from) {
        return /*from;*/ CoordinatesHolder.ofRect(from.getX() - 590, from.getY() - 395);
    }
}
