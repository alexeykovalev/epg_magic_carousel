package com.sss.magicwheel.manager.rotator;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;
import com.sss.magicwheel.manager.WheelComputationHelper;

import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class ClockwiseWheelRotator extends AbstractWheelRotator {

    private static final String TAG = ClockwiseWheelRotator.class.getCanonicalName();

    public ClockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void rotateWheel(double rotationAngleInRad, RecyclerView.Recycler recycler, RecyclerView.State state) {

//        logChildren(subWheelToRotate.getChildren());

        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad -= rotationAngleInRad;
            sectorView.setLayoutParams(sectorViewLp);
            wheelLayoutManager.alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }

        recycleAndAddSectors(recycler, state);
    }

    private void logChildren(List<View> children) {
        for (View sectorView : children) {
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final String title = AbstractWheelLayoutManager.getBigWrapperTitle(sectorView);
            double topEdgeSectorInDegree = WheelComputationHelper.radToDegree(computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad));
            double bottomEdgeSectorInDegree = WheelComputationHelper.radToDegree(computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad));
            Log.e(TAG,
                    "title [" + title + "], " +
                    "topEdgeSectorInDegree [" + topEdgeSectorInDegree + "], " +
                    "bottomEdgeSectorInDegree [" + bottomEdgeSectorInDegree + "]"
            );
        }
    }

    @Override
    protected void recycleAndAddSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        recycleSectorsFromBottomIfNeeded(recycler);
        addSectorsToTopIfNeeded(recycler, state);
    }

    private void recycleSectorsFromBottomIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = wheelLayoutManager.getChildCount() - 1; i >= 0; i--) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewTopEdgeAngularPosInRad < wheelLayoutManager.getLayoutEndAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
//                Log.e(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    private void addSectorsToTopIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToStartSectorView = wheelLayoutManager.getChildClosestToLayoutStartEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToStartSectorView);

        Log.e(AbstractWheelLayoutManager.TAG, "addSectorsToTopIfNeeded() " +
                "closestToStartSectorView [" + AbstractWheelLayoutManager.getBigWrapperTitle(closestToStartSectorView) + "]");

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad + sectorAngleInRad;
        double newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = wheelLayoutManager.getPosition(closestToStartSectorView) - 1;
        int alreadyLayoutedChildrenCount = 0;

        while (newSectorViewBottomEdgeAngularPosInRad < wheelLayoutManager.getLayoutStartAngleInRad()
                && alreadyLayoutedChildrenCount < state.getItemCount()) {
//            Log.i(TAG, "addSectorsToTopIfNeeded() " +
//                            "newSectorViewLayoutAngle [" + WheelComputationHelper.radToDegree(newSectorViewLayoutAngle) + "], " +
//                            "nextChildPos [" + nextChildPos + "]"
//            );
            Log.e(AbstractWheelLayoutManager.TAG, "addSectorsToTopIfNeeded()");
            wheelLayoutManager.setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, false);
            newSectorViewLayoutAngle += sectorAngleInRad;
            newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos--;
            alreadyLayoutedChildrenCount++;
        }
    }
}
