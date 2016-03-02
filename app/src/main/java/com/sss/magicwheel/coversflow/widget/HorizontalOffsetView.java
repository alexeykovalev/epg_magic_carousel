package com.sss.magicwheel.coversflow.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.coversflow.entity.CoverEntity;

/**
 * @author Alexey Kovalev
 * @since 24.02.2016.
 */
public final class HorizontalOffsetView extends View implements IHorizontalCoverView {

    private int offsetValue = CoverEntity.NOT_DEFINED_OFFSET_VALUE;

    public HorizontalOffsetView(Context context) {
        this(context, null);
    }

    public HorizontalOffsetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalOffsetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bind(CoverEntity coverEntity) {
        this.offsetValue = coverEntity.getOffsetValue();
    }

    @Override
    public boolean isOffsetCover() {
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resMeasureSpec = widthMeasureSpec;
        if (offsetValue != CoverEntity.NOT_DEFINED_OFFSET_VALUE) {
            resMeasureSpec = MeasureSpec.makeMeasureSpec(offsetValue, MeasureSpec.EXACTLY);
        }
        super.onMeasure(resMeasureSpec, heightMeasureSpec);
    }
}
