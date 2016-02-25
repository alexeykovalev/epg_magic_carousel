package com.sss.magicwheel.coversflow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sss.magicwheel.R;
import com.sss.magicwheel.coversflow.CoversFlowListMeasurements;
import com.sss.magicwheel.coversflow.entity.CoverEntity;

/**
 * @author Alexey Kovalev
 * @since 23.02.2016.
 */
public class HorizontalCoverView extends FrameLayout implements IHorizontalCoverView {

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
        final MarginLayoutParams lp = CoversFlowListMeasurements.getInstance().safeCopyInitialLayoutParams();
        lp.topMargin = topMarginValue;
        setLayoutParams(lp);
    }

}
