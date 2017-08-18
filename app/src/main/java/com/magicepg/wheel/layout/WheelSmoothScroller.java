package com.magicepg.wheel.layout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.magicepg.wheel.entity.WheelRotationDirection;
import com.magicepg.wheel.WheelComputationHelper;


/**
 * @author Alexey Kovalev
 * @since 08.02.2017
 */
abstract class WheelSmoothScroller extends RecyclerView.SmoothScroller {

    public static final String TAG = WheelSmoothScroller.class.getCanonicalName();

    private static final float MILLISECONDS_PER_INCH = 25f;

    private static float MILLISECONDS_PER_PX;

    private final AbstractWheelLayoutManager layoutManager;
    private final WheelComputationHelper computationHelper;
    private final double targetSeekScrollAngleInRad;

    private final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    private final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    WheelSmoothScroller(Context context, AbstractWheelLayoutManager layoutManager,
                        WheelComputationHelper computationHelper, double targetSeekScrollAngleInRad) {

        this.layoutManager = layoutManager;
        this.computationHelper = computationHelper;
        this.targetSeekScrollAngleInRad = targetSeekScrollAngleInRad;

        MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }

    /**
     * Calculates the scroll speed.
     *
     * @param displayMetrics DisplayMetrics to be used for real dimension calculations
     * @return The time (in ms) it should take for each pixel. For instance, if returned value is
     * 2 ms, it means scrolling 1000 pixels with LinearInterpolation should take 2 seconds.
     */
    private static float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }

    protected abstract WheelRotationDirection computeRotationDirectionForPosition(int targetPosition);

    @Override
    protected void onStart() {
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
//        String title = ((WheelBigWrapperView) targetView).getTitle();
        final double rotationAngleInRad = calculateAngleInRadToMakeSectorInvisible(targetView);

//        final int dy = (int) Math.round(layoutManager.fromWheelRotationAngleToTraveledDistance(rotationAngleInRad));
        int dy = (int) layoutManager.computationHelper.fromWheelRotationAngleToTraveledDistance(rotationAngleInRad);

//        Log.e(TAG, "WheelSmoothScroller title [" + title + "], " +
//                "dy [" + dy + "], " +
//                "rotationAngleInRad [" + WheelComputationHelper.radToDegree(rotationAngleInRad) + "]");

        final int time = calculateTimeForDeceleration(dy);
        if (time > 0) {
            action.update(0, -dy, time, decelerateInterpolator);
        }
    }


    @Override
    protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {
        if (getChildCount() == 0) {
            stop();
            return;
        }

        final WheelRotationDirection rotationDirection = computeRotationDirectionForPosition(getTargetPosition());

        final int targetSeekScrollDistanceAsInt = (int) layoutManager.computationHelper.fromWheelRotationAngleToTraveledDistance(targetSeekScrollAngleInRad);
        final int time = calculateTimeForScrolling(targetSeekScrollDistanceAsInt);

//        final int newDy = (int) (targetSeekScrollDistanceAsInt * TARGET_SEEK_EXTRA_SCROLL_RATIO);
        final int newDy = rotationDirection == WheelRotationDirection.Anticlockwise ?
                targetSeekScrollDistanceAsInt : -targetSeekScrollDistanceAsInt;

        action.update(0, newDy, time, mLinearInterpolator);
    }


    private double calculateAngleInRadToMakeSectorInvisible(View sectorToHide) {
        final AbstractWheelLayoutManager.LayoutParams sectorToHideLp =
                AbstractWheelLayoutManager.getChildLayoutParams(sectorToHide);
        final double sectorToHideTopEdgeAngle = computationHelper.getSectorAngleTopEdgeInRad(sectorToHideLp.anglePositionInRad);
        return sectorToHideTopEdgeAngle - layoutManager.getLayoutEndAngleInRad();
    }

    /**
     * <p>Calculates the time for deceleration so that transition from LinearInterpolator to
     * DecelerateInterpolator looks smooth.</p>
     *
     * @param dx Distance to scroll
     * @return Time for DecelerateInterpolator to smoothly traverse the distance when transitioning
     * from LinearInterpolation
     */
    protected int calculateTimeForDeceleration(int dx) {
        // we want to cover same area with the linear interpolator for the first 10% of the
        // interpolation. After that, deceleration will take control.
        // area under curve (1-(1-x)^2) can be calculated as (1 - x/3) * x * x
        // which gives 0.100028 when x = .3356
        // this is why we divide linear scrolling time with .3356
        return (int) Math.ceil(calculateTimeForScrolling(dx) / .3356);
    }

    /**
     * Calculates the time it should take to scroll the given distance (in pixels)
     *
     * @param dx Distance in pixels that we want to scroll
     * @return Time in milliseconds
     * @see #calculateSpeedPerPixel(DisplayMetrics)
     */
    protected int calculateTimeForScrolling(int dx) {
        // In a case where dx is very small, rounding may return 0 although dx > 0.
        // To avoid that issue, ceil the result so that if dx > 0, we'll always return positive
        // time.
        return (int) Math.ceil(Math.abs(dx) * MILLISECONDS_PER_PX);
    }

}
