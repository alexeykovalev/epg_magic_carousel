package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
public class PathView extends ImageView {

    private static final String TAG = PathView.class.getCanonicalName();

    private final Paint paint;

    private int xOffset = -100;
    private Rect rect;
    private Path path;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        rect = new Rect(300, 0, 410, 250);

        path = new Path();


    }

    @Override
    protected void onDraw(Canvas canvas) {

        path.reset();

        path.addCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 100, Path.Direction.CW);

//        path.arcTo();


//        makeAPathOfTriangle(canvas);


        canvas.clipPath(path);

        super.onDraw(canvas);
    }


    private void makeCircularSweepPath(Canvas canvas) {
        final int cw = canvas.getWidth();
        final int ch = canvas.getHeight();
        path.moveTo(0, ch);
        path.lineTo(cw / 2, 0);
//        path.addArc();
    }

    private void makeAPathOfTriangle(Canvas canvas) {
        path.moveTo(canvas.getWidth() / 2 + xOffset, canvas.getHeight());
        path.lineTo(canvas.getWidth(), 0 + xOffset);
        path.lineTo(canvas.getWidth(), canvas.getHeight());
        path.lineTo(canvas.getWidth() / 2 + xOffset, canvas.getHeight());
        path.close();
    }


    private void drawGrid(Canvas canvas) {


        for (int i = 25; i < 400; i += 25) {
            canvas.drawLine(100 + i, 100, 100 + i, 600, paint);
        }
        for (int i = 25; i < 500; i += 25) {
            canvas.drawLine(100, 100 + i, 500, 100 + i, paint);
        }

    }
}
