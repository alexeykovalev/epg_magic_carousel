package com.sss.magicwheel.coversflow;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sss.magicwheel.coversflow.widget.HorizontalCoverView;

/**
 * @author Alexey Kovalev
 * @since 25.02.2016.
 */
public final class HorizontalSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int horizontalSpacing;

    public HorizontalSpacingItemDecoration(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        final int coverAdapterPosition = parent.getChildAdapterPosition(view);
        // take into account right offset fake view
        final boolean isLastItem = coverAdapterPosition == (parent.getAdapter().getItemCount() - 2);

        if ((view instanceof HorizontalCoverView) && !isLastItem) {
            outRect.set(0, 0, horizontalSpacing, 0);
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }
}
