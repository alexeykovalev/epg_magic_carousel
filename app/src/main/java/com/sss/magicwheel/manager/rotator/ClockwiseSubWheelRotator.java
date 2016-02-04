package com.sss.magicwheel.manager.rotator;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;
import com.sss.magicwheel.manager.subwheel.BaseSubWheel;

import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
public final class ClockwiseSubWheelRotator extends AbstractSubWheelRotator {

    private static final String TAG = ClockwiseSubWheelRotator.class.getCanonicalName();

    protected ClockwiseSubWheelRotator(WheelOfFortuneLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void rotateSubWheel(BaseSubWheel subWheelToRotate, double rotationAngleInRad,
                               RecyclerView.Recycler recycler, RecyclerView.State state) {
        logChildren(subWheelToRotate.getChildren());
        for (View sectorView : subWheelToRotate.getChildren()) {
            final WheelOfFortuneLayoutManager.LayoutParams sectorViewLp = WheelOfFortuneLayoutManager.getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad -= rotationAngleInRad;
            sectorView.setLayoutParams(sectorViewLp);
            wheelLayoutManager.alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }

        recycleAndAddSectors(subWheelToRotate, recycler, state);
    }

    private void logChildren(List<View> children) {
        for (View sectorView : children) {
            final WheelOfFortuneLayoutManager.LayoutParams sectorViewLp = WheelOfFortuneLayoutManager.getChildLayoutParams(sectorView);
            final String title = WheelOfFortuneLayoutManager.getBigWrapperTitle(sectorView);
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
    protected void recycleAndAddSectors(BaseSubWheel subWheel, RecyclerView.Recycler recycler, RecyclerView.State state) {
        recycleSectorsFromBottomIfNeeded(subWheel, recycler);
        addSectorsToTopIfNeeded(subWheel, recycler, state);
    }

    private void recycleSectorsFromBottomIfNeeded(BaseSubWheel subWheel, RecyclerView.Recycler recycler) {
        final List<View> subWheelChildren = subWheel.getChildren();
        for (int i = subWheelChildren.size() - 1; i >= 0; i--) {
            final View sectorView = subWheelChildren.get(i);
            final WheelOfFortuneLayoutManager.LayoutParams sectorViewLp = WheelOfFortuneLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewTopEdgeAngularPosInRad < subWheel.getLayoutEndAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
//                Log.e(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    private void addSectorsToTopIfNeeded(BaseSubWheel subWheel, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToStartSectorView = subWheel.getChildClosestToLayoutStartEdge();
        final WheelOfFortuneLayoutManager.LayoutParams sectorViewLp = WheelOfFortuneLayoutManager.getChildLayoutParams(closestToStartSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad + sectorAngleInRad;
        double newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
        int childPos = wheelLayoutManager.getPosition(closestToStartSectorView) - 1;

        while (newSectorViewBottomEdgeAngularPosInRad < subWheel.getLayoutStartAngleInRad() && childPos >= 0) {
//            Log.i(TAG, "addSectorsToTopIfNeeded() " +
//                            "newSectorViewLayoutAngle [" + WheelComputationHelper.radToDegree(newSectorViewLayoutAngle) + "], " +
//                            "childPos [" + childPos + "]"
//            );
            wheelLayoutManager.setupSectorForPosition(subWheel, recycler, childPos, newSectorViewLayoutAngle, false);
            newSectorViewLayoutAngle += sectorAngleInRad;
            newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
            childPos--;
        }
    }
}
