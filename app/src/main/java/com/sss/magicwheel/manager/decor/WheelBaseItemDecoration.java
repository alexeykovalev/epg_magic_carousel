package com.sss.magicwheel.manager.decor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.layouter.WheelOfFortuneLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 29.01.2016.
 */
public abstract class WheelBaseItemDecoration extends RecyclerView.ItemDecoration {

    protected final Context context;
    protected final WheelComputationHelper computationHelper;

    protected WheelBaseItemDecoration(Context context) {
        this.context = context;
        this.computationHelper = WheelComputationHelper.getInstance();
    }

    @Override
    public abstract void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state);

    @Override
    public abstract void onDrawOver(Canvas canvas, RecyclerView wheelView, RecyclerView.State state);

    // TODO: 28.01.2016 make Point instance reusing
    @Deprecated
    protected final PointF getSectorReferencePoint(View sectorView) {
        final WheelOfFortuneLayoutManager.LayoutParams childLp = (WheelOfFortuneLayoutManager.LayoutParams) sectorView.getLayoutParams();
        final double sectorAnglePositionInRad = childLp.anglePositionInRad;

        final int innerRadius = computationHelper.getWheelConfig().getInnerRadius();
        final float refPointXPosInWheelCoordSystem = (float) (innerRadius * Math.cos(sectorAnglePositionInRad));
        final float refPointYPosInWheelCoordSystem = (float) (innerRadius * Math.sin(sectorAnglePositionInRad));
        final PointF sectorRefPointPosInWheelCoordSystem = new PointF(refPointXPosInWheelCoordSystem, refPointYPosInWheelCoordSystem);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(sectorRefPointPosInWheelCoordSystem);
    }

    protected final WheelAdapter getWheelAdapter(RecyclerView wheelView) {
        return (WheelAdapter) wheelView.getAdapter();
    }

    protected final double getSectorAnglePositionInRad(View sectorView) {
        final WheelOfFortuneLayoutManager.LayoutParams childLp =
                (WheelOfFortuneLayoutManager.LayoutParams) sectorView.getLayoutParams();
        return childLp.anglePositionInRad;
    }

    protected final double getSectorTopEdgeAnglePositionInRad(View sectorView) {
        final double halfSectorAngle = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2;
        return getSectorAnglePositionInRad(sectorView) + halfSectorAngle;
    }

    protected final double getSectorBottomEdgeAnglePositionInRad(View sectorView) {
        final double halfSectorAngle = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2;
        return getSectorAnglePositionInRad(sectorView) - halfSectorAngle;
    }
}
