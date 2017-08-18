package com.magicwheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.magicwheel.entity.Color;
import com.magicwheel.entity.CoordinatesHolder;
import com.magicwheel.entity.SectorClipAreaDescriptor;
import com.magicwheel.entity.WheelDataItem;
import com.magicwheel.util.DimensionUtils;


/**
 * Defines shape of the sector view which will be positioned on
 * wheel. Data describing sector's shape declared in
 * {@link SectorClipAreaDescriptor}
 *
 * @author Alexey Kovalev
 * @since 04.12.2016
 */
public class WheelSectorWrapperView extends android.support.v7.widget.AppCompatImageView {

    private static final int SECTOR_EDGE_RING_THICKNESS_IN_DP = 15;

    private final Paint sectorLeftEdgeDrawingPaint;
    private final Path sectorShapePath;
    private boolean isSectorShapePathInitialized;

    private Color leftEdgeColor = WheelDataItem.DEFAULT_LEFT_EDGE_COLOR;
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
        paint.setColor(WheelDataItem.DEFAULT_LEFT_EDGE_COLOR.toPackedInt());
        paint.setStrokeWidth(DimensionUtils.dpToPixels(SECTOR_EDGE_RING_THICKNESS_IN_DP));
        paint.setAntiAlias(true);
        return paint;
    }

    public void setSectorClipArea(SectorClipAreaDescriptor sectorClipAreaDescriptor) {
        this.sectorClipAreaDescriptor = sectorClipAreaDescriptor;
        this.outerCircleEmbracingSquare = sectorClipAreaDescriptor.getWheelEmbracingSquaresConfig().getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem();
        this.innerCircleEmbracingSquare = sectorClipAreaDescriptor.getWheelEmbracingSquaresConfig().getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem();
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

    public void setSectorLeftEdgeColor(Color leftEdgeColor) {
        this.leftEdgeColor = leftEdgeColor;
    }

    private void drawSectorLeftEdgeColor(Canvas canvas) {
        sectorLeftEdgeDrawingPaint.setColor(leftEdgeColor.toPackedInt());
        canvas.drawArc(
                innerCircleEmbracingSquare,
                sectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(),
                -sectorClipAreaDescriptor.getSectorSweepAngleInDegree(),
                false,
                sectorLeftEdgeDrawingPaint
        );
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

    private Path createTrapezeAreaForClip() {
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
