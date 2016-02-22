package com.sss.magicwheel.wheel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 19.02.2016.
 */
public class BottomWheelContainerRecyclerView extends AbstractWheelContainerRecyclerView {

    public interface OnBottomWheelSectorTapListener {
        void onRotateWheelByAngle(double rotationAngleInRad);
    }

    private OnBottomWheelSectorTapListener bottomWheelSectorTapListener;

    public BottomWheelContainerRecyclerView(Context context) {
        super(context);
    }

    public BottomWheelContainerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomWheelContainerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBottomWheelSectorTapListener(OnBottomWheelSectorTapListener bottomWheelSectorTapListener) {
        this.bottomWheelSectorTapListener = bottomWheelSectorTapListener;
    }

    @Override
    public void handleTapOnSectorView(View sectorViewToSelect) {
        bottomWheelSectorTapListener.onRotateWheelByAngle(computeWheelRotationForSector(sectorViewToSelect));
    }

    private double computeWheelRotationForSector(View sectorViewToSelect) {
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorViewToSelect);
        final double sectorViewBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);
        return getLayoutManager().getLayoutStartAngleInRad() - sectorViewBottomEdge;
    }
}
