package com.magicwheel.manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.magicwheel.entity.WheelRotationDirection;
import com.magicwheel.rotator.AbstractWheelRotator;
import com.magicwheel.util.WheelComputationHelper;
import com.magicwheel.util.WheelConfig;
import com.magicwheel.widget.WheelBigWrapperView;

import java.util.ArrayList;
import java.util.List;

/**
 * Base RecyclerView's layout manager implementation intended
 * for layout children like a sectors of the wheel.
 *
 * It does sectors measurements, positioning and rotation procedures required
 * for them proper layout on wheel path.
 *
 * @author Alexey Kovalev
 * @since 05.02.2017
 */
public abstract class AbstractWheelLayoutManager extends RecyclerView.LayoutManager {

    private static final int NOT_DEFINED_ADAPTER_POSITION = Integer.MAX_VALUE;
    private static final double NOT_DEFINED_ROTATION_ANGLE = Double.MIN_VALUE;


    public interface WheelOnInitialLayoutFinishingListener {
        void onInitialLayoutFinished(int finishedAtAdapterPosition);
    }

    public enum WheelStartupAnimationStatus {
        Start, InProgress, Finished
    }

    public interface WheelOnStartupAnimationListener {
        void onAnimationUpdate(WheelStartupAnimationStatus animationStatus);
    }


    protected final AbstractWheelRotator clockwiseRotator;
    protected final AbstractWheelRotator anticlockwiseRotator;

    protected final Context context;

    protected final WheelComputationHelper computationHelper;
    protected final WheelConfig wheelConfig;
    protected final WheelConfig.AngularRestrictions angularRestrictions;

    protected final WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener;
    private final List<WheelOnStartupAnimationListener> startupAnimationListeners = new ArrayList<>();

    private boolean isStartupAnimationLayoutDone;
    private boolean isStartupAnimationFinished;

    private double layoutStartAngleInRad;
    private double layoutEndAngleInRad;

    private int startLayoutFromAdapterPosition = NOT_DEFINED_ADAPTER_POSITION;

    public static LayoutParams getChildLayoutParams(View child) {
        return (LayoutParams) child.getLayoutParams();
    }

    /**
     * Available only for subclasses.
     */
    protected AbstractWheelLayoutManager(Context context,
                                         WheelComputationHelper computationHelper,
                                         WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {

        this.context = context;
        this.computationHelper = computationHelper;
        this.wheelConfig = computationHelper.getWheelConfig();
        this.angularRestrictions = wheelConfig.getAngularRestrictions();

        this.initialLayoutFinishingListener = stubIfNull(initialLayoutFinishingListener);

        this.layoutStartAngleInRad = computeLayoutStartAngleInRad();
        this.layoutEndAngleInRad = computeLayoutEndAngleInRad();

        this.clockwiseRotator = createClockwiseRotator();
        this.anticlockwiseRotator = createAnticlockwiseRotator();
    }

    private static WheelOnStartupAnimationListener stubIfNull(WheelOnStartupAnimationListener startupAnimationListener) {
        return startupAnimationListener != null ? startupAnimationListener :
                new WheelOnStartupAnimationListener() {
                    @Override
                    public void onAnimationUpdate(WheelStartupAnimationStatus animationStatus) {
                    }
                };
    }

    private static WheelOnInitialLayoutFinishingListener stubIfNull(WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        return initialLayoutFinishingListener != null ? initialLayoutFinishingListener :
                new WheelOnInitialLayoutFinishingListener() {
                    @Override
                    public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                    }
                };
    }

    public void addWheelStartupAnimationListener(WheelOnStartupAnimationListener animationListener) {
        startupAnimationListeners.add(stubIfNull(animationListener));
    }

    public void removeWheelStartupAnimationListener(WheelOnStartupAnimationListener animationListener) {
        startupAnimationListeners.remove(animationListener);
    }

    protected final void notifyOnAnimationUpdate(WheelStartupAnimationStatus animationStatus) {
        for (WheelOnStartupAnimationListener animationListener : startupAnimationListeners) {
            animationListener.onAnimationUpdate(animationStatus);
        }
    }

    /**
     * In case when wheel initially appears on screen we do layout for startup animation
     * via {@link #onLayoutChildrenForStartupAnimation(RecyclerView.Recycler, RecyclerView.State)}
     * and in all other cases we do regular children layout
     * via {@link #onLayoutChildrenRegular(RecyclerView.Recycler, RecyclerView.State)}
     */
    @Override
    public final void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        // We have nothing to show for an empty data set but clear any existing views
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        // sometimes during startup animation wheel begins randomly rotates with
        // intersecting top and bottom wheel parts.
        // Most probably it caused by requestLayout() when startup animation has not been finished yet.
        // So we just prohibit any layout requests until startup animation has not been fully finished.
        if (isStartupAnimationLayoutDone && !isStartupAnimationFinished) {
            return;
        }

        removeAndRecycleAllViews(recycler);

        if (getStartLayoutFromAdapterPosition() == NOT_DEFINED_ADAPTER_POSITION) {
            return;
        }

        final int lastlyLayoutedChildPos;
        if (isStartupAnimationLayoutDone) {
            lastlyLayoutedChildPos = onLayoutChildrenRegular(recycler, state);
            notifyLayoutFinishingListener(lastlyLayoutedChildPos + 1);
        } else {
            lastlyLayoutedChildPos = onLayoutChildrenForStartupAnimation(recycler, state);
            final Animator wheelStartupAnimator = createWheelStartupAnimator(recycler, state);
            wheelStartupAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isStartupAnimationFinished = true;
                }
            });
            wheelStartupAnimator.start();
            isStartupAnimationLayoutDone = true;
            notifyLayoutFinishingListener(lastlyLayoutedChildPos);
        }

    }

    protected abstract AbstractWheelRotator createClockwiseRotator();

    protected abstract AbstractWheelRotator createAnticlockwiseRotator();

    protected abstract double computeLayoutStartAngleInRad();

    protected abstract double computeLayoutEndAngleInRad();

    /**
     * Does children layout before launching startup wheel animation.
     * For startup animation we have to layout children in another way than
     * we do it for regular layout in {@link #onLayoutChildrenRegular(RecyclerView.Recycler, RecyclerView.State)}
     */
    protected abstract int onLayoutChildrenForStartupAnimation(RecyclerView.Recycler recycler, RecyclerView.State state);

    /**
     * Does regular children layout (not for startup animation). Usually this method will be invoked
     * when wheel adapter data set has been changed or request layout procedure has been issued.
     */
    protected abstract int onLayoutChildrenRegular(RecyclerView.Recycler recycler, RecyclerView.State state);

    protected abstract void notifyLayoutFinishingListener(int lastlyLayoutedChildPos);

    protected abstract Animator createWheelStartupAnimator(RecyclerView.Recycler recycler, RecyclerView.State state);

    public final boolean isStartupAnimationFinished() {
        return isStartupAnimationFinished;
    }

    public boolean isStartupAnimationLayoutDone() {
        return isStartupAnimationLayoutDone;
    }

    public final int getStartLayoutFromAdapterPosition() {
        return startLayoutFromAdapterPosition;
    }

    public final void setStartLayoutFromAdapterPosition(int startLayoutFromAdapterPosition) {
        this.startLayoutFromAdapterPosition = startLayoutFromAdapterPosition;
    }

    public View getChildClosestToLayoutStartEdge() {
        return getChildAt(0);
    }

    public View getChildClosestToLayoutEndEdge() {
        return getChildAt(getChildCount() - 1);
    }

    public final double getLayoutStartAngleInRad() {
        return layoutStartAngleInRad;
    }

    public final double getLayoutEndAngleInRad() {
        return layoutEndAngleInRad;
    }

    protected void setLayoutStartAngleInRad(double layoutStartAngleInRad) {
        this.layoutStartAngleInRad = layoutStartAngleInRad;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        removeAndRecycleAllViews(recycler);
        recycler.clear();
        startupAnimationListeners.clear();
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            // we cannot scroll if we don't have views
            return 0;
        }

        final double absRotationAngleInRad = Math.abs(computationHelper.fromTraveledDistanceToWheelRotationAngle(dy));

        final WheelRotationDirection rotationDirection = WheelRotationDirection.of(dy);
        rotateWheel(absRotationAngleInRad, rotationDirection, recycler, state);

        return dy;
    }

    private void rotateWheel(double rotationAngleInRad, WheelRotationDirection rotationDirection,
                             RecyclerView.Recycler recycler, RecyclerView.State state) {
        final AbstractWheelRotator wheelRotator = rotationDirection == WheelRotationDirection.Clockwise ?
                clockwiseRotator : anticlockwiseRotator;
        wheelRotator.rotateWheelBy(rotationAngleInRad);
        wheelRotator.recycleAndAddSectors(recycler, state);
    }


    // --- Due to the fact that we have infinite wheel this method for now don't required
    // they only needed for restricting scrolling ---
    /**
     * Anticlockwise rotation will correspond to positive return type.
     */
    private double computeRotationAngleInRadBasedOnCurrentState(int dy, RecyclerView.State state) {
        final WheelRotationDirection rotationDirection = WheelRotationDirection.of(dy);
        final double angleToRotate = computationHelper.fromTraveledDistanceToWheelRotationAngle(dy);

        return angleToRotate;

        /*return rotationDirection == WheelRotationDirection.Anticlockwise ?
                computeRotationAngleInRadForAnticlockwiseRotation(state, angleToRotate) :
                computeRotationAngleInRadForClockwiseRotation(angleToRotate);*/
    }

    private double computeRotationAngleInRadForAnticlockwiseRotation(RecyclerView.State state, double angleToRotate) {
        final View referenceChild = getChildClosestToLayoutEndEdge();
        final LayoutParams refChildLp = (LayoutParams) referenceChild.getLayoutParams();
        final int extraChildrenCount = state.getItemCount() - 1 - getPosition(referenceChild);
        final double lastSectorBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(refChildLp.anglePositionInRad);

        double res = NOT_DEFINED_ROTATION_ANGLE;

        // compute available space
        if (extraChildrenCount == 0) { // is last child
            // if last sector's bottom edge outside bottom limit - only scroll this extra space
            // TODO: WheelOfFortune 15.12.2016 replace with isBottomBoundsReached()
            if (wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge > 0) {
                res = Math.min(
                        angleToRotate,
                        wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge
                );
            }
        } else if (extraChildrenCount > 0) {
            res = Math.min(angleToRotate, wheelConfig.getAngularRestrictions().getSectorAngleInRad() * extraChildrenCount);
        }

        return res;
    }

    private double computeRotationAngleInRadForClockwiseRotation(double angleToRotate) {
        final View referenceChild = getChildClosestToLayoutStartEdge();
        final LayoutParams refChildLp = (LayoutParams) referenceChild.getLayoutParams();
        final int extraChildrenCount = getPosition(referenceChild);
        final double firstSectorTopEdge = refChildLp.anglePositionInRad;

        double res = NOT_DEFINED_ROTATION_ANGLE;

        // first top sector goes outside top edge
        if (extraChildrenCount == 0) {
            if (firstSectorTopEdge - computationHelper.getWheelLayoutStartAngleInRad() > 0) {
                res = Math.min(
                        angleToRotate,
                        firstSectorTopEdge - computationHelper.getWheelLayoutStartAngleInRad()
                );
            }
        } else if (extraChildrenCount > 0) {
            res = Math.min(angleToRotate, wheelConfig.getAngularRestrictions().getSectorAngleInRad() * extraChildrenCount);
        }

        return res;
    }


    public final void setupSectorForPosition(RecyclerView.Recycler recycler,
                                             int positionIndex, double angularPositionInRad,
                                             boolean isAddViewToBottom) {

        final WheelBigWrapperView bigWrapperView = (WheelBigWrapperView) recycler.getViewForPosition(positionIndex);
        measureBigWrapperView(bigWrapperView);

        RectF wrViewCoordsInCircleSystem = computationHelper.getBigWrapperViewCoordsInCircleSystem(bigWrapperView.getMeasuredWidth());
        RectF wrTransformedCoords = WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                wrViewCoordsInCircleSystem
        );

        bigWrapperView.layout(
                (int) wrTransformedCoords.left, (int) wrTransformedCoords.top,
                (int) wrTransformedCoords.right, (int) wrTransformedCoords.bottom
        );

        alignBigWrapperViewByAngle(bigWrapperView, -angularPositionInRad);

        LayoutParams lp = (LayoutParams) bigWrapperView.getLayoutParams();
        lp.anglePositionInRad = angularPositionInRad;

        if (isAddViewToBottom) {
            addView(bigWrapperView);
        } else {
            addView(bigWrapperView, 0);
        }
    }

    private void measureBigWrapperView(View bigWrapperView) {
        final int viewWidth = computationHelper.getBigWrapperViewMeasurements().getWidth();
        final int viewHeight = computationHelper.getBigWrapperViewMeasurements().getHeight();

        final int childWidthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
        final int childHeightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
        bigWrapperView.measure(childWidthSpec, childHeightSpec);
    }

    public final void alignBigWrapperViewByAngle(View bigWrapperView, double angleAlignToInRad) {
        bigWrapperView.setTranslationX(wheelConfig.getInnerRadius());
        bigWrapperView.setPivotX(-wheelConfig.getInnerRadius());
        bigWrapperView.setPivotY(bigWrapperView.getMeasuredHeight() / 2);
        float angleInDegree = (float) WheelComputationHelper.radToDegree(angleAlignToInRad);

        // TODO: WheelOfFortune 16.12.2016 ugly bug fix related to central view disappearing while scrolling
//        if (angleInDegree > -0.1f && angleInDegree < 0.1f) {
//            angleInDegree = 0;
//        }

        bigWrapperView.setRotation(angleInDegree);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public void scrollToPosition(int positionToScroll) {
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//        throw new UnsupportedOperationException("Not implemented feature yet.");

        final double targetSeekScrollDistanceInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 10;
        final WheelSmoothScroller wheelScroller = new WheelSmoothScroller(recyclerView.getContext(),
                this, computationHelper, targetSeekScrollDistanceInRad) {
            @Override
            protected WheelRotationDirection computeRotationDirectionForPosition(int targetPosition) {
                // detect rotation direction
                final int referenceSectorViewVirtualAdapterPosition = getPosition(getChildClosestToLayoutEndEdge());
                return targetPosition < referenceSectorViewVirtualAdapterPosition ?
                        WheelRotationDirection.Clockwise : WheelRotationDirection.Anticlockwise;
            }
        };
        wheelScroller.setTargetPosition(position);
        startSmoothScroll(wheelScroller);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    public static final class LayoutParams extends RecyclerView.LayoutParams {

        /**
         * Defines middle (sector's wrapper view half height) edge sector's position on circle.
         * Effectively it equals to view's rotation angle.
         */
        public double anglePositionInRad;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
        }
    }

}
