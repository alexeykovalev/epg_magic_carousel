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
import com.sss.magicwheel.entity.WheelDataItem;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public class WheelSectorWrapperView extends ImageView {

    private static final String TAG = WheelSectorWrapperView.class.getCanonicalName();

    private static final int SECTOR_EDGE_DEFAULT_COLOR = Color.GRAY;
    private static final int SECTOR_EDGE_RING_THICKNESS = 25;

    private final Paint sectorLeftEdgeDrawingPaint;
    private final Path sectorShapePath;
    private boolean isSectorShapePathInitialized;

    private WheelDataItem dataItem;
    private SectorClipAreaDescriptor sectorClipAreaDescriptor;
    private RectF outerCircleEmbracingSquare;
    private RectF innerCircleEmbracingSquare;

    public WheelSectorWrapperView(Context context) {
        this(context, null);
    }

    public WheelSectorWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.sectorShapePath = new Path();
        this.sectorLeftEdgeDrawingPaint = createSectorEdgeDrawingPaint();
    }

    private static Paint createSectorEdgeDrawingPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(SECTOR_EDGE_DEFAULT_COLOR);
        paint.setStrokeWidth(SECTOR_EDGE_RING_THICKNESS);
        paint.setAntiAlias(true);
        return paint;
    }

    public void setSectorClipArea(SectorClipAreaDescriptor sectorClipAreaDescriptor) {
        this.sectorClipAreaDescriptor = sectorClipAreaDescriptor;
        this.outerCircleEmbracingSquare = sectorClipAreaDescriptor.getCircleEmbracingSquaresConfig().getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem();
        this.innerCircleEmbracingSquare = sectorClipAreaDescriptor.getCircleEmbracingSquaresConfig().getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (sectorClipAreaDescriptor == null) {
            super.onDraw(canvas);
            return;
        }

        Path pathToClip = createSectorPathForClip();
        canvas.clipPath(pathToClip);

        super.onDraw(canvas);

        drawSectorLeftEdgeColor(canvas);
    }

    public void bindData(WheelDataItem dataItem) {
        this.dataItem = dataItem;
    }

    private void drawSectorLeftEdgeColor(Canvas canvas) {
        if (dataItem != null) {
            sectorLeftEdgeDrawingPaint.setColor(dataItem.getSectorLeftEdgeColor());
            canvas.drawArc(innerCircleEmbracingSquare, sectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(),
                    -sectorClipAreaDescriptor.getSectorSweepAngleInDegree(), false, sectorLeftEdgeDrawingPaint);
        }
    }

    private Path createSectorPathForClip() {
        if (!isSectorShapePathInitialized) {
            sectorShapePath.reset();

            CoordinatesHolder bottomLeftCorner = sectorClipAreaDescriptor.getBottomLeftCorner();
            CoordinatesHolder bottomRightCorner = sectorClipAreaDescriptor.getBottomRightCorner();
            CoordinatesHolder topLeftCorner = sectorClipAreaDescriptor.getTopLeftCorner();
            CoordinatesHolder topRightCorner = sectorClipAreaDescriptor.getTopRightCorner();

            sectorShapePath.moveTo(topLeftCorner.getXAsFloat(), topLeftCorner.getYAsFloat());
            sectorShapePath.lineTo(bottomRightCorner.getXAsFloat(), bottomRightCorner.getYAsFloat());
            sectorShapePath.arcTo(innerCircleEmbracingSquare, sectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(), -sectorClipAreaDescriptor.getSectorSweepAngleInDegree());
            sectorShapePath.arcTo(outerCircleEmbracingSquare, -sectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(), sectorClipAreaDescriptor.getSectorSweepAngleInDegree());
            sectorShapePath.lineTo(topLeftCorner.getXAsFloat(), topLeftCorner.getYAsFloat());

            sectorShapePath.close();
            isSectorShapePathInitialized = true;
        }
        return sectorShapePath;
    }

    private Path createLinearPathForClip() {
        sectorShapePath.reset();

        CoordinatesHolder first = sectorClipAreaDescriptor.getBottomLeftCorner();
        CoordinatesHolder second = sectorClipAreaDescriptor.getBottomRightCorner();
        CoordinatesHolder third = sectorClipAreaDescriptor.getTopLeftCorner();
        CoordinatesHolder four = sectorClipAreaDescriptor.getTopRightCorner();

        sectorShapePath.moveTo(first.getXAsFloat(), first.getYAsFloat());
        sectorShapePath.lineTo(second.getXAsFloat(), second.getYAsFloat());
        sectorShapePath.lineTo(four.getXAsFloat(), four.getYAsFloat());
        sectorShapePath.lineTo(third.getXAsFloat(), third.getYAsFloat());

        sectorShapePath.close();

        return sectorShapePath;
    }

}
