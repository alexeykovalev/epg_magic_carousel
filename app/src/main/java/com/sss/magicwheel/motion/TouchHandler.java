package com.sss.magicwheel.motion;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;

/**
 * // todo: JavaDoc
 *
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
public class TouchHandler implements ITouchHandler {
    /**
     * Application context
     */
    private final Context mAppContext;
    /**
     * Link to the scroll observer, which will complete scrolling actions
     */
    private IScrollable mScrollableObserver;
    /**
     * Handle velocity of scrolling and fling actions
     */
    private VelocityTracker mVelocityTracker;

    private int mScrollPointerId = INVALID_POINTER;
    private int mScrollState = SCROLL_STATE_IDLE;
    /**
     * Initial horizontal touch coordinate
     */
    private int mInitialTouchX;
    /**
     * Initial vertical touch coordinate
     */
    private int mInitialTouchY;
    /**
     * Last horizontal touch coordinate
     */
    private int mLastTouchX;
    /**
     * Last vertical touch coordinate
     */
    private int mLastTouchY;
    /**
     * Refers to a distance in pixels a user's touch can
     * wander before the gesture is interpreted as scrolling
     */
    private int mTouchSlop;
    /**
     * Minimum fling velocity
     */
    private final int mMinFlingVelocity;
    /**
     * Maximum fling velocity
     */
    private final int mMaxFlingVelocity;

    private final ViewFlinger mViewFlinger;

    private static final Interpolator sQuinticInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    public TouchHandler(Context context, IScrollable scrollable) {
        mScrollableObserver = scrollable;
        mAppContext = context;
        mViewFlinger = new ViewFlinger(context, scrollable);
        final ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
        mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(motionEvent);
        final int action = MotionEventCompat.getActionMasked(motionEvent);
        final int actionIndex = MotionEventCompat.getActionIndex(motionEvent);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                mInitialTouchX = mLastTouchX = (int) (motionEvent.getX() + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (motionEvent.getY() + 0.5f);
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN:
                mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                mInitialTouchX = mLastTouchX = (int) (MotionEventCompat.getX(motionEvent, actionIndex) + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (MotionEventCompat.getY(motionEvent, actionIndex) + 0.5f);
                break;

            case MotionEvent.ACTION_MOVE: {
                final int index = MotionEventCompat.findPointerIndex(motionEvent, mScrollPointerId);
                if (index < 0) {
                    return false;
                }

                final int x = (int) (MotionEventCompat.getX(motionEvent, index) + 0.5f);
                final int y = (int) (MotionEventCompat.getY(motionEvent, index) + 0.5f);
                if (mScrollState != SCROLL_STATE_DRAGGING) {
                    final int dx = x - mInitialTouchX;
                    final int dy = y - mInitialTouchY;
                    boolean startScroll = false;
                    if (Math.abs(dx) > mTouchSlop) {
                        mLastTouchX = mInitialTouchX + mTouchSlop * (dx < 0 ? -1 : 1);
                        startScroll = true;
                    }
                    if (Math.abs(dy) > mTouchSlop) {
                        mLastTouchY = mInitialTouchY + mTouchSlop * (dy < 0 ? -1 : 1);
                        startScroll = true;
                    }
                    if (startScroll) {
                        setScrollState(SCROLL_STATE_DRAGGING);
                    }
                }
            }
            break;

            case MotionEventCompat.ACTION_POINTER_UP: {
                onPointerUp(motionEvent);
            }
            break;

            case MotionEvent.ACTION_UP: {
                mVelocityTracker.clear();
            }
            break;

            case MotionEvent.ACTION_CANCEL: {
                cancelTouch();
            }
        }
        return mScrollState == SCROLL_STATE_DRAGGING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        /* todo: add this section for dispatch epg items touch; for onOnterceptTOuch also
        if (dispatchOnItemTouch(e)) {
            cancelTouch();
            return true;
        }*/

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(motionEvent);

        final int action = MotionEventCompat.getActionMasked(motionEvent);
        final int actionIndex = MotionEventCompat.getActionIndex(motionEvent);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                mInitialTouchX = mLastTouchX = (int) (motionEvent.getX() + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (motionEvent.getY() + 0.5f);
            }
            break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                mScrollPointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                mInitialTouchX = mLastTouchX = (int) (MotionEventCompat.getX(motionEvent, actionIndex) + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (MotionEventCompat.getY(motionEvent, actionIndex) + 0.5f);
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                final int index = MotionEventCompat.findPointerIndex(motionEvent, mScrollPointerId);
                if (index < 0) {
                    return false;
                }

                final int x = (int) (MotionEventCompat.getX(motionEvent, index) + 0.5f);
                final int y = (int) (MotionEventCompat.getY(motionEvent, index) + 0.5f);
                if (mScrollState != SCROLL_STATE_DRAGGING) {
                    final int dx = x - mInitialTouchX;
                    final int dy = y - mInitialTouchY;
                    boolean startScroll = false;
                    if (Math.abs(dx) > mTouchSlop) {
                        mLastTouchX = mInitialTouchX + mTouchSlop * (dx < 0 ? -1 : 1);
                        startScroll = true;
                    }
                    if (Math.abs(dy) > mTouchSlop) {
                        mLastTouchY = mInitialTouchY + mTouchSlop * (dy < 0 ? -1 : 1);
                        startScroll = true;
                    }
                    if (startScroll) {
                        setScrollState(SCROLL_STATE_DRAGGING);
                    }
                }
                if (mScrollState == SCROLL_STATE_DRAGGING) {
                    final int dx = x - mLastTouchX;
                    final int dy = y - mLastTouchY;
                    scrollByInternal(-dx, -dy);
                }
                mLastTouchX = x;
                mLastTouchY = y;
            }
            break;

            case MotionEventCompat.ACTION_POINTER_UP: {
                onPointerUp(motionEvent);
            }
            break;

            case MotionEvent.ACTION_UP: {
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                final float xvel = -VelocityTrackerCompat.getXVelocity(mVelocityTracker, mScrollPointerId);
                final float yvel = -VelocityTrackerCompat.getYVelocity(mVelocityTracker, mScrollPointerId);
                if (!((xvel != 0 || yvel != 0) && fling((int) xvel, (int) yvel))) {
                    setScrollState(SCROLL_STATE_IDLE);
                }
                mVelocityTracker.clear();
            }
            break;

            case MotionEvent.ACTION_CANCEL: {
                cancelTouch();
            }
            break;
        }

        return true;
    }

    private void onPointerUp(MotionEvent e) {
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        if (MotionEventCompat.getPointerId(e, actionIndex) == mScrollPointerId) {
            // Pick a new pointer to pick up the slack.
            final int newIndex = actionIndex == 0 ? 1 : 0;
            mScrollPointerId = MotionEventCompat.getPointerId(e, newIndex);
            mInitialTouchX = mLastTouchX = (int) (MotionEventCompat.getX(e, newIndex) + 0.5f);
            mInitialTouchY = mLastTouchY = (int) (MotionEventCompat.getY(e, newIndex) + 0.5f);
        }
    }

    private void cancelTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
        setScrollState(SCROLL_STATE_IDLE);
    }

    private void setScrollState(int state) {
        if (state == mScrollState) {
            return;
        }
        mScrollState = state;
    }

    /**
     * Does not perform bounds checking. Used by internal methods that have already validated input.
     */
    private void scrollByInternal(int x, int y) {
        if (x != 0) {
            mScrollableObserver.scrollHorizontallyBy(x);
        }
        if (y != 0) {
            mScrollableObserver.scrollVerticallyBy(y);
        }
    }

    private boolean fling(int velocityX, int velocityY) {
        if (Math.abs(velocityX) < mMinFlingVelocity) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < mMinFlingVelocity) {
            velocityY = 0;
        }
        velocityX = Math.max(-mMaxFlingVelocity, Math.min(velocityX, mMaxFlingVelocity));
        velocityY = Math.max(-mMaxFlingVelocity, Math.min(velocityY, mMaxFlingVelocity));
        if (velocityX != 0 || velocityY != 0) {
            mViewFlinger.fling(velocityX, velocityY);
            return true;
        }
        return false;
    }

    private class ViewFlinger implements Runnable {
        private final IScrollable mScrollable;
        private int mLastFlingX;
        private int mLastFlingY;
        private ScrollerCompat mScroller;
        private Interpolator mInterpolator = sQuinticInterpolator;


        // When set to true, postOnAnimation callbacks are delayed until the run method completes
        private boolean mEatRunOnAnimationRequest = false;

        // Tracks if postAnimationCallback should be re-attached when it is done
        private boolean mReSchedulePostAnimationCallback = false;

        public ViewFlinger(Context context, IScrollable scrollable) {
            mScroller = ScrollerCompat.create(context, sQuinticInterpolator);
            mScrollable = scrollable;
        }

        @Override
        public void run() {
            disableRunOnAnimationRequests();
            // keep a local reference so that if it is changed during onAnimation method, it won't
            // cause unexpected behaviors
            final ScrollerCompat scroller = mScroller;
            if (scroller.computeScrollOffset()) {
                final int x = scroller.getCurrX();
                final int y = scroller.getCurrY();
                final int dx = x - mLastFlingX;
                final int dy = y - mLastFlingY;
                int hresult = 0;
                int vresult = 0;
                mLastFlingX = x;
                mLastFlingY = y;
                int overscrollX = 0, overscrollY = 0;
                if (dx != 0) {
                    hresult = mScrollable.scrollHorizontallyBy(dx);
                    overscrollX = dx - hresult;
                }
                if (dy != 0) {
                    vresult = mScrollable.scrollVerticallyBy(dy);
                    overscrollY = dy - vresult;
                }

                final boolean fullyConsumedScroll = dx == hresult && dy == vresult;
                if (overscrollX != 0 || overscrollY != 0) {
                    final int vel = (int) scroller.getCurrVelocity();

                    int velX = 0;
                    if (overscrollX != x) {
                        velX = overscrollX < 0 ? -vel : overscrollX > 0 ? vel : 0;
                    }

                    int velY = 0;
                    if (overscrollY != y) {
                        velY = overscrollY < 0 ? -vel : overscrollY > 0 ? vel : 0;
                    }
                    if ((velX != 0 || overscrollX == x || scroller.getFinalX() == 0) &&
                            (velY != 0 || overscrollY == y || scroller.getFinalY() == 0)) {
                        scroller.abortAnimation();
                    }
                }

                if (scroller.isFinished() || !fullyConsumedScroll) {
                    setScrollState(SCROLL_STATE_IDLE); // setting state to idle will stop this.
                } else {
                    postOnAnimation();
                }
            }
            enableRunOnAnimationRequests();
        }

        private void disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false;
            mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false;
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true;
            } else {
                ViewCompat.postOnAnimation(mScrollable.getContentView(), this);
            }
        }

        public void fling(int velocityX, int velocityY) {
            setScrollState(SCROLL_STATE_SETTLING);
            mLastFlingX = mLastFlingY = 0;
            mScroller.fling(0, 0, velocityX, velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        /**
         * Possible will be used in future to stop scrolling
         */
        public void stop() {
            mScroller.abortAnimation();
        }

    }
}
