package com.sss.magicwheel;

import android.app.Fragment;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sss.magicwheel.coversflow.CoversFlowListMeasurements;
import com.sss.magicwheel.coversflow.entity.CoverEntity;
import com.sss.magicwheel.wheel.entity.WheelConfig;
import com.sss.magicwheel.wheel.entity.WheelDataItem;
import com.sss.magicwheel.coversflow.widget.HorizontalCoversFlowView;
import com.sss.magicwheel.wheel.WheelComputationHelper;
import com.sss.magicwheel.wheel.widget.WheelListener;
import com.sss.magicwheel.wheel.widget.WheelOfFortuneContainerFrameView;

import java.util.ArrayList;
import java.util.List;

import static com.sss.magicwheel.wheel.WheelComputationHelper.*;

/**
 * @author Alexey Kovalev
 * @since 01.02.2016.
 */
public final class FragmentWheelPage extends Fragment {

    private static final int DEFAULT_SECTOR_ANGLE_IN_DEGREE = 20;

    private boolean isWheelContainerInitialized;

    private final Handler handler = new Handler();

    private WheelOfFortuneContainerFrameView wheelOfFortuneContainerFrameView;

    private HorizontalCoversFlowView horizontalCoversFlowView;
    private WheelComputationHelper computationHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        // TODO: 03.02.2016 simplify for now considering container has 0 height

        WheelComputationHelper.initialize(createWheelConfig(0));
        computationHelper = WheelComputationHelper.getInstance();
        CoversFlowListMeasurements.initialize(computationHelper, getActivity());

        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_wheel_page_layout, container, false);

        wheelOfFortuneContainerFrameView = (WheelOfFortuneContainerFrameView) rootView.findViewById(R.id.wheel_of_fortune_container_frame);
        wheelOfFortuneContainerFrameView.swapData(createWheelSampleDataSet());

        wheelOfFortuneContainerFrameView.addDataItemSelectionListener(new WheelListener() {
            @Override
            public void onDataItemSelected(WheelDataItem selectedDataItem) {
//                Log.e("TAG", "selectedDataItem [" + selectedDataItem.getTitle() + "]");
                horizontalCoversFlowView.swapData(loadDataForWheelDataItem(selectedDataItem));
                // TODO: 29.02.2016 most probably has to be in data loading callback
                horizontalCoversFlowView.displayWithScaleUpAnimation();
            }

            @Override
            public void onWheelRotationStateChange(WheelRotationState wheelRotationState) {
                if (wheelRotationState == WheelRotationState.InRotation) {
                    horizontalCoversFlowView.hideWithScaleDownAnimation();
                }
            }
        });

        inflateCoversFlowContainer(inflater, rootView);

        /*rootView.findViewById(R.id.fragment_request_layout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/

/*
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (!isWheelContainerInitialized) {
                    isWheelContainerInitialized = true;
                    final int fragmentContainerTopEdge = container.getTop();
                    WheelComputationHelper.initialize(createWheelConfig(fragmentContainerTopEdge));
                    initTopWheelContainer(topWheelContainerView);
                }
            }
        });
*/
        return rootView;
    }

    private void inflateCoversFlowContainer(LayoutInflater inflater, ViewGroup fragmentRootView) {
        horizontalCoversFlowView = (HorizontalCoversFlowView) inflater.inflate(
                R.layout.horizontal_covers_flow_list_layout, fragmentRootView, false
        );

        horizontalCoversFlowView.setVisibility(View.GONE);

        final FrameLayout.LayoutParams coversFlowViewLp = (FrameLayout.LayoutParams) horizontalCoversFlowView.getLayoutParams();
        coversFlowViewLp.height = computeCoversFlowListHeight();

        // TODO: 25.02.2016 hack for testing
//        coversFlowViewLp.topMargin = (int) App.dpToPixels(10);
        horizontalCoversFlowView.setLayoutParams(coversFlowViewLp);

        fragmentRootView.addView(horizontalCoversFlowView);
    }

    private int computeCoversFlowListHeight() {
        return CoversFlowListMeasurements.getInstance().getCoverMaxHeight();
    }

    private List<CoverEntity> loadDataForWheelDataItem(WheelDataItem selectedWheelDataItem) {
        return createSampleCoversData(20);
    }

    private List<CoverEntity> createSampleCoversData(int amountOfCovers) {
        final List<CoverEntity> covers = new ArrayList<>();
        final int leftOffset = CoversFlowListMeasurements.getInstance().getLeftOffset();
        final int rightOffset = CoversFlowListMeasurements.getInstance().getRightOffset();

        covers.add(CoverEntity.offsetItem(leftOffset));
        for (int i = 0; i < amountOfCovers; i++) {
            covers.add(CoverEntity.dataItem("Cover [" + i + "]"));
        }
        covers.add(CoverEntity.offsetItem(rightOffset));
        return covers;
    }

    private List<WheelDataItem> createWheelSampleDataSet() {
        List<WheelDataItem> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(new WheelDataItem("item.Num [" + i + "]"));
        }
        return items;
    }

    private WheelConfig createWheelConfig(int fragmentContainerTopEdge) {
        final int screenHeight = WheelComputationHelper.getScreenDimensions(getActivity()).getHeight();
        final int availableWheelRenderingHeight = (screenHeight - fragmentContainerTopEdge) / 2;

        final int yWheelCenterPosition = availableWheelRenderingHeight;
        final PointF circleCenter = new PointF(0, yWheelCenterPosition);

        // TODO: 03.12.2015 Not good hardcoded values
        final int outerRadius = availableWheelRenderingHeight;
        final int innerRadius = outerRadius - outerRadius / 3;

        final double sectorAngleInRad = computeSectorAngleInRad(TOP_EDGE_ANGLE_RESTRICTION_IN_RAD, BOTTOM_EDGE_ANGLE_RESTRICTION_IN_RAD);
        final double halfGapAreaAngleInRad = computeHalfGapAreaAngleInRad(sectorAngleInRad);

        final WheelConfig.AngularRestrictions angularRestrictions = WheelConfig.AngularRestrictions
                .builder(sectorAngleInRad)
                .wheelEdgesAngularRestrictions(TOP_EDGE_ANGLE_RESTRICTION_IN_RAD, BOTTOM_EDGE_ANGLE_RESTRICTION_IN_RAD)
                .gapEdgesAngularRestrictions(halfGapAreaAngleInRad, -halfGapAreaAngleInRad)
                .build();

        return new WheelConfig(circleCenter, outerRadius, innerRadius, angularRestrictions);
    }

    private double computeSectorAngleInRad(double topEdgeAngleRestrictionInRad, double bottomEdgeAngleRestrictionInRad) {
        final double availableAngleInRad = topEdgeAngleRestrictionInRad - bottomEdgeAngleRestrictionInRad;
        return availableAngleInRad / TOTAL_SECTORS_AMOUNT;
    }

    private double computeHalfGapAreaAngleInRad(double sectorAngleInRad) {
        return sectorAngleInRad + sectorAngleInRad / 2;
    }

}
