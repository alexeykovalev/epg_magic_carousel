package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sss.magicwheel.R;
import com.sss.magicwheel.coversflow.entity.CoverEntity;

/**
 * @author Alexey Kovalev
 * @since 23.02.2016.
 */
public class HorizontalCoverView extends FrameLayout {

    private ImageView coverImage;
    private TextView coverTitle;

    private MarginLayoutParams initialCoverViewLp;

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


    public void bind(CoverEntity entityToBind) {
        coverTitle.setText(entityToBind.getTitle());
        Picasso.with(getContext())
                .load(entityToBind.getImageResource())
                .resize(300, 300)
                .into(coverImage);
    }

    public void restoreInitialSize() {
        setLayoutParams(initialCoverViewLp);
    }

    public void saveInitialSize() {
        if (initialCoverViewLp == null) {
            final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
            this.initialCoverViewLp = new MarginLayoutParams(layoutParams);
        }
    }

    public int getInitialWidth() {
        return initialCoverViewLp.width;
    }

    public int getInitialHeight() {
        return initialCoverViewLp.height;
    }

    public double getAspectRatio() {
        return getInitialWidth() / getInitialHeight();
    }
}
