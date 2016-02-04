package com.sss.magicwheel.manager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.WheelDataItem;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelBigWrapperView extends FrameLayout {

    private WheelSectorWrapperView sectorWrapperView;
    private TextView titleView;
    private final WheelComputationHelper computationHelper;

    public WheelBigWrapperView(Context context) {
        this(context, null);
    }

    public WheelBigWrapperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelBigWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.computationHelper = WheelComputationHelper.getInstance();

        inflateAndBindContent(context);
        initSectorWrapperView();
    }

    private void inflateAndBindContent(Context context) {
        final View rootView = inflate(context, R.layout.wheel_big_wrapper_view_layout, this);
        sectorWrapperView = (WheelSectorWrapperView) rootView.findViewById(R.id.sector_wrapper_view);
        titleView = (TextView) rootView.findViewById(R.id.big_wrapper_text);
    }

    private void initSectorWrapperView() {
        WheelComputationHelper computationHelper = WheelComputationHelper.getInstance();

        ViewGroup.LayoutParams lp = sectorWrapperView.getLayoutParams();
        lp.width = computationHelper.getSectorWrapperViewMeasurements().getWidth();
        lp.height = computationHelper.getSectorWrapperViewMeasurements().getHeight();
        sectorWrapperView.setLayoutParams(lp);

        sectorWrapperView.setSectorClipArea(computationHelper.getSectorClipArea());
    }

    public void bindData(WheelDataItem dataItem) {
        sectorWrapperView.bindData(dataItem);
        titleView.setText(dataItem.getTitle());
        loadSectorImage(dataItem.getSectorImageDrawable());
    }

    private void loadSectorImage(int imageDrawableResId) {
        final int targetWidth = computationHelper.getSectorWrapperViewMeasurements().getWidth();
        final int targetHeight = computationHelper.getSectorWrapperViewMeasurements().getHeight();
        Picasso
                .with(getContext())
                .load(imageDrawableResId)
                .resize(targetWidth, targetHeight)
                .into(sectorWrapperView);
    }

    public String getTitle() {
        return titleView.getText().toString();
    }

    // TODO: 01.02.2016 attempt to draw rays using big wrapper - problem big wrapper view has to be much more larger size than it actually is
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
//        float radius= computationHelper.getWheelConfig().getInnerRadius();
//
//        super.dispatchDraw(canvas);
//
////        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), 0, paint);
//        canvas.drawCircle(0, getMeasuredHeight() / 2, radius, myPaint);
//    }

}
