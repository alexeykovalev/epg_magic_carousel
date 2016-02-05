package com.sss.magicwheel.manager.subwheel;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
@Deprecated
public final class BottomSubWheel /*extends BaseSubWheel*/ {

//    private static final String TAG = BottomSubWheel.class.getCanonicalName();
//
//    protected BottomSubWheel(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
//        super(wheelLayoutManager, computationHelper);
//    }
//
//    @Override
//    public void doInitialChildrenLayout(RecyclerView.Recycler recycler,
//                                              RecyclerView.State state,
//                                              int startLayoutFromAdapterPosition,
//                                              OnInitialLayoutFinishingListener layoutFinishingListener) {
//
//        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
//        final double bottomLimitAngle = layoutEndAngleInRad - sectorAngleInRad;
//
//        double layoutAngle = layoutStartAngleInRad + sectorAngleInRad / 2;
//        int childPos = startLayoutFromAdapterPosition;
//        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
//            wheelLayoutManager.setupSectorForPosition(this, recycler, childPos, layoutAngle, true);
//            layoutAngle -= sectorAngleInRad;
//            childPos++;
//        }
//
//        if (layoutFinishingListener != null) {
//            layoutFinishingListener.onInitialLayoutFinished(childPos - 1);
//        }
//    }
//
//
//    @Override
//    public String getUniqueMarker() {
//        return TAG;
//    }
//
//    @Override
//    public double getLayoutStartAngleInRad() {
//        return wheelConfig.getAngularRestrictions().getGapAreaBottomEdgeAngleRestrictionInRad();
//    }
//
//    @Override
//    public double getLayoutEndAngleInRad() {
//        return wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();
//    }
//
//    /*@Override
//    public View getChildClosestToBottomEdge() {
//        return wheelLayoutManager.getChildAt(0);
//    }
//
//    @Override
//    public View getChildClosestToTopEdge() {
//        throw new UnsupportedOperationException("Not implemented feature yet.");
//    }*/
}
