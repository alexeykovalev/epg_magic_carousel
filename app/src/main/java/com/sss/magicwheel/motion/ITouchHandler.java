package com.sss.magicwheel.motion;

/**
 * // todo: JavaDoc
 *
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */

import android.view.MotionEvent;

/**
 * Represents touch handler
 */
public interface ITouchHandler {

    int INVALID_POINTER = -1;
    int SCROLL_STATE_IDLE = 0;
    int SCROLL_STATE_DRAGGING = 1;
    int SCROLL_STATE_SETTLING = 2;
    int MAX_SCROLL_DURATION = 2000;

    /**
     * Intercepts touch events
     * @param e motion event
     * @return true if event happens, false - otherwise
     */
    boolean onInterceptTouchEvent(MotionEvent e);

    /**
     * On touch event handler
     * @param e motion event
     * @return true if event happens, false - otherwise
     */
    boolean onTouchEvent(MotionEvent e);

}
