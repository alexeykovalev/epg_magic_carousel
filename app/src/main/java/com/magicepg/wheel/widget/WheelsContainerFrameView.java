package com.magicepg.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.magicepg.R;
import com.magicepg.wheel.WheelAdapter;
import com.magicepg.wheel.WheelListener;
import com.magicepg.wheel.entity.WheelDataItem;
import com.magicepg.wheel.entity.WheelRotationDirection;
import com.magicepg.wheel.layout.AbstractWheelLayoutManager;
import com.magicepg.wheel.layout.BottomWheelLayoutManager;
import com.magicepg.wheel.layout.TopWheelLayoutManager;
import com.magicepg.util.DimensionUtils;
import com.magicepg.wheel.WheelComputationHelper;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Container holding wheel's top and bottom parts and their
 * initialization steps.
 *
 * @author Alexey Kovalev
 * @since 10.02.2017
 */
public final class WheelsContainerFrameView extends FrameLayout {

    // TODO: WheelOfFortune 01.03.2016 has to be constant from Device configuration class. Find the name of this class.
    private static final double TOUCH_SPOT_SIZE_FOR_CLICK_EVENT = 10.0;

    private final WheelComputationHelper computationHelper;

    @Bind(R.id.top_wheel_container)
    TopWheelContainerRecyclerView topWheelContainerView;

    @Bind(R.id.bottom_wheel_container)
    BottomWheelContainerRecyclerView bottomWheelContainerView;

    @Bind(R.id.wheel_decoration_frame)
    WheelSectorRaysDecorationFrameView wheelSectorsRaysDecorationFrame;

    /**
     * We use it as not recycler view item decoration because RecyclerView's
     * containers rotated in order to implement wheel startup animation.
     */
    private final WheelFrameCircleLinesDrawer wheelFrameCircleLinesDrawer;

    private int lastTouchAction;

    /**
     * For detecting taping on bottom wheel's sector.
     */
    private double previousTouchDownX;
    private double previousTouchDownY;

    public WheelsContainerFrameView(Context context) {
        this(context, null);
    }

    public WheelsContainerFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelsContainerFrameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        computationHelper = WheelComputationHelper.getInstance();
        inflateAndBindContainerView(context);

//        topWheelContainerView.showSettings(INVISIBLE);
//        bottomWheelContainerView.showSettings(INVISIBLE);

        wheelSectorsRaysDecorationFrame.setWheelContainers(topWheelContainerView, bottomWheelContainerView);

        wheelFrameCircleLinesDrawer = new WheelFrameCircleLinesDrawer(
                computationHelper, topWheelContainerView, bottomWheelContainerView
        );

        initBottomWheelContainer();
        initTopWheelContainer();
    }

    private void inflateAndBindContainerView(Context context) {
        inflate(context, R.layout.wheel_container_layout, this);
        ButterKnife.bind(this);
    }

    public void addWheelListener(WheelListener listener) {
        topWheelContainerView.addWheelListener(listener);
    }

    public void removeWheelListener(WheelListener listener) {
        topWheelContainerView.removeWheelListener(listener);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (topWheelContainerView.getLayoutManager().isStartupAnimationLayoutDone()
                && bottomWheelContainerView.getLayoutManager().isStartupAnimationLayoutDone()) {
            wheelFrameCircleLinesDrawer.drawWheelGradientedFrame(canvas);
        }
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
     * <p>
     * Bottom wheel will receive scroll notifications from MASTER via
     * {@link com.magicepg.wheel.layout.TopWheelLayoutManager.WheelOnScrollingCallback}
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

    public void swapDataAndRelayoutWheelsStartingFromPosition(List<WheelDataItem> newData, int startLayoutFromPosition) {
        layoutWheelContainersStartingFromPosition(startLayoutFromPosition);
        final List<WheelDataItem> unmodifiableNewData = Collections.unmodifiableList(newData);
        topWheelContainerView.getAdapter().swapData(unmodifiableNewData);
        bottomWheelContainerView.getAdapter().swapData(unmodifiableNewData);
//        wheelSectorsRaysDecorationFrame.invalidate();
    }

    /**
     * This method is entry point for displaying wheels on the screen.
     *
     * @param startingPosition - defines startup position from which wheel's sectors
     *                               and related to them data items will be layouted.
     */
    private void layoutWheelContainersStartingFromPosition(int startingPosition) {
        // This position is virtual in terms of relative shift from
        // {@link @WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT}
        // So for top wheel {@link @WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT} position
        // is treated as zero point - starting layout position.
        final int virtualPositionToStartLayout = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT + startingPosition;
        topWheelContainerView.getLayoutManager().setStartLayoutFromAdapterPosition(virtualPositionToStartLayout);
        bottomWheelContainerView.getLayoutManager().setStartLayoutFromAdapterPosition(virtualPositionToStartLayout - 1);
    }

    private void initTopWheelContainer() {
        final TopWheelLayoutManager topWheelLayoutManager = new TopWheelLayoutManager(
                getContext(), computationHelper,
                new AbstractWheelLayoutManager.WheelOnInitialLayoutFinishingListener() {
                    @Override
                    public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                        // uncomment it in case when bottom wheel part rendering depends on last adapter position
                        // layout by top wheel
//                        bottomWheelLayoutManager.setStartLayoutFromAdapterPosition(WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT - 1);

                        // don't call requestLayout() - cause wheels flickering behaviour
                        bottomWheelContainerView.onLayout(
                                false,
                                bottomWheelContainerView.getLeft(),
                                bottomWheelContainerView.getTop(),
                                bottomWheelContainerView.getRight(),
                                bottomWheelContainerView.getBottom()
                        );
                    }
                },
                new TopWheelLayoutManager.WheelOnScrollingCallback() {
                    @Override
                    public void onScrolledBy(int dy) {
                        bottomWheelContainerView.scrollBy(0, dy);
                    }
                }
        );

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
        final BottomWheelLayoutManager bottomWheelLayoutManager = new BottomWheelLayoutManager(getContext(), computationHelper, null);
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
//        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

    private void addBottomWheelItemDecorations(RecyclerView wheelContainerView) {
//        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getContext()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

    private WheelAdapter createEmptyWheelAdapter(WheelAdapter.OnWheelItemClickListener clickHandler) {
        return new WheelAdapter(getContext(), Collections.<WheelDataItem>emptyList(), clickHandler);
    }

    private static class WheelFrameCircleLinesDrawer {

        private static final int FRAME_LINE_COLOR = Color.GRAY;
        private static final int FRAME_LINE_THICKNESS_IN_DP = 20;

        /**
         * 0 - fully transparent.
         * 255 - Fully opaque.
         */
        private static final int FRAME_LINE_TRANSPARENCY = 170;

        private final WheelComputationHelper computationHelper;
        private final View topWheelView;
        private final View bottomWheelView;

        private final Paint drawingPaint;
        private final RectF innerCircleEmbracingSquare;
        private final RectF outerCircleEmbracingSquare;

        private final int wheelTopEdgeAngleInDegree;
        private final int wheelBottomEdgeAngleInDegree;
        private final int wheelFrameSweepAngleInDegree;

        WheelFrameCircleLinesDrawer(WheelComputationHelper computationHelper, View topWheelView, View bottomWheelView) {
            this.computationHelper = computationHelper;
            this.topWheelView = topWheelView;
            this.bottomWheelView = bottomWheelView;

            this.drawingPaint = createDrawingPaint();
            this.innerCircleEmbracingSquare = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                    this.computationHelper.getInnerCircleEmbracingSquareInCircleCoordsSystem()
            );
            this.outerCircleEmbracingSquare = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                    this.computationHelper.getOuterCircleEmbracingSquareInCircleCoordsSystem()
            );

            this.wheelTopEdgeAngleInDegree =
                    (int) WheelComputationHelper.radToDegree(this.computationHelper.getWheelLayoutStartAngleInRad());

            this.wheelBottomEdgeAngleInDegree = (int) WheelComputationHelper.radToDegree(
                    this.computationHelper.getWheelConfig().getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad()
            );
            this.wheelFrameSweepAngleInDegree = (wheelTopEdgeAngleInDegree - wheelBottomEdgeAngleInDegree) / 2;
        }

        private static Paint createDrawingPaint() {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(FRAME_LINE_COLOR);
            paint.setStrokeWidth(DimensionUtils.dpToPixels(FRAME_LINE_THICKNESS_IN_DP));
            paint.setAlpha(FRAME_LINE_TRANSPARENCY);
            // start at 0,0 and go to 0,max to use a vertical
            // gradient the full height of the screen.
            return paint;
        }

        void drawWheelGradientedFrame(Canvas canvas) {
            drawTopWheelDecorationFrame(canvas);
            drawBottomWheelDecorationFrame(canvas);
        }

        private void drawTopWheelDecorationFrame(Canvas canvas) {
            drawingPaint.setShader(new LinearGradient(0, 0, 0, topWheelView.getHeight() / 2 , FRAME_LINE_COLOR, Color.TRANSPARENT, Shader.TileMode.MIRROR));
            canvas.drawArc(innerCircleEmbracingSquare, 0, -wheelFrameSweepAngleInDegree, false, drawingPaint);
            canvas.drawArc(outerCircleEmbracingSquare, 0, -wheelFrameSweepAngleInDegree, false, drawingPaint);
        }

        private void drawBottomWheelDecorationFrame(Canvas canvas) {
            drawingPaint.setShader(new LinearGradient(0, 0, 0, bottomWheelView.getHeight() / 2 , FRAME_LINE_COLOR, Color.TRANSPARENT, Shader.TileMode.MIRROR));
            final int wheelBottomEdgeAngleInDegreeAbs = Math.abs(wheelBottomEdgeAngleInDegree);
            canvas.drawArc(innerCircleEmbracingSquare, 0, wheelBottomEdgeAngleInDegreeAbs, false, drawingPaint);
            canvas.drawArc(outerCircleEmbracingSquare, 0, wheelBottomEdgeAngleInDegreeAbs, false, drawingPaint);
        }
    }

}
