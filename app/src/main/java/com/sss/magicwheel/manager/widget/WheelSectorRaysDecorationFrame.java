package com.sss.magicwheel.manager.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.sss.magicwheel.R;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 12.02.2016.
 */
public final class WheelSectorRaysDecorationFrame extends FrameLayout {

    private static final int DEFAULT_RAY_WIDTH = 700;
    private static final int DEFAULT_RAY_HEIGHT = 10;

    // TODO: 28.01.2016 ray with default draw does not fit exactly at sector's edge. This magic constant compensates this divergence.
    private static final int MAGIC_CONSTANT_FOR_RAY_ALIGNMENT = 12;


    private final Drawable rayDrawable;
    private final WheelComputationHelper computationHelper;
    private WheelContainerRecyclerView topWheelContainerView;
    private WheelContainerRecyclerView bottomWheelContainerView;

    public WheelSectorRaysDecorationFrame(Context context) {
        this(context, null);
    }

    public WheelSectorRaysDecorationFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelSectorRaysDecorationFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.computationHelper = WheelComputationHelper.getInstance();
        this.rayDrawable = context.getResources().getDrawable(R.drawable.wheel_sector_ray_drawable);
    }

    public void setWheelContainerViews(WheelContainerRecyclerView topWheelContainerView, WheelContainerRecyclerView bottomWheelContainer) {
        this.topWheelContainerView = topWheelContainerView;
        this.bottomWheelContainerView = bottomWheelContainer;

        topWheelContainerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                invalidate();
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

//        WheelComputationHelper computationHelper = WheelComputationHelper.getInstance();
//
//        Paint paint = new Paint();
//        paint.setColor(Color.GREEN);
//        paint.setStrokeWidth(10);
//        paint.setAntiAlias(true);
//
//        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), 0, paint);

        super.dispatchDraw(canvas);


        /*if (topWheelContainerView != null) {
            for (int i = 0; i < topWheelContainerView.getChildCount(); i++) {
                final View sectorView = topWheelContainerView.getChildAt(i);
                drawSectorTopEdgeRay(sectorView, canvas);
            }
        }*/

        drawRaysForBottomWheel(canvas);

//        Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        myPaint.setStyle(Paint.Style.STROKE);
//        int strokeWidth = 20;  // or whatever
//        myPaint.setStrokeWidth(strokeWidth);
//        myPaint.setColor(0xffff0000);   //color.RED
//        float radius= computationHelper.getWheelConfig().getInnerRadius();
//
//
////        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), 0, paint);
//        canvas.drawCircle(0, getMeasuredHeight() / 2, radius, myPaint);
    }

    private void drawSectorTopEdgeRay(View sectorView, Canvas canvas) {
        final double sectorAnglePositionInRad = getSectorAnglePositionInRad(sectorView);
        final PointF sectorReferencePoint = getSectorTopLeftCornerPos(sectorView);
        final float sectorAnglePositionInDegree = (float) WheelComputationHelper.radToDegree(sectorAnglePositionInRad);
        final float sectorHalfAngleInDegree = (float) WheelComputationHelper.radToDegree(
                computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2
        );

        final float rotateRayByAngle = sectorAnglePositionInDegree + sectorHalfAngleInDegree;

        canvas.save();
        // negative rotation angle due to anticlockwise rotation
        canvas.rotate(-rotateRayByAngle, sectorReferencePoint.x, sectorReferencePoint.y);
        Drawable topEdgeRayDrawable = getRayDrawable(sectorReferencePoint);
        topEdgeRayDrawable.draw(canvas);
        canvas.restore();
    }

    private Drawable getRayDrawable(PointF rayStartPos) {
        final float startRayPosX = rayStartPos.x;
        final float startRayPosY = rayStartPos.y - MAGIC_CONSTANT_FOR_RAY_ALIGNMENT;
        final int rayHeight = rayDrawable.getIntrinsicHeight();
        rayDrawable.setBounds(
                (int) startRayPosX,
                (int) startRayPosY,
                (int) (startRayPosX + DEFAULT_RAY_WIDTH),
                (int) (startRayPosY + rayHeight)
        );
        return rayDrawable;
    }

    private PointF getSectorTopLeftCornerPos(View sectorView) {
        final double topLeftSectorCornerAnglePosInRad = getSectorTopEdgeAnglePositionInRad(sectorView);
        final int innerRadius = computationHelper.getWheelConfig().getInnerRadius();

        final float refPointXPosInWheelCoordSystem = (float) (innerRadius * Math.cos(topLeftSectorCornerAnglePosInRad));
        final float refPointYPosInWheelCoordSystem = (float) (innerRadius * Math.sin(topLeftSectorCornerAnglePosInRad));
        final PointF topLeftCornerPosInWheelCoordsSystem = new PointF(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(topLeftCornerPosInWheelCoordsSystem);
    }


    private double getSectorAnglePositionInRad(View sectorView) {
        final AbstractWheelLayoutManager.LayoutParams childLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
        return childLp.anglePositionInRad;
    }

    private double getSectorTopEdgeAnglePositionInRad(View sectorView) {
        final double halfSectorAngle = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2;
        return getSectorAnglePositionInRad(sectorView) + halfSectorAngle;
    }



    private void drawRaysForBottomWheel(Canvas canvas) {
        if (bottomWheelContainerView != null) {
            for (int i = 0; i < bottomWheelContainerView.getChildCount(); i++) {
                final View sectorView = bottomWheelContainerView.getChildAt(i);
                drawRayForBottomWheelSector(sectorView, canvas);
//                drawSectorTopEdgeRay(sectorView, canvas);
            }
        }
    }

    private void drawRayForBottomWheelSector(View sectorView, Canvas canvas) {

        final double sectorAnglePositionInRad = getSectorAnglePositionInRad(sectorView);
        final float sectorAnglePositionInDegree = (float) WheelComputationHelper.radToDegree(sectorAnglePositionInRad);

        if (sectorAnglePositionInDegree <= getBottomWheelContainerRotationInDegree()) {

            Log.e("TAG", "sectorAnglePositionInDegree [" + sectorAnglePositionInDegree + "], " +
                    "getBottomWheelContainerRotationInDegree() [" + getBottomWheelContainerRotationInDegree() + "]");

            final PointF sectorReferencePoint = getSectorTopLeftCornerPos(sectorView);
            final float sectorHalfAngleInDegree = (float) WheelComputationHelper.radToDegree(
                    computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2
            );

            final float rotateRayByAngle = sectorAnglePositionInDegree + sectorHalfAngleInDegree;

            canvas.save();
            // negative rotation angle due to anticlockwise rotation
            canvas.rotate(-rotateRayByAngle, sectorReferencePoint.x, sectorReferencePoint.y);
            Drawable topEdgeRayDrawable = getRayDrawable(sectorReferencePoint);
            topEdgeRayDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private double getBottomWheelContainerRotationInDegree() {
        return bottomWheelContainerView.getRotation();
    }

}
