package com.sss.magicwheel.wheel.rotator;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;
import com.sss.magicwheel.wheel.WheelComputationHelper;

/**
 * @author Alexey Kovalev
 * @since 03.02.2016.
 */
@Deprecated
public final class AnticlockwiseWheelRotator extends AbstractWheelRotator {

    public AnticlockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void rotateWheelBy(double rotationAngleInRad) {
//        final double halfSectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2;
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad += rotationAngleInRad;
            // we store top sector angular position
//            sectorViewLp.anglePositionInRad += halfSectorAngleInRad;
            wheelLayoutManager.alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }
    }

    @Override
    public void recycleSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        recycleSectorsFromTopIfNeeded(recycler);
    }

    @Override
    public void addSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        addSectorsToBottomIfNeeded(recycler, state);
    }

    private void recycleSectorsFromTopIfNeeded(RecyclerView.Recycler recycler) {
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            final double sectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad);

            if (sectorViewBottomEdgeAngularPosInRad > wheelLayoutManager.getLayoutStartAngleInRad()) {
                wheelLayoutManager.removeAndRecycleViewAt(i, recycler);
//                Log.i(TAG, "Recycle view at index [" + i + "]");
            }
        }
    }

    private void addSectorsToBottomIfNeeded(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToEndSectorView = wheelLayoutManager.getChildClosestToLayoutEndEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToEndSectorView);

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();
        final double bottomEndLayoutAngleInRad = wheelLayoutManager.getLayoutEndAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad - sectorAngleInRad;
        double newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = wheelLayoutManager.getPosition(closestToEndSectorView) + 1;
        int alreadyLayoutedChildrenCount = 0;

//        Log.e("TAG", "addSectorsToBottomIfNeeded() " +
//                "newSectorViewTopEdgeAngularPosInRad [" + WheelComputationHelper.radToDegree(newSectorViewTopEdgeAngularPosInRad) + "], " +
//                "bottomEndLayoutAngleInRad [" + WheelComputationHelper.radToDegree(bottomEndLayoutAngleInRad) + "]");

        while (newSectorViewTopEdgeAngularPosInRad > bottomEndLayoutAngleInRad && alreadyLayoutedChildrenCount < state.getItemCount()) {

//            Log.e("TAG", "addSectorsToBottomIfNeeded() " +
//                    "newSectorViewTopEdgeAngularPosInRad [" + WheelComputationHelper.radToDegree(newSectorViewTopEdgeAngularPosInRad) + "], " +
//                    "bottomEndLayoutAngleInRad [" + WheelComputationHelper.radToDegree(bottomEndLayoutAngleInRad) + "]");

            wheelLayoutManager.setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, true);
            newSectorViewLayoutAngle -= sectorAngleInRad;
            newSectorViewTopEdgeAngularPosInRad = computationHelper.getSectorAngleTopEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos++;
            alreadyLayoutedChildrenCount++;
        }
    }
}
