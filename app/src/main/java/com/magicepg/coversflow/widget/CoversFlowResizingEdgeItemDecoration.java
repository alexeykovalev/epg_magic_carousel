package com.magicepg.coversflow.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

/**
 * Only for DEBUG purposes.
 * Draws red line edge where covers in covers flow container start resizing.
 *
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
final class CoversFlowResizingEdgeItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint edgesPaint;
    private final float resizingEdgePosition;

    CoversFlowResizingEdgeItemDecoration(float resizingEdgePosition) {
        this.resizingEdgePosition = resizingEdgePosition;
        this.edgesPaint = createEdgesPaint();
    }

    private static Paint createEdgesPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(7);
        return paint;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        drawLeftEdge(canvas);
    }

    private void drawLeftEdge(Canvas canvas) {
        final int height = canvas.getHeight();
        canvas.drawLine(resizingEdgePosition, 0, resizingEdgePosition, height, edgesPaint);
    }

}
