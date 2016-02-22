package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sss.magicwheel.R;
import com.sss.magicwheel.coversflow.entity.CoverEntity;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class HorizontalCoversFlowView extends FrameLayout {

    private RecyclerView coversFlowContainer;

    public HorizontalCoversFlowView(Context context) {
        this(context, null);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoversFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateAndBind(context);

        initCoversContainer(context);
    }

    private void inflateAndBind(Context context) {
        inflate(context, R.layout.horizontal_covers_flow_layout, this);
        coversFlowContainer = (RecyclerView) findViewById(R.id.covers_flow_container);
    }

    private void initCoversContainer(Context context) {
        coversFlowContainer.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        coversFlowContainer.setAdapter(new CoversFlowAdapter(context, Collections.<CoverEntity>emptyList()));
    }

    public void swapData(List<CoverEntity> coversData) {
        ((CoversFlowAdapter) coversFlowContainer.getAdapter()).swapData(coversData);
    }


}
