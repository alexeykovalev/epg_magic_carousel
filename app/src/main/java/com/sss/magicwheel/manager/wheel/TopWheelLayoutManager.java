package com.sss.magicwheel.manager.wheel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class TopWheelLayoutManager extends AbstractWheelLayoutManager {

    /**
     * In order to make wheel infinite we have to set virtual position as start layout position.
     */
    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    public TopWheelLayoutManager(Context context, WheelComputationHelper computationHelper, WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        super(computationHelper, initialLayoutFinishingListener);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        // We have nothing to show for an empty data set but clear any existing views
        int itemCount = getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        removeAndRecycleAllViews(recycler);

        if (getStartLayoutFromAdapterPosition() == NOT_DEFINED_ADAPTER_POSITION) {
            return;
        }

        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
        final double halfSectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;
        final double bottomLimitAngle = getLayoutEndAngleInRad();

        double layoutAngle = getLayoutStartAngleInRad() + halfSectorAngleInRad;
        int childPos = getStartLayoutFromAdapterPosition();
        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }

        if (initialLayoutFinishingListener != null) {
            initialLayoutFinishingListener.onInitialLayoutFinished(childPos);
        }
    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad();
//                - wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;
    }

    @Override
    protected int getStartLayoutFromAdapterPosition() {
        return START_LAYOUT_FROM_ADAPTER_POSITION;
    }
}
