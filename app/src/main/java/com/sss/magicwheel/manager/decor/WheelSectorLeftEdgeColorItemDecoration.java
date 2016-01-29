package com.sss.magicwheel.manager.decor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 29.01.2016.
 */
public final class WheelSectorLeftEdgeColorItemDecoration extends WheelBaseItemDecoration {

    private  static final int SECTOR_EDGE_RING_THICKNESS = 20;

    private final RectF innerCircleEmbracingSquare;
    private final Paint sectorEdgeDrawingPaint;

    public WheelSectorLeftEdgeColorItemDecoration(Context context) {
        super(context);
        this.sectorEdgeDrawingPaint = createSectorEdgeDrawingPaint();
        this.innerCircleEmbracingSquare = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getCircleConfig().getCircleCenterRelToRecyclerView(),
                computationHelper.getInnerCircleEmbracingSquareInCircleCoordsSystem()
        );
    }

    private Paint createSectorEdgeDrawingPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(SECTOR_EDGE_RING_THICKNESS);
        return paint;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView wheelView, RecyclerView.State state) {
        final WheelAdapter wheelAdapter = getWheelAdapter(wheelView);
        for (int i = 0; i < wheelView.getChildCount(); i++) {
            final View sectorView = wheelView.getChildAt(i);
            final int sectorViewAdapterPosition = wheelView.getChildAdapterPosition(sectorView);
            drawSectorSideColor(canvas, sectorView, wheelAdapter.getDataItemByPosition(sectorViewAdapterPosition));
        }
    }

    private void drawSectorSideColor(Canvas canvas, View sectorView, WheelDataItem sectorDataItem) {
        final int sectorTopEdgeAnglePositionInDegree =
                (int) WheelComputationHelper.radToDegree(getSectorTopEdgeAnglePositionInRad(sectorView));

        final int sectorAngleInDegree = (int) WheelComputationHelper.radToDegree(
                computationHelper.getCircleConfig().getAngularRestrictions().getSectorAngleInRad()
        );

        sectorEdgeDrawingPaint.setColor(sectorDataItem.getSectorLeftEdgeColor());
//        sectorEdgeDrawingPaint.setColor(Color.GREEN);
        canvas.drawArc(innerCircleEmbracingSquare,
                sectorTopEdgeAnglePositionInDegree,
                sectorAngleInDegree,
                false, sectorEdgeDrawingPaint
        );
    }

}
