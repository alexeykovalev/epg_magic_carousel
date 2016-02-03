package com.sss.magicwheel.manager.layouter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelRotationDirection;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class TopSubWheelLayouter extends BaseSubWheelLayouter {

    protected TopSubWheelLayouter(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void doInitialChildrenLayout(RecyclerView.Recycler recycler,
                                        RecyclerView.State state,
                                        int startLayoutFromAdapterPosition,
                                        OnInitialLayoutFinishingListener layoutFinishingListener) {

        final double sectorAngleInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad();
        final double bottomLimitAngle = wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad();

        double layoutAngle = computationHelper.getWheelLayoutStartAngleInRad();
        int childPos = startLayoutFromAdapterPosition;
        while (layoutAngle > bottomLimitAngle && childPos < state.getItemCount()) {
            setupSectorForPosition(recycler, childPos, layoutAngle, true);
            layoutAngle -= sectorAngleInRad;
            childPos++;
        }
    }


    // TODO: 15.12.2015 same code snippets - remove code duplication
    @Override
    public void rotateSubWheel(double rotationAngle, WheelRotationDirection rotationDirection) {
        if (rotationDirection == WheelRotationDirection.Anticlockwise) {
            for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
                View child = wheelLayoutManager.getChildAt(i);
                final WheelOfFortuneLayoutManager.LayoutParams childLp =
                        (WheelOfFortuneLayoutManager.LayoutParams) child.getLayoutParams();
                childLp.anglePositionInRad += rotationAngle;
                child.setLayoutParams(childLp);
                alignBigWrapperViewByAngle(child, -childLp.anglePositionInRad);
            }
        } else {
            for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
                View child = wheelLayoutManager.getChildAt(i);
                final WheelOfFortuneLayoutManager.LayoutParams childLp =
                        (WheelOfFortuneLayoutManager.LayoutParams) child.getLayoutParams();
                childLp.anglePositionInRad -= rotationAngle;
                child.setLayoutParams(childLp);
                alignBigWrapperViewByAngle(child, -childLp.anglePositionInRad);
            }
        }
    }

}
