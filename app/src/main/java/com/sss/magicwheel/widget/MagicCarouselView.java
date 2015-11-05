package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.sss.magicwheel.R;
import com.sss.magicwheel.util.MagicCalculationHelper;

import java.util.Random;

/**
 * @author Alexey
 * @since 05.11.2015
 */
public class MagicCarouselView extends ViewGroup {

    private static final int STUB_VIEW_WIDTH = 400;
    private static final int STUB_VIEW_HEIGHT = 200;
    private static final int[] AVAILABLE_VIEW_COLORS = new int[] {
            Color.BLUE, Color.WHITE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN
    };

    private final MagicCalculationHelper calculationHelper;
    private final Random randomizer;


    public MagicCarouselView(Context context) {
        this(context, null);
    }

    public MagicCarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicCarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        randomizer = new Random();
        calculationHelper = MagicCalculationHelper.getInstance();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        removeAllViewsInLayout();

        int curLeft = 0;
        int curTop = 0;

        View child = createAndMeasureNewView();
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();

        child.layout(curLeft, curTop, curLeft + childWidth, curTop + childHeight);
        addView(child);
    }


    private View createAndMeasureNewView() {
        View stubView = LayoutInflater.from(getContext()).inflate(R.layout.rectangle_stub_view, this, false);

        final int childWidthSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_WIDTH, MeasureSpec.EXACTLY);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(STUB_VIEW_HEIGHT, MeasureSpec.EXACTLY);
        stubView.measure(childWidthSpec, childHeightSpec);
        stubView.setBackgroundColor(getRandomBackgroundColor());
        return stubView;
    }


    private int getRandomBackgroundColor() {
        int index = randomizer.nextInt(AVAILABLE_VIEW_COLORS.length);
        return AVAILABLE_VIEW_COLORS[index];
    }


}