package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.LinearClipData;

import java.util.Random;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelBigWrapperView extends FrameLayout {

    private static final int[] SECTOR_WRAPPER_VIEW_BG_COLORS = new int[] {
            Color.GRAY, Color.BLUE, Color.YELLOW, Color.LTGRAY, Color.RED
    };

    private WheelSectorWrapperView sectorWrapperView;
    private final TextView titleView;
    private final Random randomizer = new Random();

    public WheelBigWrapperView(Context context) {
        this(context, null);
    }

    public WheelBigWrapperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelBigWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final View rootView = inflate(context, R.layout.wheel_big_wrapper_view_layout, this);
        sectorWrapperView = (WheelSectorWrapperView) rootView.findViewById(R.id.sector_wrapper_view);
        titleView = (TextView) rootView.findViewById(R.id.big_wrapper_text);
    }

    public void setSectorWrapperViewSize(int width, int height, LinearClipData sectorClipArea) {

//        Log.e("TAG", "mW " + getMeasuredWidth() + " mH " + getMeasuredHeight());

//        Log.e("TAG", "width [" + WheelUtils.pixelsToDp(getContext(), width) + "], h [" + WheelUtils.pixelsToDp(getContext(), height) + "]");

//        sectorWrapperView = (WheelSectorWrapperView) LayoutInflater.from(getContext()).inflate(R.layout.sector_wrapper_view_layout, this, false);

        /*final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) sectorWrapperView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        sectorWrapperView.setLayoutParams(lp);

        sectorWrapperView.setMaxWidth(width);
        sectorWrapperView.setMaxHeight(height);*/

//        sectorWrapperView.setAdjustViewBounds(false);

        sectorWrapperView.setSectorClipArea(sectorClipArea);

//        brushWithRandomColor(sectorWrapperView);
    }

    public void updateText(String text) {
        titleView.setText(text);
    }

    public String getText() {
        return titleView.getText().toString();
    }

    public void loadImage(int imageDrawableResId) {
        Picasso.with(getContext()).load(imageDrawableResId).into(sectorWrapperView);
    }

    private void brushWithRandomColor(View sectorWrapperView) {
        int index = randomizer.nextInt(SECTOR_WRAPPER_VIEW_BG_COLORS.length);
        int colorToBrush = SECTOR_WRAPPER_VIEW_BG_COLORS[index];
        sectorWrapperView.setBackgroundColor(colorToBrush);
    }

}
