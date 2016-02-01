package com.sss.magicwheel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private View headerView;
    private View fragmentContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        headerView = findViewById(R.id.header);
        fragmentContainerView = findViewById(R.id.fragments_container);

        injectWheelPage();
    }

    public int getAvailableSpace() {
        Log.e("TAG", "headerView.getHeight() [" + headerView.getHeight() + "]");
        return getScreenHeight() - headerView.getHeight();
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size.y;
    }


    private void injectWheelPage() {
        Fragment wheelFragment = new FragmentWheelPage();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragments_container, wheelFragment)
                .commit();
    }
}
