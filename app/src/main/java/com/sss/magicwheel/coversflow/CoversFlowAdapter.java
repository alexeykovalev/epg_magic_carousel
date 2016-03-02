package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.R;
import com.sss.magicwheel.coversflow.entity.CoverEntity;
import com.sss.magicwheel.coversflow.widget.HorizontalCoverView;
import com.sss.magicwheel.coversflow.widget.IHorizontalCoverView;

import java.util.ArrayList;
import java.util.List;

/**
 * Data set adapter for {@link com.sss.magicwheel.coversflow.widget.HorizontalCoversFlowView}
 * container.
 *
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class CoversFlowAdapter extends RecyclerView.Adapter<CoversFlowAdapter.CoverViewHolder> {

    private static final int REGULAR_COVER_VIEW_TYPE = 0;
    private static final int OFFSET_COVER_VIEW_TYPE = 1;

    private final LayoutInflater inflater;
    private final List<CoverEntity> coversData;
    private final ICoverClickListener coverClickListener;

    public interface ICoverClickListener {
        void onCoverClick(HorizontalCoverView coverView, CoverEntity coverEntity);
    }

    public CoversFlowAdapter(Context context, List<CoverEntity> coversData, ICoverClickListener coverClickListener) {
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
    public int getItemViewType(int position) {
        return getItemForPosition(position).isOffsetItem() ? OFFSET_COVER_VIEW_TYPE : REGULAR_COVER_VIEW_TYPE;
    }

    @Override
    public CoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final IHorizontalCoverView resView;
        if (viewType == REGULAR_COVER_VIEW_TYPE) {
            resView = (HorizontalCoverView) inflater.inflate(R.layout.cover_item_layout, parent, false);
        } else if (viewType == OFFSET_COVER_VIEW_TYPE) {
            resView = (IHorizontalCoverView) inflater.inflate(R.layout.fake_cover_layout, parent, false);
        } else {
            throw new IllegalStateException("Unknown viewType [" + viewType + "]");
        }

        return new CoverViewHolder(resView, coverClickListener);
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
        private final ICoverClickListener coverClickListener;

        private static View asView(IHorizontalCoverView horizontalCoverView) {
            return (View) horizontalCoverView;
        }

        public CoverViewHolder(IHorizontalCoverView coverView, ICoverClickListener coverClickListener) {
            super(asView(coverView));
            this.coverView = coverView;
            this.coverClickListener = coverClickListener;
        }

        void bind(final CoverEntity entityToBind) {
            coverView.bind(entityToBind);
            if (!coverView.isOffsetCover()) {
                asView(coverView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View coverView) {
                        coverClickListener.onCoverClick((HorizontalCoverView) coverView, entityToBind);
                    }
                });
            }
        }
    }

}
