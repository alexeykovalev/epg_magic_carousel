package com.sss.magicwheel.coversflow;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public class HorizontalCoversFlowEdgeDecorator extends RecyclerView.ItemDecoration {

    private final Paint edgesPaint;

    public HorizontalCoversFlowEdgeDecorator() {
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
        final float startX = CoversFlowListMeasurements.getInstance().getResizingEdgePosition();
        canvas.drawLine(startX, 0, startX, height, edgesPaint);
    }

}
