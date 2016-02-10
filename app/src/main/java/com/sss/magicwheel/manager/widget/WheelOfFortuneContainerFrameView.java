package com.sss.magicwheel.manager.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.sss.magicwheel.R;
import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.decor.WheelFrameItemDecoration;
import com.sss.magicwheel.manager.wheel.AbstractWheelLayoutManager;
import com.sss.magicwheel.manager.wheel.BottomWheelLayoutManager;
import com.sss.magicwheel.manager.wheel.TopWheelLayoutManager;

import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 10.02.2016.
 */
public final class WheelOfFortuneContainerFrameView extends FrameLayout {

    private WheelComputationHelper computationHelper;

    private WheelContainerRecyclerView topWheelContainer;
    private WheelContainerRecyclerView bottomWheelContainer;
    private BottomWheelLayoutManager bottomWheelLayoutManager;

//    private AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener bottomWheelInitialLayoutFinishingListener;

    public WheelOfFortuneContainerFrameView(Context context) {
        this(context, null);
    }

    public WheelOfFortuneContainerFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelOfFortuneContainerFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        computationHelper = WheelComputationHelper.getInstance();
        inflateAndBindContainerView(context);

        initBottomWheelContainer(bottomWheelContainer);
        initTopWheelContainer(topWheelContainer);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        topWheelContainer.dispatchTouchEvent(event);
        bottomWheelContainer.dispatchTouchEvent(event);
        return true;
    }

    public void swapData(List<WheelDataItem> newData) {
//        ensureListenerIsSet();
        final List<WheelDataItem> unmodifiableNewData = Collections.unmodifiableList(newData);
        topWheelContainer.getAdapter().swapData(unmodifiableNewData);
        bottomWheelContainer.getAdapter().swapData(unmodifiableNewData);
    }

    /*private void ensureListenerIsSet() {
        if (bottomWheelInitialLayoutFinishingListener == null) {
            throw new IllegalStateException("You have to set bottom wheel initial layout finishing " +
                    "listener before data swapping.");
        }
    }

    public void setBottomWheelInitialLayoutFinishingListener(AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener bottomWheelInitialLayoutFinishingListener) {
        if (bottomWheelInitialLayoutFinishingListener == null) {
            throw new IllegalArgumentException("Listener has to be NOT NULL.");
        }
        this.bottomWheelInitialLayoutFinishingListener = bottomWheelInitialLayoutFinishingListener;
    }*/

    private void inflateAndBindContainerView(Context context) {
        inflate(context, R.layout.wheel_container_layout, this);
        topWheelContainer = (WheelContainerRecyclerView) findViewById(R.id.top_wheel_container);
        bottomWheelContainer = (WheelContainerRecyclerView) findViewById(R.id.bottom_wheel_container);
    }

    private void initTopWheelContainer(RecyclerView topWheelContainerView) {
        topWheelContainerView.setLayoutManager(new TopWheelLayoutManager(getContext(), computationHelper,
                new AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener() {
            @Override
            public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                    bottomWheelLayoutManager.setStartLayoutFromAdapterPosition(finishedAtAdapterPosition);
            }
        }));
        topWheelContainerView.setAdapter(createEmptyWheelAdapter());
        addWheelItemDecorations(topWheelContainerView);
    }

    private void initBottomWheelContainer(RecyclerView topWheelContainerView) {
        bottomWheelLayoutManager = new BottomWheelLayoutManager(getContext(), computationHelper,
                new AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener() {
            @Override
            public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                startWheelAppearingAnimation();
            }
        });
        topWheelContainerView.setLayoutManager(bottomWheelLayoutManager);
        topWheelContainerView.setAdapter(createEmptyWheelAdapter());
        addWheelItemDecorations(topWheelContainerView);
    }

    private void startWheelAppearingAnimation() {

    }

    private void addWheelItemDecorations(RecyclerView wheelContainerView) {
        wheelContainerView.addItemDecoration(new WheelFrameItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getActivity()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

    private WheelAdapter createEmptyWheelAdapter() {
        return new WheelAdapter(getContext(), Collections.<WheelDataItem>emptyList(), new WheelAdapter.OnWheelItemClickListener() {
            @Override
            public void onItemClicked(View clickedSectorView, WheelDataItem dataItem) {
                topWheelContainer.handleTapOnSectorView(clickedSectorView);
//                topWheelContainer.smoothlySelectDataItem(dataItem);
            }
        });
    }

}
