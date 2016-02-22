package com.sss.magicwheel.coversflow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sss.magicwheel.R;
import com.sss.magicwheel.coversflow.entity.CoverEntity;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 22.02.2016.
 */
public final class HorizontalCoversFlowFrameView extends FrameLayout {

    private RecyclerView coversFlowContainer;

    public HorizontalCoversFlowFrameView(Context context) {
        this(context, null);
    }

    public HorizontalCoversFlowFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalCoversFlowFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        coversFlowContainer.addItemDecoration(new HorizontalEdgesDecorator(context));
    }

    public void swapData(List<CoverEntity> coversData) {
        ((CoversFlowAdapter) coversFlowContainer.getAdapter()).swapData(coversData);
    }


    public void resizeCover() {
        final View firstCover = coversFlowContainer.getChildAt(1);

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
