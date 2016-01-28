package com.sss.magicwheel.manager;

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
import com.sss.magicwheel.entity.SectorClipAreaDescriptor;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public class WheelSectorWrapperView extends ImageView {

    private static final String TAG = WheelSectorWrapperView.class.getCanonicalName();

    private final Paint paint;
    private Path path;
    private SectorClipAreaDescriptor sectorClipAreaDescriptor;
    private RectF outerCircleEmbracingSquare;
    private RectF innerCircleEmbracingSquare;


    public WheelSectorWrapperView(Context context) {
        this(context, null);
    }

    public WheelSectorWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);
        paint.setColor(Color.RED);

        path = new Path();
    }


    public void setSectorClipArea(SectorClipAreaDescriptor sectorClipAreaDescriptor) {
        this.sectorClipAreaDescriptor = sectorClipAreaDescriptor;
        this.outerCircleEmbracingSquare = sectorClipAreaDescriptor.getCircleEmbracingSquaresConfig().getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem();
        this.innerCircleEmbracingSquare = sectorClipAreaDescriptor.getCircleEmbracingSquaresConfig().getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw()");

        if (sectorClipAreaDescriptor == null) {
            super.onDraw(canvas);
            return;
        }

        Log.e(TAG, "Clip area " + sectorClipAreaDescriptor);

//        drawArcs(canvas);

        Path pathToClip = createSectorPathForClip();
        canvas.clipPath(pathToClip);

        super.onDraw(canvas);
    }

    @Deprecated
    private void drawArcs(Canvas canvas) {
        RectF rectF = outerCircleEmbracingSquare;
        canvas.drawArc(rectF, 10, -20, false, paint);

        rectF = innerCircleEmbracingSquare;
        canvas.drawArc(rectF, 10, -20, true, paint);
    }

    private Path createSectorPathForClip() {
        path.reset();

        CoordinatesHolder bottomLeftCorner = sectorClipAreaDescriptor.getBottomLeftCorner();
        CoordinatesHolder bottomRightCorner = sectorClipAreaDescriptor.getBottomRightCorner();
        CoordinatesHolder topLeftCorner = sectorClipAreaDescriptor.getTopLeftCorner();
        CoordinatesHolder topRightCorner = sectorClipAreaDescriptor.getTopRightCorner();

        path.moveTo(topLeftCorner.getXAsFloat(), topLeftCorner.getYAsFloat());
        path.lineTo(bottomRightCorner.getXAsFloat(), bottomRightCorner.getYAsFloat());
        path.arcTo(innerCircleEmbracingSquare, sectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(), -sectorClipAreaDescriptor.getSectorSweepAngleInDegree());
        path.arcTo(outerCircleEmbracingSquare, -sectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(), sectorClipAreaDescriptor.getSectorSweepAngleInDegree());
        path.lineTo(topLeftCorner.getXAsFloat(), topLeftCorner.getYAsFloat());

        path.close();

        return path;
    }


    private Path createLinearPathForClip() {
        path.reset();

        CoordinatesHolder first = sectorClipAreaDescriptor.getBottomLeftCorner();
        CoordinatesHolder second = sectorClipAreaDescriptor.getBottomRightCorner();
        CoordinatesHolder third = sectorClipAreaDescriptor.getTopLeftCorner();
        CoordinatesHolder four = sectorClipAreaDescriptor.getTopRightCorner();

        path.moveTo(first.getXAsFloat(), first.getYAsFloat());
        path.lineTo(second.getXAsFloat(), second.getYAsFloat());
        path.lineTo(four.getXAsFloat(), four.getYAsFloat());
        path.lineTo(third.getXAsFloat(), third.getYAsFloat());

        path.close();

        return path;

    }

}
