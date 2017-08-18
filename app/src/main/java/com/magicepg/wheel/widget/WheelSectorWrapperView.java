package com.magicepg.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.magicepg.util.DimensionUtils;
import com.magicepg.wheel.entity.WheelDataItem;
import com.magicepg.wheel.entity.WheelSectorClipAreaDescriptor;

import entity.Color;
import entity.CoordinatesHolder;


/**
 * Defines shape of the sector view which will be positioned on
 * wheel. Data describing sector's shape declared in
 * {@link WheelSectorClipAreaDescriptor}
 *
 * @author Alexey Kovalev
 * @since 04.12.2016
 */
public class WheelSectorWrapperView extends android.support.v7.widget.AppCompatImageView {

    private static final int SECTOR_EDGE_RING_THICKNESS_IN_DP = 15;

    private final Paint sectorLeftEdgeDrawingPaint;
    private final Path sectorShapeClipPath;
    private boolean isSectorShapeClipPathInitialized;

    private Color leftEdgeColor = WheelDataItem.DEFAULT_LEFT_EDGE_COLOR;
    private WheelSectorClipAreaDescriptor wheelSectorClipAreaDescriptor;
    private RectF outerCircleEmbracingSquare;
    private RectF innerCircleEmbracingSquare;

    public WheelSectorWrapperView(Context context) {
        this(context, null);
    }

    public WheelSectorWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.sectorShapeClipPath = new Path();
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

    public void setSectorClipArea(WheelSectorClipAreaDescriptor wheelSectorClipAreaDescriptor) {
        this.wheelSectorClipAreaDescriptor = wheelSectorClipAreaDescriptor;
        this.outerCircleEmbracingSquare = wheelSectorClipAreaDescriptor.getWheelEmbracingSquaresConfig().getOuterCircleEmbracingSquareInSectorWrapperCoordsSystem();
        this.innerCircleEmbracingSquare = wheelSectorClipAreaDescriptor.getWheelEmbracingSquaresConfig().getInnerCircleEmbracingSquareInSectorWrapperCoordsSystem();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (wheelSectorClipAreaDescriptor == null) {
            super.onDraw(canvas);
            return;
        }

        Path clipPath = createSectorShapeClipPath();
        canvas.clipPath(clipPath);

        super.onDraw(canvas);

        drawLeftEdgeColor(canvas);
    }

    public void setSectorLeftEdgeColor(Color leftEdgeColor) {
        this.leftEdgeColor = leftEdgeColor;
    }

    private void drawLeftEdgeColor(Canvas canvas) {
        sectorLeftEdgeDrawingPaint.setColor(leftEdgeColor.toPackedInt());
        canvas.drawArc(
                innerCircleEmbracingSquare,
                wheelSectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(),
                -wheelSectorClipAreaDescriptor.getSectorSweepAngleInDegree(),
                false,
                sectorLeftEdgeDrawingPaint
        );
    }

    private Path createSectorShapeClipPath() {
        if (!isSectorShapeClipPathInitialized) {
            sectorShapeClipPath.reset();

            final CoordinatesHolder bottomRight = wheelSectorClipAreaDescriptor.getBottomRightCorner();
            final CoordinatesHolder topLeft = wheelSectorClipAreaDescriptor.getTopLeftCorner();

            sectorShapeClipPath.moveTo(topLeft.getXAsFloat(), topLeft.getYAsFloat());
            sectorShapeClipPath.lineTo(bottomRight.getXAsFloat(), bottomRight.getYAsFloat());
            sectorShapeClipPath.arcTo(innerCircleEmbracingSquare, wheelSectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(), -wheelSectorClipAreaDescriptor.getSectorSweepAngleInDegree());
            sectorShapeClipPath.arcTo(outerCircleEmbracingSquare, -wheelSectorClipAreaDescriptor.getSectorTopEdgeAngleInDegree(), wheelSectorClipAreaDescriptor.getSectorSweepAngleInDegree());
            sectorShapeClipPath.lineTo(topLeft.getXAsFloat(), topLeft.getYAsFloat());

            sectorShapeClipPath.close();
            isSectorShapeClipPathInitialized = true;
        }
        return sectorShapeClipPath;
    }

    private Path createTrapezeShapeClipPath() {
        sectorShapeClipPath.reset();

        CoordinatesHolder bottomLeft = wheelSectorClipAreaDescriptor.getBottomLeftCorner();
        CoordinatesHolder bottomRight = wheelSectorClipAreaDescriptor.getBottomRightCorner();
        CoordinatesHolder topLeft = wheelSectorClipAreaDescriptor.getTopLeftCorner();
        CoordinatesHolder topRight = wheelSectorClipAreaDescriptor.getTopRightCorner();

        sectorShapeClipPath.moveTo(bottomLeft.getXAsFloat(), bottomLeft.getYAsFloat());
        sectorShapeClipPath.lineTo(bottomRight.getXAsFloat(), bottomRight.getYAsFloat());
        sectorShapeClipPath.lineTo(topRight.getXAsFloat(), topRight.getYAsFloat());
        sectorShapeClipPath.lineTo(topLeft.getXAsFloat(), topLeft.getYAsFloat());

        sectorShapeClipPath.close();

        return sectorShapeClipPath;
    }

}
