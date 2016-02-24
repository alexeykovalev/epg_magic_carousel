package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sss.magicwheel.App;

/**
 * @author Alexey Kovalev
 * @since 24.02.2016.
 */
public class RightOffsetCoverView extends View {

    private static final int DEFAULT_OFFSET_IN_DP = 300;
    private final int offsetValue;

    public RightOffsetCoverView(Context context) {
        this(context, null);
    }

    public RightOffsetCoverView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RightOffsetCoverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.offsetValue = (int) App.dpToPixels(DEFAULT_OFFSET_IN_DP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSpec = MeasureSpec.makeMeasureSpec(offsetValue, MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, heightMeasureSpec);
    }
}
