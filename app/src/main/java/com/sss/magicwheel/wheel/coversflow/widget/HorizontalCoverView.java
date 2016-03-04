package com.sss.magicwheel.wheel.coversflow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sss.magicwheel.App;
import com.sss.magicwheel.R;
import com.sss.magicwheel.wheel.coversflow.CoversFlowListMeasurements;
import com.sss.magicwheel.wheel.coversflow.entity.CoverEntity;

/**
 * @author Alexey Kovalev
 * @since 23.02.2016.
 */
public final class HorizontalCoverView extends FrameLayout implements IHorizontalCoverView {

    private ImageView coverImage;
    private TextView coverTitle;

    private final CoversFlowListMeasurements coversFlowListMeasurements;

    public HorizontalCoverView(Context context) {
        this(context, null);
    }

    public HorizontalCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        coversFlowListMeasurements = CoversFlowListMeasurements.getInstance();
        inflateAndBind(context);
    }

    private void inflateAndBind(Context context) {
        inflate(context, R.layout.horizontal_cover_layout, this);
        coverImage = (ImageView) findViewById(R.id.cover_image);
        coverTitle = (TextView) findViewById(R.id.cover_title);
    }

    /**
     * Hook invoked when cover has been selected (resized to MAX size)
     * in covers flow.
     */
    public void onCoverSelected() {

    }

    @Override
    public void bind(CoverEntity entityToBind) {
        coverTitle.setText(entityToBind.getTitle());
        App.glide()
                .load(entityToBind.getImageResource())
                .override(300, 300)
                .fitCenter()
                .into(coverImage);
    }

    @Override
    public boolean isOffsetCover() {
        return false;
    }
}
