package com.magicepg;

import android.app.Dialog;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.magicepg.coversflow.CoverEntity;
import com.magicepg.coversflow.CoversFlowComputationHelper;
import com.magicepg.coversflow.widget.HorizontalCoversFlowView;
import com.magicepg.func.Consumer;
import com.magicepg.util.DimensionUtils;
import com.magicepg.wheel.WheelComputationHelper;
import com.magicepg.wheel.WheelListener;
import com.magicepg.wheel.entity.WheelConfig;
import com.magicepg.wheel.entity.WheelDataItem;
import com.magicepg.wheel.widget.WheelsContainerFrameView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.magicepg.wheel.WheelComputationHelper.BOTTOM_EDGE_ANGLE_RESTRICTION_IN_RAD;
import static com.magicepg.wheel.WheelComputationHelper.INNER_RADIUS_TO_OUTER_RADIUS_COEF;
import static com.magicepg.wheel.WheelComputationHelper.TOP_EDGE_ANGLE_RESTRICTION_IN_RAD;
import static com.magicepg.wheel.WheelComputationHelper.TOTAL_SECTORS_AMOUNT;
import static com.magicepg.wheel.WheelComputationHelper.WHEEL_CENTER_X_SHIFT_IN_DP;

/**
 * @author Alexey Kovalev
 * @since 01.02.2017
 */
public final class WheelFragment extends DialogFragment {

    public static final String TAG = WheelFragment.class.getCanonicalName();

    @Bind(R.id.wheel_container_frame)
    WheelsContainerFrameView wheelsContainerFrameView;

    @Bind(R.id.horizontal_covers_flow_list)
    HorizontalCoversFlowView horizontalCoversFlowView;

    @OnClick(R.id.close_wheel_page_icon)
    void onCloseWheelPage() {
        dismiss();
    }

    private WheelPageDataLoader wheelPageDataLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWheelComputationHelpers();
    }

    // TODO: WheelOfFortune 03.02.2016 simplify for now considering container has 0 height
    private void initWheelComputationHelpers() {
        WheelComputationHelper.initialize(getActivity(), createWheelConfigForWheel());
        WheelComputationHelper computationHelper = WheelComputationHelper.getInstance();
        CoversFlowComputationHelper.initialize(computationHelper);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        // hides Android notification bar
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_wheel_page_layout, null);
        dialog.getWindow().setContentView(view);

        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wheel_page_layout, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        ButterKnife.bind(this, rootView);
        wheelPageDataLoader = new WheelPageDataLoader();

        changeHorizontalStripeViewSize();

        horizontalCoversFlowView.addCoverSelectionListener(new HorizontalCoversFlowView.OnCoverSelectionListener() {
            @Override
            public void onCoverSelected(CoverEntity coverEntity) {
//                backgroundCoverView.swapBackgroundData(coverEntity.getCoverImageUri().get());
            }
        });

        horizontalCoversFlowView.addCoverPlayButtonClickListener(new HorizontalCoversFlowView.OnCoverPlayButtonClickListener() {
            @Override
            public void onCoverClicked(CoverEntity clickedCover) {
//                dismiss();
            }
        });

        wheelsContainerFrameView.addWheelListener(new WheelListener() {
            @Override
            public void onDataItemSelected(WheelDataItem selectedDataItem) {
                horizontalCoversFlowView.bind(Collections.<CoverEntity>emptyList());
                loadCoverEntitiesForSelectedWheelItem(selectedDataItem);
            }

            @Override
            public void onWheelRotationStateChange(WheelRotationState wheelRotationState) {
                if (wheelRotationState == WheelRotationState.InRotation) {
                    horizontalCoversFlowView.hideWithScaleDownAnimation();
                } else if (wheelRotationState == WheelRotationState.RotationStopped) {
                    horizontalCoversFlowView.displayWithScaleUpAnimation();
                }
            }
        });

        loadWheelDataItems();
    }

    @Override
    public void dismiss() {
        horizontalCoversFlowView.dispose();
        super.dismiss();
    }

    private void changeHorizontalStripeViewSize() {
        final FrameLayout.LayoutParams coversFlowViewLp = (FrameLayout.LayoutParams) horizontalCoversFlowView.getLayoutParams();
        coversFlowViewLp.height = CoversFlowComputationHelper.getInstance().getCoverMaxHeight();
        horizontalCoversFlowView.setLayoutParams(coversFlowViewLp);
    }

    private void loadWheelDataItems() {
        wheelPageDataLoader.loadWheelData(new Consumer<WheelPageDataLoader.WheelData>() {
            @Override
            public void accept(WheelPageDataLoader.WheelData wheelData) {
                wheelsContainerFrameView.swapDataAndRelayoutWheelsStartingFromPosition(
                        wheelData.getWheelDataItems(),
                        wheelData.getDataItemPositionToSelect()
                );
            }
        });
    }

    private void loadCoverEntitiesForSelectedWheelItem(WheelDataItem selectedDataItem) {
        wheelPageDataLoader.loadCoverEntitiesByWheelItem(selectedDataItem, new Consumer<List<CoverEntity>>() {
            @Override
            public void accept(List<CoverEntity> covers) {
                horizontalCoversFlowView.bind(covers);
//                horizontalCoversFlowView.goToCoverAtPosition(coversFlowDataWrapper.getCoverPositionToSelect());
            }
        });
    }


    private WheelConfig createWheelConfigForWheel() {
        final int screenHeight = WheelComputationHelper.computeScreenDimensions(getActivity()).getHeight();

        final int yWheelCenterPosition = screenHeight / 2;
        final float xWheelCenterPosition = -DimensionUtils.dpToPixels(WHEEL_CENTER_X_SHIFT_IN_DP);
        final PointF circleCenter = new PointF(xWheelCenterPosition, yWheelCenterPosition);

        final float outerRadiusRaw = screenHeight / 2;
        final float innerRadiusRaw = INNER_RADIUS_TO_OUTER_RADIUS_COEF * outerRadiusRaw;
        final float thirdPartSectorEdgeLength = (outerRadiusRaw - innerRadiusRaw) / 3;

        // we have to move wheel outer radius outside screen's top and bottom restrictions
        final int outerRadius = (int) (outerRadiusRaw + thirdPartSectorEdgeLength);
        final int innerRadius = (int) (innerRadiusRaw + thirdPartSectorEdgeLength);
        final double sectorAngleInRad = computeSectorAngleInRad(TOP_EDGE_ANGLE_RESTRICTION_IN_RAD, BOTTOM_EDGE_ANGLE_RESTRICTION_IN_RAD);
        return createWheelConfig(outerRadius, innerRadius, circleCenter, sectorAngleInRad);
    }

    public static WheelConfig createWheelConfig(int outerRadius, int innerRadius, PointF circleCenter, double sectorAngleInRad) {

        final double halfGapAreaAngleInRad = computeHalfGapAreaAngleInRad(sectorAngleInRad);

        final WheelConfig.AngularRestrictions angularRestrictions = WheelConfig.AngularRestrictions
                .builder(sectorAngleInRad)
                .wheelEdgesAngularRestrictions(TOP_EDGE_ANGLE_RESTRICTION_IN_RAD, BOTTOM_EDGE_ANGLE_RESTRICTION_IN_RAD)
                .gapEdgesAngularRestrictions(halfGapAreaAngleInRad, -halfGapAreaAngleInRad)
                .build();

        return new WheelConfig(circleCenter, outerRadius, innerRadius, angularRestrictions);
    }

    private static double computeSectorAngleInRad(double topEdgeAngleRestrictionInRad, double bottomEdgeAngleRestrictionInRad) {
        final double availableAngleInRad = topEdgeAngleRestrictionInRad - bottomEdgeAngleRestrictionInRad;
        return availableAngleInRad / TOTAL_SECTORS_AMOUNT;
    }

    private static double computeHalfGapAreaAngleInRad(double sectorAngleInRad) {
        return sectorAngleInRad + sectorAngleInRad / 2;
    }

}