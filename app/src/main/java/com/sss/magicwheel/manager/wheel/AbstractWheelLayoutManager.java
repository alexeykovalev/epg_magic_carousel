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
import com.sss.magicwheel.manager.widget.WheelContainerRecyclerView;

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

    private static final boolean IS_LOG_ACTIVATED = true;
    private static final boolean IS_FILTER_LOG_BY_METHOD_NAME = true;

    private static final Set<String> ALLOWED_METHOD_NAMES = new HashSet<>();

    static {
        ALLOWED_METHOD_NAMES.add("scrollVerticallyBy");
//        ALLOWED_METHOD_NAMES.add("onLayoutChildren");
    }

    protected final AbstractWheelRotator clockwiseRotator;
    protected final AbstractWheelRotator anticlockwiseRotator;

    private static boolean isMessageContainsAllowedMethod(String logMessage) {
        if (logMessage == null || logMessage.isEmpty()) {
            return false;
        }
        for (String methodName : ALLOWED_METHOD_NAMES) {
            if (logMessage.contains(methodName)) {
                return true;
            }
        }
        return false;
    }

    protected final Context context;
    protected final WheelContainerRecyclerView wheelRecyclerView;

    protected final WheelConfig wheelConfig;
    protected final WheelConfig.AngularRestrictions angularRestrictions;
    protected final WheelComputationHelper computationHelper;

    protected final WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener;

    private boolean isStartupAnimationPlayed;

    private double layoutStartAngleInRad;
    private double layoutEndAngleInRad;

    private int startLayoutFromAdapterPosition = NOT_DEFINED_ADAPTER_POSITION;

    public static LayoutParams getChildLayoutParams(View child) {
        return (LayoutParams) child.getLayoutParams();
    }

    // TODO: 04.02.2016 for testing purposes
    public static String getBigWrapperTitle(View bigWrapperView) {
        return ((WheelBigWrapperView) bigWrapperView).getTitle();
    }

    public interface WheelOnInitialLayoutFinishingListener {
        void onInitialLayoutFinished(int finishedAtAdapterPosition);
    }

    protected AbstractWheelLayoutManager(Context context,
                                         WheelContainerRecyclerView wheelRecyclerView,
                                         WheelComputationHelper computationHelper,
                                         WheelOnInitialLayoutFinishingListener initialLayoutFinishingListener) {

        this.context = context;
        this.computationHelper = computationHelper;
        this.wheelRecyclerView = wheelRecyclerView;
        this.wheelConfig = computationHelper.getWheelConfig();
        this.angularRestrictions = wheelConfig.getAngularRestrictions();

        this.initialLayoutFinishingListener = initialLayoutFinishingListener;

        this.layoutStartAngleInRad = computeLayoutStartAngleInRad();
        this.layoutEndAngleInRad = computeLayoutEndAngleInRad();

        this.clockwiseRotator = new ClockwiseWheelRotator(this, computationHelper);
        this.anticlockwiseRotator = new AnticlockwiseWheelRotator(this, computationHelper);
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

//        final int normalizedDy = dy % getOuterDiameter();
//        final double absRotationAngleInRad = computeRotationAngleInRadBasedOnCurrentState(normalizedDy, state);

        final double absRotationAngleInRad = Math.abs(fromTraveledDistanceToWheelRotationAngle(dy));

        Log.e(WheelSmoothScroller.TAG, "AbstractWheelLayoutManager " +
                "absRotationAngleInRad [" + WheelComputationHelper.radToDegree(absRotationAngleInRad) + "]");

        if (absRotationAngleInRad == NOT_DEFINED_ROTATION_ANGLE) {
            Log.i(TAG, "HIT INTO NOT_DEFINED_ROTATION_ANGLE");
            return 0;
        }

        final WheelRotationDirection rotationDirection = WheelRotationDirection.of(dy);
        rotateWheel(absRotationAngleInRad, rotationDirection, recycler, state);

        /*
        final int resultSwipeDistanceAbs = (int) Math.round(fromWheelRotationAngleToTraveledDistance(absRotationAngleInRad));
        logI(
                "scrollVerticallyBy() " +
                        "dy [" + dy + "], " +
                        "normalizedDy [" + normalizedDy + "], " +
                        "resultSwipeDistanceAbs [" + resultSwipeDistanceAbs + "], " +
                        "rotationAngleInDegree [" + WheelComputationHelper.radToDegree(absRotationAngleInRad) + "]"
        );

        return rotationDirection == WheelRotationDirection.Anticlockwise ?
                resultSwipeDistanceAbs : -resultSwipeDistanceAbs;
        */

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

    /**
     * Transforms swipe gesture's travelled distance {@code scrollDelta} into relevant
     * wheel rotation angle.
     */
    double fromTraveledDistanceToWheelRotationAngle(int scrollDelta) {
//        final int outerDiameter = getOuterDiameter();
//        final double asinArg = Math.abs(scrollDelta) / (double) outerDiameter;
//        return Math.asin(asinArg);
        return (double) scrollDelta / wheelConfig.getOuterRadius();
    }

    double fromWheelRotationAngleToTraveledDistance(double rotationAngleInRad) {
//        final int outerDiameter = getOuterDiameter();
//        return outerDiameter * Math.sin(rotationAngleInRad);

        return rotationAngleInRad * wheelConfig.getOuterRadius();
    }

    @Deprecated
    int getOuterDiameter() {
        return 2 * wheelConfig.getOuterRadius();
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
        final double angleToRotate = fromTraveledDistanceToWheelRotationAngle(dy);

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

    private void logI(String message) {
        if (IS_LOG_ACTIVATED) {
            if (IS_FILTER_LOG_BY_METHOD_NAME && isMessageContainsAllowedMethod(message)) {
                Log.i(TAG, message);
            }
        }
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
         *
         * Defines sector's view top edge angular position.
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
