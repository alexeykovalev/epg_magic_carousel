package com.magicepg.wheel.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.magicepg.R;
import com.magicepg.wheel.entity.WheelDataItem;
import com.magicepg.wheel.WheelComputationHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Alexey Kovalev
 * @since 04.12.2016
 */
public final class WheelBigWrapperView extends FrameLayout {

    @Bind(R.id.sector_wrapper_view)
    WheelSectorWrapperView sectorWrapperView;

    @Bind(R.id.sector_data_container)
    ViewGroup sectorDataContainer;

    private final WheelComputationHelper computationHelper;

    public WheelBigWrapperView(Context context) {
        this(context, null);
    }

    public WheelBigWrapperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelBigWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        computationHelper = WheelComputationHelper.getInstance();
        inflateAndBindContent(context);
        initSectorWrapperView();
        setLayerType(ViewGroup.LAYER_TYPE_HARDWARE, null);
    }

    private void inflateAndBindContent(Context context) {
        inflate(context, R.layout.wheel_big_wrapper_view_layout, this);
        ButterKnife.bind(this);
    }

    private void initSectorWrapperView() {
        ViewGroup.LayoutParams lp = sectorWrapperView.getLayoutParams();
        lp.width = computationHelper.getSectorWrapperViewMeasurements().getWidth();
        lp.height = computationHelper.getSectorWrapperViewMeasurements().getHeight();
        sectorWrapperView.setLayoutParams(lp);
        sectorWrapperView.setSectorClipArea(computationHelper.getSectorClipArea());
    }

    public void bindData(WheelDataItem dataItem) {
        loadSectorCoverImage(dataItem);
        sectorWrapperView.setSectorLeftEdgeColor(dataItem.getLeftEdgeColor());
        // TODO: 8/16/17 don't required probably
//        AbstractChannelSectorDataBinder binder = AbstractChannelSectorDataBinder.bindChannelSectorData(dataItem.getChannelData(), sectorDataContainer);
    }

    private void loadSectorCoverImage(WheelDataItem wheelDataItem) {
        final int coverRequiredWidth = computationHelper.getSectorWrapperViewMeasurements().getWidth();
        final int coverRequiredHeight = computationHelper.getSectorWrapperViewMeasurements().getHeight();

        if (wheelDataItem.hasCover()) {
            Uri sectorCoverUrl = wheelDataItem.getCoverUri().get();
            Glide.with(getContext())
                    .load(sectorCoverUrl)
                    .override(coverRequiredWidth, coverRequiredHeight)
                    .into(sectorWrapperView);
        }
    }

}
