package com.sss.magicwheel.manager.rotator;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;
import com.sss.magicwheel.manager.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class AnticlockwiseWheelRotator extends AbstractWheelRotator {

    public AnticlockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void rotateWheel(double rotationAngleInRad, RecyclerView.Recycler recycler, RecyclerView.State state) {
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }

    @Override
    protected void recycleAndAddSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        recycleSectorsFromTopIfNeeded();
        addSectorsToBottomIfNeeded(recycler, state);
    }

    private void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        final View lastChild = wheelLayoutManager.getChildClosestToBottom();
//        final WheelOfFortuneLayoutManager.LayoutParams lastChildLp = (WheelOfFortuneLayoutManager.LayoutParams) lastChild.getLayoutParams();
//
//        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
//        final double bottomLimitAngle = wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();
//
//        double layoutAngle = computationHelper.getSectorAngleBottomEdgeInRad(lastChildLp.anglePositionInRad);
//        int childPos = wheelLayoutManager.getPosition(lastChild) + 1;
//        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
////            Log.e(TAG, "addSectorsToBottomIfNeeded() " +
////                    "layoutAngle [" + WheelUtils.radToDegree(layoutAngle) + "], " +
////                    "childPos [" + childPos + "]"
////            );
//            wheelLayoutManager.setupSectorForPosition(recycler, childPos, layoutAngle, true);
//            layoutAngle -= sectorAngleInRad;
//            childPos++;
//        }
    }

//    private void recycleSectorsFromTopIfNeeded(BaseSubWheel subWheel, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
//            final WheelOfFortuneLayoutManager.LayoutParams childLp
//                    = (WheelOfFortuneLayoutManager.LayoutParams) wheelLayoutManager.getChildAt(i).getLayoutParams();
//            if (childLp.anglePositionInRad > computationHelper.getWheelLayoutStartAngleInRad()) {
//                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
////                Log.i(TAG, "Recycle view at index [" + i + "]");
//            }
//        }
//    }
}
