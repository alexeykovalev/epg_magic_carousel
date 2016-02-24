package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.R;
import com.sss.magicwheel.coversflow.entity.CoverEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoversFlowAdapter extends RecyclerView.Adapter<CoversFlowAdapter.CoverViewHolder> {

    private static final int REGULAR_COVER = 0;
    private static final int FAKE_COVER = 1;


    private final Context context;
    private final LayoutInflater inflater;
    private final List<CoverEntity> coversData;

    public CoversFlowAdapter(Context context, List<CoverEntity> coversData) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.coversData = new ArrayList<>(coversData);
    }

    public void swapData(List<CoverEntity> coversData) {
        this.coversData.clear();
        this.coversData.addAll(coversData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return getItemForPosition(position).isOffsetItem() ? FAKE_COVER : REGULAR_COVER;
    }

    @Override
    public CoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View resView;
        if (viewType == REGULAR_COVER) {
            final HorizontalCoverView coverView = (HorizontalCoverView) inflater.inflate(R.layout.cover_item_layout, parent, false);
            coverView.restoreInitialSize();
            resView = coverView;
        } else if (viewType == FAKE_COVER) {
            resView = inflater.inflate(R.layout.fake_cover_layout, parent, false);
        } else {
            throw new IllegalStateException("Unknown viewType [" + viewType + "]");
        }

        return new CoverViewHolder(resView);
    }

    @Override
    public void onBindViewHolder(CoverViewHolder holder, int position) {
        holder.bind(getItemForPosition(position));
    }

    @Override
    public int getItemCount() {
        return coversData.size();
    }

    private CoverEntity getItemForPosition(int position) {
        return coversData.get(position);
    }

    static class CoverViewHolder extends RecyclerView.ViewHolder {

        public CoverViewHolder(View coverView) {
            super(coverView);
        }

        void bind(CoverEntity entityToBind) {
            if (!entityToBind.isOffsetItem()) {
                ((HorizontalCoverView) itemView).bind(entityToBind);
            }
        }
    }
}
