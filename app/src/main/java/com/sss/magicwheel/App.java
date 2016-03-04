package com.sss.magicwheel;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * @author Alexey Kovalev
 * @since 23.02.2016.
 */
public class App extends Application {

    public static App instance;

    public static App getInstance() {
        return instance;
    }

    public static float dpToPixels(float valueInDp) {
        return (valueInDp * instance.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static RequestManager glide() {
        return Glide.with(instance);
    }
}
