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
public final class ClockwiseWheelRotator extends AbstractWheelRotator {

    private static final String TAG = ClockwiseWheelRotator.class.getCanonicalName();

    public ClockwiseWheelRotator(AbstractWheelLayoutManager wheelLayoutManager, WheelComputationHelper computationHelper) {
        super(wheelLayoutManager, computationHelper);
    }

    @Override
    public void rotateWheelBy(double rotationAngleInRad) {
//        final double halfSectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad() / 2;
        for (int i = 0; i < wheelLayoutManager.getChildCount(); i++) {
            final View sectorView = wheelLayoutManager.getChildAt(i);
            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
            sectorViewLp.anglePositionInRad -= rotationAngleInRad;
            // we store top sector angular position
//            sectorViewLp.anglePositionInRad += halfSectorAngleInRad;
            wheelLayoutManager.alignBigWrapperViewByAngle(sectorView, -sectorViewLp.anglePositionInRad);
        }
    }

//    private void logChildren(List<View> children) {
//        for (View sectorView : children) {
//            final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(sectorView);
//            final String title = AbstractWheelLayoutManager.getBigWrapperTitle(sectorView);
//            double topEdgeSectorInDegree = WheelComputationHelper.radToDegree(computationHelper.getSectorAngleTopEdgeInRad(sectorViewLp.anglePositionInRad));
//            double bottomEdgeSectorInDegree = WheelComputationHelper.radToDegree(computationHelper.getSectorAngleBottomEdgeInRad(sectorViewLp.anglePositionInRad));
//            Log.e(TAG,
//                    "title [" + title + "], " +
//                    "topEdgeSectorInDegree [" + topEdgeSectorInDegree + "], " +
//                    "bottomEdgeSectorInDegree [" + bottomEdgeSectorInDegree + "]"
//            );
//        }
//    }

    @Override
    public void recycleSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        recycleSectorsFromLayoutEndEdge(recycler);
    }

    @Override
    public void addSectors(RecyclerView.Recycler recycler, RecyclerView.State state) {
        addSectorsToLayoutStartEdge(recycler, state);
    }

    /**
     * When sectorView's top edge goes outside layoutEndAngle then recycle this sector.
     */
    private void recycleSectorsFromLayoutEndEdge(RecyclerView.Recycler recycler) {
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

    /**
     * Add new sector views until lastly added sectorView's bottom edge be greater than
     * layoutStartEdge
     */
    private void addSectorsToLayoutStartEdge(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final View closestToStartSectorView = wheelLayoutManager.getChildClosestToLayoutStartEdge();
        final AbstractWheelLayoutManager.LayoutParams sectorViewLp = AbstractWheelLayoutManager.getChildLayoutParams(closestToStartSectorView);

//        Log.e(AbstractWheelLayoutManager.TAG, "addSectorsToLayoutStartEdge() " +
//                "closestToStartSectorView [" + AbstractWheelLayoutManager.getBigWrapperTitle(closestToStartSectorView) + "]");

        final double sectorAngleInRad = computationHelper.getWheelConfig().getAngularRestrictions().getSectorAngleInRad();

        double newSectorViewLayoutAngle = sectorViewLp.anglePositionInRad + sectorAngleInRad;
        double newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
        int nextChildPos = wheelLayoutManager.getPosition(closestToStartSectorView) - 1;
        int alreadyLayoutedChildrenCount = 0;

        // TODO: 10.02.2016 might be concernes for bottom wheel part - disappearing of bottom most sectorView
        while (newSectorViewBottomEdgeAngularPosInRad < wheelLayoutManager.getLayoutStartAngleInRad()
                && alreadyLayoutedChildrenCount < state.getItemCount()) {
//            Log.i(TAG, "addSectorsToLayoutStartEdge() " +
//                            "newSectorViewLayoutAngle [" + WheelComputationHelper.radToDegree(newSectorViewLayoutAngle) + "], " +
//                            "nextChildPos [" + nextChildPos + "]"
//            );
//            Log.e(AbstractWheelLayoutManager.TAG, "addSectorsToLayoutStartEdge()");
            wheelLayoutManager.setupSectorForPosition(recycler, nextChildPos, newSectorViewLayoutAngle, false);
            newSectorViewLayoutAngle += sectorAngleInRad;
            newSectorViewBottomEdgeAngularPosInRad = computationHelper.getSectorAngleBottomEdgeInRad(newSectorViewLayoutAngle);
            nextChildPos--;
            alreadyLayoutedChildrenCount++;
        }
    }
}
