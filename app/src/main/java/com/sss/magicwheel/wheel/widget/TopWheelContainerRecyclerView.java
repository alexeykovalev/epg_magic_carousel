package com.sss.magicwheel.wheel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.wheel.entity.WheelRotationDirection;
import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 19.02.2016.
 */
public class TopWheelContainerRecyclerView extends AbstractWheelContainerRecyclerView {

    public TopWheelContainerRecyclerView(Context context) {
        super(context);
    }

    public TopWheelContainerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopWheelContainerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void handleTapOnSectorView(View sectorViewToSelect) {
        smoothRotateWheelByAngleInRad(computeWheelRotationForTapOnSector(sectorViewToSelect), WheelRotationDirection.Clockwise);
    }

    private double computeWheelRotationForTapOnSector(View sectorViewToSelect) {
        AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorViewToSelect);
        final double sectorAngleTopEdgeInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad);
        return sectorAngleTopEdgeInRad - getLayoutManager().getLayoutEndAngleInRad();
    }
}
