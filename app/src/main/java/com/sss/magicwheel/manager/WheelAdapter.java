package com.sss.magicwheel.manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.WheelDataItem;

import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 04.12.2015.
 */
public class WheelAdapter extends RecyclerView.Adapter<WheelAdapter.WheelItemViewHolder> {

    private static final int[] COVERS_LIST_DRAWABLE = new int[] {
            R.drawable.first_cover,
            R.drawable.second_cover
    };

    private static int counter = 0;

    private static int getCoverDrawable() {
        int index = counter % COVERS_LIST_DRAWABLE.length;
        counter++;
        return COVERS_LIST_DRAWABLE[index];
    }

    private final List<WheelDataItem> dataItems;
    private final LayoutInflater inflater;

    // TODO: 29.01.2016 Check for Preconditions here
    public WheelAdapter(Context context, List<WheelDataItem> dataItems) {
        this.dataItems = dataItems;
        this.inflater = LayoutInflater.from(context);
    }

    // TODO: 29.01.2016 Optional<WheelDataItem> here
    public WheelDataItem getDataItemByPosition(int position) {
        return position < dataItems.size() ? dataItems.get(position) : null;
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

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    static class WheelItemViewHolder extends RecyclerView.ViewHolder {

        private final WheelBigWrapperView bigWrapperView;

        public WheelItemViewHolder(WheelBigWrapperView bigWrapperView) {
            super(bigWrapperView);
            this.bigWrapperView = bigWrapperView;
        }

        void bindData(WheelDataItem dataItem) {
            bigWrapperView.updateText(dataItem.getTitle());
            bigWrapperView.loadImage(getCoverDrawable());
        }
    }

}
