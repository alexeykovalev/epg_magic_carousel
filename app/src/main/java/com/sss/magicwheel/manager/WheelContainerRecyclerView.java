package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * @author Alexey Kovalev
 * @since 02.02.2016.
 */
public final class WheelContainerRecyclerView extends RecyclerView {

    private RectF gapClipRectInRvCoords;
    private Path gapPath;

    public WheelContainerRecyclerView(Context context) {
        this(context, null);
    }

    public WheelContainerRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelContainerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private RectF createGapClipRect() {
        WheelComputationHelper computationHelper = WheelComputationHelper.getInstance();
        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getWheelConfig().getCircleCenterRelToRecyclerView(),
                new RectF(
                        0,
                        150,
                        computationHelper.getWheelConfig().getOuterRadius() + 150,
                        -150
                )
        );
    }

    private Path createGapClipPath(RectF gapClipRect) {
        final Path res = new Path();

        /*res.moveTo(gapClipRect.left, gapClipRect.top);
        res.lineTo(gapClipRect.right, gapClipRect.top);
        res.lineTo(gapClipRect.right, gapClipRect.bottom);
        res.lineTo(gapClipRect.left, gapClipRect.bottom);
        res.lineTo(gapClipRect.left, gapClipRect.top);
        res.close();*/

        res.addRect(gapClipRect, Path.Direction.CW);

        return res;
    }


    @Override
    public void onDraw(Canvas canvas) {
        if (!WheelComputationHelper.isInitialized()) {
            super.onDraw(canvas);
            return;
        }

        init();
        canvas.clipPath(gapPath);
        super.onDraw(canvas);
    }

    private void init() {
        this.gapClipRectInRvCoords = createGapClipRect();
        this.gapPath = createGapClipPath(gapClipRectInRvCoords);
    }
}
