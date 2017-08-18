package com.magicepg.coversflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.magicepg.R;
import com.magicepg.coversflow.widget.CoverView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoversFlowAdapter extends RecyclerView.Adapter<CoversFlowAdapter.CoverViewHolder> {

    private final LayoutInflater inflater;
    private final List<CoverEntity> coversData;
    private final ICoverClickListener coverClickListener;
    private final CoverView.OnPlayButtonClickListener coverPlayButtonClickListener;

    public interface ICoverClickListener {
        void onCoverClick(CoverView coverView, CoverEntity coverEntity);
    }

    public CoversFlowAdapter(Context context, List<CoverEntity> coversData,
                             CoverView.OnPlayButtonClickListener coverPlayButtonClickListener,
                             ICoverClickListener coverClickListener) {
        this.coverPlayButtonClickListener = coverPlayButtonClickListener;
        this.inflater = LayoutInflater.from(context);
        this.coversData = new ArrayList<>(coversData);
        this.coverClickListener = coverClickListener;
    }

    public void swapData(List<CoverEntity> coversData) {
        this.coversData.clear();
        this.coversData.addAll(coversData);
        notifyDataSetChanged();
    }

    @Override
    public CoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final CoverView coverView = (CoverView) inflater.inflate(R.layout.cover_item_layout, parent, false);
        coverView.setDefaultSize(parent.getHeight());
        return new CoverViewHolder(coverView, coverClickListener, coverPlayButtonClickListener);
    }

    @Override
    public void onBindViewHolder(CoverViewHolder holder, int position) {
        holder.bind(getItemForPosition(position));
    }

    @Override
    public int getItemCount() {
        return coversData.size();
    }

    public CoverEntity getItemForPosition(int position) {
        return coversData.get(position);
    }

    static class CoverViewHolder extends RecyclerView.ViewHolder {

        private final CoverView coverView;
        private final ICoverClickListener coverClickListener;
        private final CoverView.OnPlayButtonClickListener coverPlayButtonClickListener;

        public CoverViewHolder(CoverView coverView,
                               ICoverClickListener coverClickListener,
                               CoverView.OnPlayButtonClickListener coverPlayButtonClickListener) {
            super(coverView);
            this.coverView = coverView;
            this.coverClickListener = coverClickListener;
            this.coverPlayButtonClickListener = coverPlayButtonClickListener;
        }

        void bind(final CoverEntity entityToBind) {
            coverView.bind(entityToBind);
            coverView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View coverView) {
                    coverClickListener.onCoverClick((CoverView) coverView, entityToBind);
                }
            });
            coverView.setOnPlayButtonClickListener(coverPlayButtonClickListener);
        }
    }

}
