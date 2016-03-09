package com.sss.magicwheel.wheel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.R;
import com.sss.magicwheel.wheel.entity.WheelDataItem;
import com.sss.magicwheel.wheel.widget.WheelBigWrapperView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public final class WheelAdapter extends RecyclerView.Adapter<WheelAdapter.WheelItemViewHolder> {

    /**
     * In order to support endless wheel scrolling we have to set up fake position
     * from which data items will be fetched.
     */
    private static final int VIRTUAL_ITEMS_COUNT = Integer.MAX_VALUE;

    /**
     * In order to support endless wheel scrolling we have to set up fake position
     * from which data items will be fetched.
     */
    public static final int MIDDLE_VIRTUAL_ITEMS_COUNT = VIRTUAL_ITEMS_COUNT / 2;

    private final List<WheelDataItem> dataItems;
    private final OnWheelItemClickListener itemClickListener;
    private final LayoutInflater inflater;

    /**
     * For internal use only. Don't use for passing {@link WheelDataItem}
     * entity associated with sector.
     */
    public interface OnWheelItemClickListener {
        void onItemClicked(View clickedSectorView);
    }

    // TODO: WheelOfFortune 29.01.2016 Check for Preconditions here
    public WheelAdapter(Context context, List<WheelDataItem> dataItems, OnWheelItemClickListener itemClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.dataItems = new ArrayList<>(dataItems);
        this.itemClickListener = itemClickListener;
    }

    public void swapData(List<WheelDataItem> newData) {
        dataItems.clear();
        dataItems.addAll(newData);
        notifyDataSetChanged();
    }

    // TODO: WheelOfFortune 29.01.2016 Optional<WheelDataItem> here
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
        return getRealItemCount() == 0 ? 0 : VIRTUAL_ITEMS_COUNT;
    }

    public List<WheelDataItem> getData() {
        return Collections.unmodifiableList(dataItems);
    }

    public int getRealItemCount() {
        return dataItems.size();
    }

    public int toRealPosition(int virtualPosition) {
        final int realItemsCount = getRealItemCount();
        final int shift = (virtualPosition - MIDDLE_VIRTUAL_ITEMS_COUNT) % realItemsCount;
        final boolean isPositiveShift = shift >= 0;
        return isPositiveShift ? shift : (realItemsCount + shift);
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
                    itemClickListener.onItemClicked(itemView);
                }
            });
            bigWrapperView.bindData(dataItem);
        }
    }

}
