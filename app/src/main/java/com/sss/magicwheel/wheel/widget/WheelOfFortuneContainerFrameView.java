package com.sss.magicwheel.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.sss.magicwheel.R;
import com.sss.magicwheel.wheel.entity.WheelDataItem;
import com.sss.magicwheel.wheel.entity.WheelRotationDirection;
import com.sss.magicwheel.wheel.WheelAdapter;
import com.sss.magicwheel.wheel.misc.WheelComputationHelper;
import com.sss.magicwheel.wheel.decor.WheelFrameItemDecoration;
import com.sss.magicwheel.wheel.manager.AbstractWheelLayoutManager;
import com.sss.magicwheel.wheel.manager.BottomWheelLayoutManager;
import com.sss.magicwheel.wheel.manager.TopWheelLayoutManager;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.*;

/**
 * Container holding wheel's top and bottom parts and their
 * initialization steps.
 *
 * @author Alexey Kovalev
 * @since 10.02.2016.
 */
public final class WheelOfFortuneContainerFrameView extends FrameLayout {

    // TODO: 01.03.2016 has to be constant from Device configuration class. Find the name of this class.
    private static final double TOUCH_SPOT_SIZE_FOR_CLICK_EVENT = 10.0;

    private final WheelComputationHelper computationHelper;

    private BottomWheelLayoutManager bottomWheelLayoutManager;

    private TopWheelContainerRecyclerView topWheelContainerView;
    private BottomWheelContainerRecyclerView bottomWheelContainerView;

    private int lastTouchAction;

    private WheelSectorRaysDecorationFrameView wheelSectorsRaysDecorationFrame;

    /**
     * We use it as not recycler view item decoration because RecyclerView's
     * containers rotated in order to implement wheel startup animation.
     */
    private final WheelFrameItemDecoration wheelFrameItemDecoration;

    /**
     * For detecting taping on bottom wheel's sector.
     */
    private double previousTouchDownX;
    private double previousTouchDownY;

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

//        topWheelContainerView.setVisibility(INVISIBLE);
//        bottomWheelContainerView.setVisibility(INVISIBLE);

        wheelSectorsRaysDecorationFrame.setWheelContainers(topWheelContainerView, bottomWheelContainerView);
        wheelFrameItemDecoration = new WheelFrameItemDecoration(getContext());
    }

    private void inflateAndBindContainerView(Context context) {
        inflate(context, R.layout.wheel_container_layout, this);
        topWheelContainerView = (TopWheelContainerRecyclerView) findViewById(R.id.top_wheel_container);
        bottomWheelContainerView = (BottomWheelContainerRecyclerView) findViewById(R.id.bottom_wheel_container);
        wheelSectorsRaysDecorationFrame = (WheelSectorRaysDecorationFrameView) findViewById(R.id.wheel_decoration_frame);
    }

    public void initWheelContainers(int virtualStartupPosition) {
        initBottomWheelContainer();
        initTopWheelContainer(virtualStartupPosition);
    }

    public void addWheelListener(WheelListener listener) {
        topWheelContainerView.addWheelListener(listener);
    }

    public void removeWheelListener(WheelListener listener) {
        topWheelContainerView.removeWheelListener(listener);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        wheelFrameItemDecoration.onDraw(canvas, null, null);
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * We dispatch touch event only to the top wheel. This wheel will
     * play role of MASTER in MASTER-SLAVE couple where SLAVE would
     * be bottom wheel.
     * <p/>
     * Bottom wheel will receive scroll notifications from MASTER via
     * {@link com.sss.magicwheel.wheel.manager.TopWheelLayoutManager.WheelOnScrollingCallback}
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // eat touch event if startup animation has not been finished yet
        if (!topWheelContainerView.getLayoutManager().isStartupAnimationFinished()
                || !bottomWheelContainerView.getLayoutManager().isStartupAnimationFinished()) {
            return true;
        }

        // in order to dispatch click event to sectorView inside bottom wheel
        final int actionMasked = MotionEventCompat.getActionMasked(event);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                bottomWheelContainerView.dispatchTouchEvent(event);
                previousTouchDownX = event.getX();
                previousTouchDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                final double touchSpotSize = computeTouchSpotSize(event);
                if (lastTouchAction != MotionEvent.ACTION_MOVE || (touchSpotSize <= TOUCH_SPOT_SIZE_FOR_CLICK_EVENT)) {
                    bottomWheelContainerView.dispatchTouchEvent(event);
                }
                break;
        }

        lastTouchAction = actionMasked;
        topWheelContainerView.dispatchTouchEvent(event);
        return true;
    }

    private double computeTouchSpotSize(MotionEvent currentTouchEvent) {
        final double deltaXAbs = abs(currentTouchEvent.getX() - previousTouchDownX);
        final double deltaYAbs = abs(currentTouchEvent.getY() - previousTouchDownY);
        return sqrt(deltaXAbs * deltaXAbs + deltaYAbs * deltaYAbs);
    }

    public void swapData(List<WheelDataItem> newData) {
        final List<WheelDataItem> unmodifiableNewData = Collections.unmodifiableList(newData);
        topWheelContainerView.getAdapter().swapData(unmodifiableNewData);
        bottomWheelContainerView.getAdapter().swapData(unmodifiableNewData);
//        wheelSectorsRaysDecorationFrame.invalidate();
    }

    private void initTopWheelContainer(int layoutStartAdapterPosition) {
        final TopWheelLayoutManager topWheelLayoutManager = new TopWheelLayoutManager(
                getContext(), computationHelper,
                new AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener() {
                    @Override
                    public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                        bottomWheelLayoutManager.setStartLayoutFromAdapterPosition(finishedAtAdapterPosition);
                        bottomWheelLayoutManager.requestLayout();
                    }
                },
                new TopWheelLayoutManager.WheelOnScrollingCallback() {
                    @Override
                    public void onScrolledBy(int dy) {
                        bottomWheelContainerView.scrollBy(0, dy);
                    }
                }
        );
        topWheelLayoutManager.setStartLayoutFromAdapterPosition(layoutStartAdapterPosition);

        topWheelContainerView.setLayoutManager(topWheelLayoutManager);
        topWheelContainerView.setAdapter(createEmptyWheelAdapter(new WheelAdapter.OnWheelItemClickListener() {
            @Override
            public void onItemClicked(View clickedSectorView) {
                topWheelContainerView.handleTapOnSectorView(clickedSectorView);
            }
        }));
        topWheelContainerView.setIsCutGapAreaActivated(true);

        addTopWheelItemDecorations(topWheelContainerView);
    }

    private void initBottomWheelContainer() {
        bottomWheelLayoutManager = new BottomWheelLayoutManager(getContext(), computationHelper, null);
        bottomWheelLayoutManager.addWheelStartupAnimationListener(new AbstractWheelLayoutManager.WheelOnStartupAnimationListener() {
            @Override
            public void onAnimationUpdate(AbstractWheelLayoutManager.WheelStartupAnimationStatus animationStatus) {
                if (animationStatus == AbstractWheelLayoutManager.WheelStartupAnimationStatus.Finished) {
                    bottomWheelContainerView.setIsCutGapAreaActivated(true);
                }
            }
        });
        bottomWheelLayoutManager.addWheelStartupAnimationListener(new AbstractWheelLayoutManager.WheelOnStartupAnimationListener() {
            @Override
            public void onAnimationUpdate(AbstractWheelLayoutManager.WheelStartupAnimationStatus animationStatus) {
                if (animationStatus == AbstractWheelLayoutManager.WheelStartupAnimationStatus.InProgress) {
                    wheelSectorsRaysDecorationFrame.invalidate();
                }
            }
        });
        bottomWheelContainerView.setBottomWheelSectorTapListener(new BottomWheelContainerRecyclerView.OnBottomWheelSectorTapListener() {
            @Override
            public void onRotateWheelByAngle(double rotationAngleInRad) {
                // dispatch rotation to top wheel because it's MASTER and bottom
                // wheel will be rotated automatically because it's SLAVE
                topWheelContainerView.smoothRotateWheelByAngleInRad(rotationAngleInRad, WheelRotationDirection.Anticlockwise);
            }
        });

        bottomWheelContainerView.setLayoutManager(bottomWheelLayoutManager);
        bottomWheelContainerView.setAdapter(createEmptyWheelAdapter(new WheelAdapter.OnWheelItemClickListener() {
            @Override
            public void onItemClicked(View clickedSectorView) {
                bottomWheelContainerView.handleTapOnSectorView(clickedSectorView);
            }
        }));

        addBottomWheelItemDecorations(bottomWheelContainerView);
    }

    private void addTopWheelItemDecorations(RecyclerView wheelContainerView) {
//        wheelContainerView.addItemDecoration(new WheelFrameItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

    private void addBottomWheelItemDecorations(RecyclerView wheelContainerView) {
//        wheelContainerView.addItemDecoration(new WheelFrameItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

    private WheelAdapter createEmptyWheelAdapter(WheelAdapter.OnWheelItemClickListener clickHandler) {
        return new WheelAdapter(getContext(), Collections.<WheelDataItem>emptyList(), clickHandler);
    }

}
