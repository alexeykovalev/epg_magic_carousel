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
    private static final int OFFSET_COVER = 1;

    private final LayoutInflater inflater;
    private final List<CoverEntity> coversData;

    public CoversFlowAdapter(Context context, List<CoverEntity> coversData) {
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
        return getItemForPosition(position).isOffsetItem() ? OFFSET_COVER : REGULAR_COVER;
    }

    @Override
    public CoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final IHorizontalCoverView resView;
        if (viewType == REGULAR_COVER) {
            final HorizontalCoverView coverView = (HorizontalCoverView) inflater.inflate(R.layout.cover_item_layout, parent, false);
            coverView.restoreInitialSize();
            resView = coverView;
        } else if (viewType == OFFSET_COVER) {
            resView = (IHorizontalCoverView) inflater.inflate(R.layout.fake_cover_layout, parent, false);
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

        private final IHorizontalCoverView coverView;

        public CoverViewHolder(IHorizontalCoverView coverView) {
            super((View) coverView);
            this.coverView = coverView;
        }

        void bind(CoverEntity entityToBind) {
            coverView.bind(entityToBind);
        }
    }
}
