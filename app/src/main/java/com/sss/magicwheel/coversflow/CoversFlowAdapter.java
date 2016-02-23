package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    public CoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final HorizontalCoverView coverView = (HorizontalCoverView) inflater.inflate(R.layout.cover_item_layout, parent, false);
        coverView.restoreInitialSize();
        return new CoverViewHolder(coverView);
    }

    @Override
    public void onBindViewHolder(CoverViewHolder holder, int position) {
        holder.bind(getItemForPosition(position));
    }

    @Override
    public void onViewRecycled(CoverViewHolder holder) {
        holder.coverView.restoreInitialSize();
    }

    @Override
    public int getItemCount() {
        return coversData.size();
    }

    private CoverEntity getItemForPosition(int position) {
        return coversData.get(position);
    }

    static class CoverViewHolder extends RecyclerView.ViewHolder {

        private final HorizontalCoverView coverView;

        public CoverViewHolder(HorizontalCoverView coverView) {
            super(coverView);
            this.coverView = coverView;
        }

        void bind(CoverEntity entityToBind) {
            coverView.bind(entityToBind);
        }
    }
}
