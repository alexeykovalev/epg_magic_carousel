package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sss.magicwheel.App;
import com.sss.magicwheel.R;
import com.sss.magicwheel.coversflow.entity.CoverEntity;

/**
 * @author Alexey Kovalev
 * @since 23.02.2016.
 */
public class HorizontalCoverView extends FrameLayout implements IHorizontalCoverView {
    @Deprecated
    private static final int INITIAL_HEIGHT_IN_DP = 100;

    @Deprecated
    private static final int INITIAL_WIDTH_IN_DP = (int) (INITIAL_HEIGHT_IN_DP * CoversFlowListMeasurements.COVER_ASPECT_RATIO);

    @Deprecated
    private final static MarginLayoutParams INITIAL_COVER_LAYOUT_PARAMS = new MarginLayoutParams(
            (int) App.dpToPixels(INITIAL_WIDTH_IN_DP),
            (int) App.dpToPixels(INITIAL_HEIGHT_IN_DP)
    );

    private static final Rect MARGINS_RECT;

    static {
        INITIAL_COVER_LAYOUT_PARAMS.leftMargin = (int) App.dpToPixels(15);
        MARGINS_RECT = new Rect(
                INITIAL_COVER_LAYOUT_PARAMS.leftMargin,
                INITIAL_COVER_LAYOUT_PARAMS.topMargin,
                INITIAL_COVER_LAYOUT_PARAMS.rightMargin,
                INITIAL_COVER_LAYOUT_PARAMS.bottomMargin
        );
    }

    public static MarginLayoutParams safeCopyInitialLayoutParams() {
        return CoversFlowListMeasurements.getInstance().safeCopyInitialLayoutParams();
    }

    @Deprecated
    public static int getInitialWidth() {
        return CoversFlowListMeasurements.getInstance().getCoverDefaultWidth();
    }

    @Deprecated
    public static int getInitialHeight() {
        return CoversFlowListMeasurements.getInstance().getCoverDefaultHeight();
    }

    @Deprecated
    public static Rect getInitialMargins() {
        return CoversFlowListMeasurements.getInstance().getCoverDefaultMargins();
    }


    private ImageView coverImage;
    private TextView coverTitle;

    public HorizontalCoverView(Context context) {
        this(context, null);
    }

    public HorizontalCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateAndBind(context);
    }

    private void inflateAndBind(Context context) {
        inflate(context, R.layout.horizontal_cover_layout, this);
        coverImage = (ImageView) findViewById(R.id.cover_image);
        coverTitle = (TextView) findViewById(R.id.cover_title);
    }

    @Override
    public void bind(CoverEntity entityToBind) {
        coverTitle.setText(entityToBind.getTitle());
        Picasso.with(getContext())
                .load(entityToBind.getImageResource())
                .resize(300, 300)
                .into(coverImage);
    }

    public void restoreInitialSize(int parentHeight) {
        final int topMarginValue = (parentHeight - getHeight()) / 2;
        final MarginLayoutParams lp = safeCopyInitialLayoutParams();
        lp.topMargin = topMarginValue;
        setLayoutParams(lp);
    }

}
