package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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
        final View coverView = inflater.inflate(R.layout.horizontal_cover_layout, parent, false);
        return new CoverViewHolder(context, coverView);
    }

    @Override
    public void onBindViewHolder(CoverViewHolder holder, int position) {
        holder.bind(getItemByPosition(position));
    }

    @Override
    public int getItemCount() {
        return coversData.size();
    }

    private CoverEntity getItemByPosition(int position) {
        return coversData.get(position);
    }

    static class CoverViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final ImageView coverImage;
        private final TextView coverTitle;

        public CoverViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            coverImage = (ImageView) itemView.findViewById(R.id.cover_image);
            coverTitle = (TextView) itemView.findViewById(R.id.cover_title);
        }

        void bind(CoverEntity entityToBind) {
            coverTitle.setText(entityToBind.getTitle());
            Picasso.with(context)
                    .load(entityToBind.getImageResource())
                    .resize(300, 300)
                    .into(coverImage);
        }
    }
}
