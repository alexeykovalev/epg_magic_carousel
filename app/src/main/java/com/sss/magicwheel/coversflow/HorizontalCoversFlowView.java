package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.coversflow.entity.CoverEntity;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class HorizontalCoversFlowView extends RecyclerView {


    private static class CoverZoomScrollListener extends OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            Log.e("TAG", "newState [" + newState + "]");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        }
    }

    public HorizontalCoversFlowView(Context context) {
        this(context, null);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        addOnScrollListener(new CoverZoomScrollListener());
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new CoversFlowAdapter(context, Collections.<CoverEntity>emptyList()));
        addItemDecoration(new HorizontalEdgesDecorator(context));
    }

    public void swapData(List<CoverEntity> coversData) {
        getAdapter().swapData(coversData);
    }

    @Override
    public CoversFlowAdapter getAdapter() {
        return (CoversFlowAdapter) super.getAdapter();
    }

    public void resizeCover() {
        final View firstCover = getChildAt(1);

        final ViewGroup.LayoutParams lp = firstCover.getLayoutParams();
        lp.width *= 2;
        lp.height *= 2;

        firstCover.setLayoutParams(lp);

//        firstCover.setPivotX(firstCover.getWidth() / 2);
//        firstCover.setPivotY(firstCover.getHeight() / 2);
//
//        firstCover.setScaleX(2);
//        firstCover.setScaleY(2);
    }
}
