package com.sss.magicwheel.manager.decor;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.manager.WheelAdapter;

/**
 * @author Alexey Kovalev
 * @since 29.01.2016.
 */
public final class WheelSectorLeftSideColorItemDecoration extends WheelBaseItemDecoration {

    public WheelSectorLeftSideColorItemDecoration(Context context) {
        super(context);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView wheelView, RecyclerView.State state) {
        final WheelAdapter wheelAdapter = getWheelAdapter(wheelView);
        for (int i = 0; i < wheelView.getChildCount(); i++) {
            final View sectorView = wheelView.getChildAt(i);
            final int sectorViewAdapterPosition = wheelView.getChildAdapterPosition(sectorView);
            drawSectorSideColor(sectorView, wheelAdapter.getDataItemByPosition(sectorViewAdapterPosition));
        }
    }

    private void drawSectorSideColor(View sectorView, WheelDataItem sectorDataItem) {

    }

}
