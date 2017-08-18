package com.magicepg;

import android.app.Application;

/**
 * @author Alexey
 * @since 8/16/17
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
}
