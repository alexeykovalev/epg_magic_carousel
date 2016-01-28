package com.sss.magicwheel.manager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sss.magicwheel.R;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelBigWrapperView extends FrameLayout {

    private final WheelSectorWrapperView sectorWrapperView;
    private final TextView titleView;

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
        initSectorWrapperView();
    }

    private void initSectorWrapperView() {
        WheelComputationHelper computationHelper = WheelComputationHelper.getInstance();

        ViewGroup.LayoutParams lp = sectorWrapperView.getLayoutParams();
        lp.width = computationHelper.getSectorWrapperViewWidth();
        lp.height = computationHelper.getSectorWrapperViewHeight();
        sectorWrapperView.setLayoutParams(lp);

        sectorWrapperView.setSectorClipArea(computationHelper.createSectorClipArea());
    }

    public void updateText(String text) {
        titleView.setText(text);
    }

    public void loadImage(int imageDrawableResId) {
        WheelComputationHelper computationHelper = WheelComputationHelper.getInstance();
        Picasso
                .with(getContext())
                .load(imageDrawableResId)
                .resize(computationHelper.getSectorWrapperViewWidth(), computationHelper.getSectorWrapperViewHeight())
                .into(sectorWrapperView);
    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        WheelComputationHelper computationHelper = WheelComputationHelper.getInstance();
//
//        Paint paint = new Paint();
//        paint.setColor(Color.GREEN);
//        paint.setStrokeWidth(10);
//        paint.setAntiAlias(true);
//
//
//        Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        myPaint.setStyle(Paint.Style.STROKE);
//        int strokeWidth = 20;  // or whatever
//        myPaint.setStrokeWidth(strokeWidth);
//        myPaint.setColor(0xffff0000);   //color.RED
//        float radius= computationHelper.getCircleConfig().getInnerRadius();
//
//        super.dispatchDraw(canvas);
//
////        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), 0, paint);
//        canvas.drawCircle(0, getMeasuredHeight() / 2, radius, myPaint);
//    }

}
