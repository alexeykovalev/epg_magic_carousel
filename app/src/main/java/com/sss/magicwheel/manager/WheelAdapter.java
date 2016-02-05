package com.sss.magicwheel.manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    private final LayoutInflater inflater;

    // TODO: 29.01.2016 Check for Preconditions here
    public WheelAdapter(Context context, List<WheelDataItem> dataItems) {
        this.dataItems = dataItems;
        this.inflater = LayoutInflater.from(context);
    }

    // TODO: 29.01.2016 Optional<WheelDataItem> here
    public WheelDataItem getDataItemByPosition(int virtualPosition) {
        return dataItems.get(toRealPosition(virtualPosition));
    }

    @Override
    public WheelItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) inflater.inflate(R.layout.wheel_big_wrapper_layout, parent, false);
        return new WheelItemViewHolder(bigWrapperView);
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
        return Math.abs((virtualPosition - MIDDLE_VIRTUAL_ITEMS_COUNT) % dataItems.size());
    }

    static class WheelItemViewHolder extends RecyclerView.ViewHolder {

        private final WheelBigWrapperView bigWrapperView;

        public WheelItemViewHolder(WheelBigWrapperView bigWrapperView) {
            super(bigWrapperView);
            this.bigWrapperView = bigWrapperView;
        }

        void bindData(WheelDataItem dataItem) {
            bigWrapperView.bindData(dataItem);
        }
    }

}
