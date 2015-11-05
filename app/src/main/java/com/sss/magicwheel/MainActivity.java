package com.sss.magicwheel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.sss.magicwheel.util.MagicCalculationHelper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCalculationHelper();
        setContentView(R.layout.activity_main_layout);
    }

    private void initCalculationHelper() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        MagicCalculationHelper.initialize(wm.getDefaultDisplay());
    }

}
