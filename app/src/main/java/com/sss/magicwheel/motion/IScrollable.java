package com.sss.magicwheel.motion;

import android.view.View;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
public interface IScrollable {

    int scrollHorizontallyBy(int dx);

    int scrollVerticallyBy(int dy);

    View getContentView();
}