package com.sss.magicwheel.manager.subwheel;

import android.support.v7.widget.RecyclerView;

import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
@Deprecated
public final class TopSubWheel /*extends BaseSubWheel*/ {

//    private static final String TAG = TopSubWheel.class.getCanonicalName();
//
//    protected TopSubWheel(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
//        super(wheelLayoutManager, computationHelper);
//    }
//
//    @Override
//    public void doInitialChildrenLayout(RecyclerView.Recycler recycler,
//                                        RecyclerView.State state,
//                                        int startLayoutFromAdapterPosition,
//                                        WheelOnInitialLayoutFinishingListener layoutFinishingListener) {
//
//        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
//        final double bottomLimitAngle = layoutEndAngleInRad - sectorAngleInRad;
//
//        double layoutAngle = layoutStartAngleInRad;
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
//    @Override
//    public String getUniqueMarker() {
//        return TAG;
//    }
//
//    @Override
//    public double getLayoutStartAngleInRad() {
//        return wheelConfig.getAngularRestrictions().getWheelLayoutStartAngleInRad();
//    }
//
//    @Override
//    public double getLayoutEndAngleInRad() {
//        return wheelConfig.getAngularRestrictions().getGapAreaTopEdgeAngleRestrictionInRad();
//    }
//
//    /*@Override
//    public View getChildClosestToTopEdge() {
//        return wheelLayoutManager.getChildAt(wheelLayoutManager.getChildCount() - 1);
//    }
//
//    @Override
//    public View getChildClosestToBottomEdge() {
//        throw new UnsupportedOperationException("Not implemented feature yet.");
//    }
//*/
//
//
//    // TODO: 15.12.2015 same code snippets - remove code duplication
////    @Override
////    private void rotateWheelBy(double rotationAngle, WheelRotationDirection rotationDirection) {
////        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
////            for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
////                View child = wheelLayoutManager.getChildAt(i);
////                final WheelOfFortuneLayoutManager.LayoutParams childLp =
////                        (WheelOfFortuneLayoutManager.LayoutParams) child.getLayoutParams();
////                childLp.anglePositionInRad += rotationAngle;
////                child.setLayoutParams(childLp);
////                wheelLayoutManager.alignBigWrapperViewByAngle(child, -childLp.anglePositionInRad);
////            }
////        } else {
////            throw new UnsupportedOperationException("Clockwise rotation has to be be done in rotator.");
////        }
////    }
//
}
