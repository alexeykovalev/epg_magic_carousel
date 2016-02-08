package com.sss.magicwheel.manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.manager.widget.WheelBigWrapperView;

import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelAdapter extends RecyclerView.Adapter<WheelAdapter.WheelItemViewHolder> {

    public static final int VIRTUAL_ITEMS_COUNT = Integer.MAX_VALUE;
    public static final int MIDDLE_VIRTUAL_ITEMS_COUNT = VIRTUAL_ITEMS_COUNT / 2;

    private final List<WheelDataItem> dataItems;
    private final OnWheelItemClickListener itemClickListener;
    private final LayoutInflater inflater;

    public interface OnWheelItemClickListener {
        void onItemClicked(WheelDataItem dataItem);
    }

    // TODO: 29.01.2016 Check for Preconditions here
    public WheelAdapter(Context context, List<WheelDataItem> dataItems, OnWheelItemClickListener itemClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.dataItems = dataItems;
        this.itemClickListener = itemClickListener;
    }

    public int getVirtualPositionForDataItem(WheelDataItem dataItemToSelect) {
        return MIDDLE_VIRTUAL_ITEMS_COUNT + findRealPositionForDataItem(dataItemToSelect);
    }

    // TODO: 08.02.2016 Guava's find method has to be here
    private int findRealPositionForDataItem(WheelDataItem dataItemToFind) {
        int res = 0;
        for (WheelDataItem dataItem : dataItems) {
            if (dataItem.equals(dataItemToFind)) {
                break;
            }
            res++;
        }
        return res;
    }

    // TODO: 29.01.2016 Optional<WheelDataItem> here
    public WheelDataItem getDataItemByPosition(int virtualPosition) {
        return dataItems.get(toRealPosition(virtualPosition));
    }

    @Override
    public WheelItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) inflater.inflate(R.layout.wheel_big_wrapper_layout, parent, false);
        return new WheelItemViewHolder(bigWrapperView, itemClickListener);
    }

    @Override
    public void onBindViewHolder(WheelItemViewHolder holder, int position) {
        holder.bindData(getDataItemByPosition(position));
    }

    /**
     * In order to make wheel infinite we return virtual items count instead
     * of effectively existing.
     */
    @Override
    public int getItemCount() {
        return VIRTUAL_ITEMS_COUNT;
    }

    public int getRealItemCount() {
        return dataItems.size();
    }

    private int toRealPosition(int virtualPosition) {
        final int shift = (virtualPosition - MIDDLE_VIRTUAL_ITEMS_COUNT) % dataItems.size();
        final boolean isPositiveShift = shift >= 0;
        return isPositiveShift ?
                shift : (dataItems.size() + shift);
    }

    static class WheelItemViewHolder extends RecyclerView.ViewHolder {

        private final WheelBigWrapperView bigWrapperView;
        private final OnWheelItemClickListener itemClickListener;

        public WheelItemViewHolder(WheelBigWrapperView bigWrapperView, OnWheelItemClickListener itemClickListener) {
            super(bigWrapperView);
            this.bigWrapperView = bigWrapperView;
            this.itemClickListener = itemClickListener;
        }

        void bindData(final WheelDataItem dataItem) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClicked(dataItem);
                }
            });
            bigWrapperView.bindData(dataItem);
        }
    }

}
