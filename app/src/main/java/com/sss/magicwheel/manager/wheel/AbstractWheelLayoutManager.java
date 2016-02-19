package com.sss.magicwheel.manager.wheel;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelRotationDirection;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.rotator.AbstractWheelRotator;
import com.sss.magicwheel.manager.rotator.AnticlockwiseWheelRotator;
import com.sss.magicwheel.manager.rotator.ClockwiseWheelRotator;
import com.sss.magicwheel.manager.widget.WheelBigWrapperView;
import com.sss.magicwheel.manager.widget.AbstractWheelContainerRecyclerView;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Kovalev
 * @since 05.02.2016.
 */
public abstract class AbstractWheelLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = AbstractWheelLayoutManager.class.getCanonicalName();
    private static final double NOT_DEFINED_ROTATION_ANGLE = Double.MIN_VALUE;

    public static final int NOT_DEFINED_ADAPTER_POSITION = Integer.MAX_VALUE;
//    public static final int START_LAYOUT_FROM_ADAPTER_POSITION = WheelAdapter.MIDDLE_VIRTUAL_ITEMS_COUNT;

    protected final AbstractWheelRotator clockwiseRotator;
    protected final AbstractWheelRotator anticlockwiseRotator;

    protected final Context context;
    protected final AbstractWheelContainerRecyclerView wheelRecyclerView;

    protected final WheelConfig wheelConfig;
    protected final WheelConfig.AngularRestrictions angularRestrictions;
    protected final WheelComputationHelper computationHelper;

    protected final WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener;
    protected final WheelOnStartupAnimationListener startupAnimationListener;

    private boolean isStartupAnimationPlayed;

    private double layoutStartAngleInRad;
    private double layoutEndAngleInRad;

    private int startLayoutFromAdapterPosition = NOT_DEFINED_ADAPTER_POSITION;

    public static LayoutParams getChildLayoutParams(View child) {
        return (LayoutParams) child.getLayoutParams();
    }

    public interface WheelOnInitialLayoutFinishingListener {
        void onInitialLayoutFinished(int finishedAtAdapterPosition);
    }

    public enum WheelStartupAnimationStatus {
        Start, InProgress, Finished
    }

    public interface WheelOnStartupAnimationListener {
        void onAnimationUpdate(WheelStartupAnimationStatus animationStatus);
    }

    protected AbstractWheelLayoutManager(Context context,
                                         AbstractWheelContainerRecyclerView wheelRecyclerView,
                                         WheelComputationHelper computationHelper,
                                         WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener,
                                         WheelOnStartupAnimationListener startupAnimationListener) {

        this.context = context;
        this.computationHelper = computationHelper;
        this.wheelRecyclerView = wheelRecyclerView;
        this.wheelConfig = computationHelper.getWheelConfig();
        this.angularRestrictions = wheelConfig.getAngularRestrictions();

        this.initialLayoutFinishingListener = stubIfNull(initialLayoutFinishingListener);
        this.startupAnimationListener = stubIfNull(startupAnimationListener);

        this.layoutStartAngleInRad = computeLayoutStartAngleInRad();
        this.layoutEndAngleInRad = computeLayoutEndAngleInRad();

        this.clockwiseRotator = new ClockwiseWheelRotator(this, computationHelper);
        this.anticlockwiseRotator = new AnticlockwiseWheelRotator(this, computationHelper);
    }

    private WheelOnStartupAnimationListener stubIfNull(WheelOnStartupAnimationListener startupAnimationListener) {
        return startupAnimationListener != null ? startupAnimationListener :
                new WheelOnStartupAnimationListener() {
                    @Override
                    public void onAnimationUpdate(WheelStartupAnimationStatus animationStatus) {
                    }
                };
    }

    private WheelOnInitialLayoutFinishingListener stubIfNull(WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {
        return initialLayoutFinishingListener != null ? initialLayoutFinishingListener :
                new WheelOnInitialLayoutFinishingListener() {
                    @Override
                    public void onInitialLayoutFinished(int finishedAtAdapterPosition) {
                    }
                };
    }

    @Override
    public final void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        // We have nothing to show for an empty data set but clear any existing views
        int itemCount = getItemCount();
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        removeAndRecycleAllViews(recycler);

        if (getStartLayoutFromAdapterPosition() == NOT_DEFINED_ADAPTER_POSITION) {
            return;
        }

        final int lastlyLayoutedChildPos;
        if (isStartupAnimationPlayed) {
            lastlyLayoutedChildPos = onLayoutChildrenRegular(recycler, state);
        } else {
            lastlyLayoutedChildPos = onLayoutChildrenForStartupAnimation(recycler, state);
            createWheelStartupAnimator().start();
            isStartupAnimationPlayed = true;
        }

        notifyLayoutFinishingListener(lastlyLayoutedChildPos);
    }

    protected abstract int onLayoutChildrenForStartupAnimation(RecyclerView.Recycler recycler, RecyclerView.State state);
    protected abstract int onLayoutChildrenRegular(RecyclerView.Recycler recycler, RecyclerView.State state);

    protected abstract void notifyLayoutFinishingListener(int lastlyLayoutedChildPos);

    public abstract Animator createWheelStartupAnimator();

    protected abstract double computeLayoutStartAngleInRad();
    protected abstract double computeLayoutEndAngleInRad();


    // TODO: 05.02.2016 consider removing overriding
    protected int getStartLayoutFromAdapterPosition() {
        return startLayoutFromAdapterPosition;
    }

    public void setStartLayoutFromAdapterPosition(int startLayoutFromAdapterPosition) {
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

    protected void setLayoutEndAngleInRad(double layoutEndAngleInRad) {
        this.layoutEndAngleInRad = layoutEndAngleInRad;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        removeAndRecycleAllViews(recycler);
        recycler.clear();
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

    @Override
    public void scrollToPosition(int positionToScroll) {
        throw new UnsupportedOperationException("Not implemented feature yet.");
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final double targetSeekScrollDistanceInRad = wheelConfig.getAngularRestrictions().getSectorAngleInRad() / 10;
        final WheelSmoothScroller wheelScroller = new WheelSmoothScroller(recyclerView.getContext(),
                this, computationHelper, targetSeekScrollDistanceInRad) {
            @Override
            protected WheelRotationDirection computeRotationDirectionForPosition(int targetPosition) {
                return AbstractWheelLayoutManager.this.detectRotationDirection(targetPosition);
            }
        };
        wheelScroller.setTargetPosition(position);
        startSmoothScroll(wheelScroller);
    }

    private WheelRotationDirection detectRotationDirection(int targetPosition) {
        final int referenceSectorViewVirtualAdapterPosition = getPosition(getChildClosestToLayoutEndEdge());
        return targetPosition < referenceSectorViewVirtualAdapterPosition ?
                WheelRotationDirection.Clockwise : WheelRotationDirection.Anticlockwise;
    }

    // for y: use -1 for up direction, 1 for down direction.
    @Deprecated
    private PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos ? -1 : 1;

        return new PointF(0, direction);
    }

    private void rotateWheel(double rotationAngleInRad, WheelRotationDirection rotationDirection,
                             RecyclerView.Recycler recycler, RecyclerView.State state) {
        final AbstractWheelRotator wheelRotator = resolveRotatorByDirection(rotationDirection);
        wheelRotator.rotateWheelBy(rotationAngleInRad);
        wheelRotator.recycleAndAddSectors(recycler, state);
    }

    private AbstractWheelRotator resolveRotatorByDirection(WheelRotationDirection rotationDirection) {
        return rotationDirection == WheelRotationDirection.Clockwise ? clockwiseRotator : anticlockwiseRotator;
    }

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
        final View referenceChild = getChildClosestToBottom();
        final LayoutParams refChildLp = (LayoutParams) referenceChild.getLayoutParams();
        final int extraChildrenCount = state.getItemCount() - 1 - getPosition(referenceChild);
        final double lastSectorBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(refChildLp.anglePositionInRad);

        double res = NOT_DEFINED_ROTATION_ANGLE;

        // compute available space
        if (extraChildrenCount == 0) { // is last child
            // if last sector's bottom edge outside bottom limit - only scroll this extra space
            // TODO: 15.12.2015 replace with isBottomBoundsReached()
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
        final View referenceChild = getChildClosestToTop();
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

        /*Log.e(TAG,
                "setupSectorForPosition() add viewTitle [" + getBigWrapperTitle(bigWrapperView) + "], " +
                        "angleInRad [" + WheelComputationHelper.radToDegree(lp.anglePositionInRad) + "]"
        );*/

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
        bigWrapperView.setPivotX(0);
        bigWrapperView.setPivotY(bigWrapperView.getMeasuredHeight() / 2);
        float angleInDegree = (float) WheelComputationHelper.radToDegree(angleAlignToInRad);

        // TODO: 16.12.2015 ugly bug fix related to central view disappearing while scrolling
//        if (angleInDegree > -0.1f && angleInDegree < 0.1f) {
//            angleInDegree = 0;
//        }

        bigWrapperView.setRotation(angleInDegree);

//        final String text = ((WheelBigWrapperView) bigWrapperView).getText();
//        Log.e(TAG, "alignBigWrapperViewByAngle text [" + text + "], angleInDegree [" + angleInDegree + "]");
    }

    @Deprecated
    private boolean isBottomBoundsReached() {
        View lastChild = getChildClosestToBottom();
        LayoutParams lastChildLp = (LayoutParams) lastChild.getLayoutParams();
        final double lastSectorBottomEdge = computationHelper.getSectorAngleBottomEdgeInRad(lastChildLp.anglePositionInRad);

        return wheelConfig.getAngularRestrictions().getWheelBottomEdgeAngleRestrictionInRad() - lastSectorBottomEdge <= 0;
    }

    @Deprecated
    public View getChildClosestToBottom() {
        return getChildClosestToLayoutEndEdge();
    }

    @Deprecated
    public View getChildClosestToTop() {
        return getChildClosestToLayoutStartEdge();
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
