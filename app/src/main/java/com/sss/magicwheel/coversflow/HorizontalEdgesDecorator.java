package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.App;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public class HorizontalEdgesDecorator extends RecyclerView.ItemDecoration {

    public static final int START_LEFT_EDGE_DRAW_FROM_IN_DP = 335;
    private static final int EDGES_WIDTH_IN_DP = 150;

    private final Context context;
    private final Paint edgesPaint;

    public HorizontalEdgesDecorator(Context context) {
        this.context = context;
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
//        drawRightEdge(canvas);
    }

    private void drawLeftEdge(Canvas canvas) {
        final int height = canvas.getHeight();
        final float startX = App.dpToPixels(START_LEFT_EDGE_DRAW_FROM_IN_DP);
        canvas.drawLine(startX, 0, startX, height, edgesPaint);
    }

    private void drawRightEdge(Canvas canvas) {
        final int height = canvas.getHeight();
        final float startX = App.dpToPixels(START_LEFT_EDGE_DRAW_FROM_IN_DP + EDGES_WIDTH_IN_DP);
        canvas.drawLine(startX, 0, startX, height, edgesPaint);
    }

}
