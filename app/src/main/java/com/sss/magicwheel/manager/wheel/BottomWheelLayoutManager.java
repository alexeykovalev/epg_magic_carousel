package com.sss.magicwheel.manager.wheel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.entity.WheelRotationDirection;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public final class BottomWheelLayoutManager extends AbstractWheelLayoutManager {

    // TODO: 05.02.2016 for testing
    @Deprecated
    private static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    public BottomWheelLayoutManager(Context context, WheelComputationHelper computationHelper, WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
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
            initialLayoutFinishingListener.onInitialLayoutFinished(childPos - 1);
        }
    }

    @Override
    protected double computeLayoutStartAngleInRad() {
        return wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad();
//                + wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 2;
    }

    @Override
    protected double computeLayoutEndAngleInRad() {
        return wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();
    }

//    @Override
//    protected void rotateWheel(double rotationAngleInRad, WheelRotationDirection rotationDirection,
//                               RecyclerView.Recycler recycler, RecyclerView.State state) {
//
//        super.rotateWheel(rotationAngleInRad, rotationDirection, recycler, state);
//
//        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
//            rotateWheelAnticlockwise(rotationAngleInRad, recycler, state);
//        } else {
//            throw new UnsupportedOperationException("Not implemented feature yet.");
//        }
//    }

    private void rotateWheelAnticlockwise(double rotationAngleInRad, RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < getChildCount(); i++) {
            final View sectorView = getChildAt(i);
            final LayoutParams sectorViewLp = getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad += rotationAngleInRad;
            sectorView.setLayoutParams(sectorViewLp);
            alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }

        recycleSectorsFromTopIfNeeded(recycler, state);
        addSectorsToBottomIfNeeded(recycler, state);
    }

    /**
     * When sectorView's bottom edge goes outside
     */
    private void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < getChildCount(); i++) {
            final View sectorView = getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewBottomEdgeAngularPosInRad > computationHelper.getWheelLayoutStartAngleInRad()) {
                removeAndRecycleViewAt(i, recycler);
//                Log.i(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    private void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToEndSectorView = getChildClosestToLayoutEndEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToEndSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
        final double bottomEndLayoutAngleInRad = getLayoutEndAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad - sectorAngleInRad;
        double newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = getPosition(closestToEndSectorView) + 1;
        int alreadyLayoutedChildrenCount = 0;

//        Log.e("TAG", "addSectorsToBottomIfNeeded() " +
//                "newSectorViewTopEdgeAngularPosInRad [" + WheelComputationHelper.radToDegree(newSectorViewTopEdgeAngularPosInRad) + "], " +
//                "bottomEndLayoutAngleInRad [" + WheelComputationHelper.radToDegree(bottomEndLayoutAngleInRad) + "]");

        while (newSectorViewTopEdgeAngularPosInRad > bottomEndLayoutAngleInRad && alreadyLayoutedChildrenCount < state.getItemCount()) {

//            Log.e("TAG", "addSectorsToBottomIfNeeded() " +
//                    "newSectorViewTopEdgeAngularPosInRad [" + WheelComputationHelper.radToDegree(newSectorViewTopEdgeAngularPosInRad) + "], " +
//                    "bottomEndLayoutAngleInRad [" + WheelComputationHelper.radToDegree(bottomEndLayoutAngleInRad) + "]");

            setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, true);
            newSectorViewLayoutAngle -= sectorAngleInRad;
            newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos++;
            alreadyLayoutedChildrenCount++;
        }
    }


}
