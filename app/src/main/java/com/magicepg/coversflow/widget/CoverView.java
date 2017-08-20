package com.magicepg.coversflow.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.magicepg.R;
import com.magicepg.coversflow.CoverEntity;
import com.magicepg.coversflow.CoversFlowComputationHelper;
import com.magicepg.func.Optional;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author Alexey Kovalev
 * @since 23.02.2016.
 */
public final class CoverView extends FrameLayout {

    private static final float FULLY_TRANSPARENT = 0.0f;
    private static final float FULLY_OPAQUE = 1.0f;

    @Bind(R.id.cover_image)
    ImageView coverImageView;

    @Bind(R.id.cover_main_title)
    TextView coverMainTitleView;

    @Bind(R.id.cover_helper_title)
    TextView coverHelperTitle;

    @Bind(R.id.asset_info_line_view)
    TextView assetInfoLineView;

    @Bind({
            R.id.asset_cover_bg_shader,
            R.id.cover_main_title,
            R.id.asset_play_button,
            R.id.asset_info_line_view
    })
    List<View> mainContentViews;

    @Bind({
            R.id.cover_helper_title
    })
    List<View> helperContentView;

    @OnClick(R.id.asset_play_button)
    void handleClickOnPlayButton() {
        if (onPlayButtonClickListener != null) {
            onPlayButtonClickListener.onCoverClick(entityToBind);
        }
    }

    private final CoversFlowComputationHelper assetsComputationHelper;
    private CoverEntity entityToBind;
    private OnPlayButtonClickListener onPlayButtonClickListener;
    private final CoverScalingData coverScalingData = new CoverScalingData();

    public CoverView(Context context) {
        this(context, null);
    }

    public CoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        assetsComputationHelper = CoversFlowComputationHelper.getInstance();
        inflateAndBind(context);
    }

    private void inflateAndBind(Context context) {
        inflate(context, R.layout.horizontal_cover_layout, this);
        ButterKnife.bind(this);
    }

    public void setOnPlayButtonClickListener(OnPlayButtonClickListener onPlayButtonClickListener) {
        this.onPlayButtonClickListener = onPlayButtonClickListener;
    }

    public CoverEntity getAssociatedData() {
        return entityToBind;
    }

    /**
     * Hook invoked when cover has been selected (resized to MAX size)
     * in covers flow.
     */
    public void onCoverSelected() {
        final Optional<Uri> coverImageUriWrapper = entityToBind.getCoverImageUri();
        if (coverImageUriWrapper.isPresent()) {
            final int coverMaxWidth = assetsComputationHelper.getCoverMaxWidth();
            final int coverMaxHeight = assetsComputationHelper.getCoverMaxHeight();
            Glide.with(getContext())
                    .load(coverImageUriWrapper.get())
                    .override(coverMaxWidth, coverMaxHeight)
                    .centerCrop()
                    .into(coverImageView);
        }
    }

    public void bind(CoverEntity entityToBind) {
        this.entityToBind = entityToBind;
        coverMainTitleView.setText(entityToBind.getTitle());
        coverHelperTitle.setText(entityToBind.getTitle());
        assetInfoLineView.setText("Additional Info Line");
        loadCoverImageForWidthSize(entityToBind);
    }

    public void setDefaultSize(int containerHeight) {
        setCoverViewSize(containerHeight,
                assetsComputationHelper.getCoverDefaultWidth(),
                assetsComputationHelper.getCoverDefaultHeight()
        );
    }

    public void setCoverViewSize(float containerHeight, int coverViewNewWidth, int coverViewNewHeight) {
        final MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        lp.height = coverViewNewHeight;
        lp.width = coverViewNewWidth;
        lp.topMargin = (int) (containerHeight / 2 - (float) lp.height / 2);
    }

    public void updateScalingData(float scalingFactor, CoverScalingData.ScalingType scalingType) {
        coverScalingData.scalingFactor = scalingFactor;
        coverScalingData.scalingType = scalingType;
        updateContentContainersBasedOnScalingData(coverScalingData);
    }

    // TODO: 22.03.2016 JavaDoc
    private void updateContentContainersBasedOnScalingData(CoverScalingData coverScalingData) {
        final float scalingFactor = coverScalingData.scalingFactor;
        final CoverScalingData.ScalingType scalingType = coverScalingData.scalingType;
        if (scalingType == CoverScalingData.ScalingType.NotDefined) { // cover view has default - not scaled size
            setAlphaForContentContainer(mainContentViews, FULLY_TRANSPARENT);
            setAlphaForContentContainer(helperContentView, FULLY_OPAQUE);
        } else if (scalingType == CoverScalingData.ScalingType.ScaleDown) {
            if (coverScalingData.greatThanHalfScalingFactor()) {
                setAlphaForContentContainer(mainContentViews, 2 * scalingFactor - 1);
                setAlphaForContentContainer(helperContentView, FULLY_TRANSPARENT);
            } else {
                setAlphaForContentContainer(mainContentViews, FULLY_TRANSPARENT);
                setAlphaForContentContainer(helperContentView, 1 - 2 * scalingFactor);
            }
        } else if (scalingType == CoverScalingData.ScalingType.ScaleUp) {
            if (coverScalingData.lessThanHalfScalingFactor()) {
                setAlphaForContentContainer(mainContentViews, FULLY_TRANSPARENT);
                setAlphaForContentContainer(helperContentView, 1 - 2 * scalingFactor);
            } else {
                setAlphaForContentContainer(mainContentViews, 2 * scalingFactor - 1);
                setAlphaForContentContainer(helperContentView, FULLY_TRANSPARENT);
            }
        }
    }

    private void setAlphaForContentContainer(List<View> contentContainer, float alphaValueToSet) {
        final int coverVisibility = alphaValueToSet == 0.0f ? GONE : VISIBLE;
        for (View view : contentContainer) {
            view.setVisibility(coverVisibility);
            view.setAlpha(alphaValueToSet);
        }
    }

    private void loadCoverImageForWidthSize(CoverEntity coverEntity) {
        final Optional<Uri> coverImageUrlWrapper = coverEntity.getCoverImageUri();
        if (coverImageUrlWrapper.isPresent()) {
            final int coverImageWidth = assetsComputationHelper.getCoverDefaultWidth();
            final int coverImageHeight = assetsComputationHelper.getCoverDefaultHeight();
            Glide.with(getContext())
                    .load(coverEntity.getCoverImageUri().get())
                    .override(coverImageWidth, coverImageHeight)
                    .centerCrop()
                    .into(coverImageView);
        }
    }

    public static final class CoverScalingData {

        static float HALF_SCALING_COEF = 1f / 2;

        public enum ScalingType {
            ScaleUp, ScaleDown, NotDefined
        }

        /**
         * 1 - for fully scaled cover to biggest size
         * 0 - for cover scaled to default size (dropped to default size)
         */
        private float scalingFactor;

        private ScalingType scalingType;

        private CoverScalingData() {
        }

        boolean greatThanHalfScalingFactor() {
            return scalingFactor >= CoverScalingData.HALF_SCALING_COEF && scalingFactor <= 1;
        }

        boolean lessThanHalfScalingFactor() {
            return scalingFactor >= 0 && scalingFactor <= CoverScalingData.HALF_SCALING_COEF;
        }

        @Override
        public String toString() {
            return "CoverScalingData{" +
                    "scalingFactor=" + scalingFactor +
                    ", scalingType=" + scalingType +
                    '}';
        }
    }

    public interface OnPlayButtonClickListener {
        void onCoverClick(CoverEntity coverEntity);
    }
}
