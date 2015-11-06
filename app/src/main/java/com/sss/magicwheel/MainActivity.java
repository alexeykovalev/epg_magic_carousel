package com.sss.magicwheel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.sss.magicwheel.util.MagicCalculationHelper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCalculationHelper();
        setContentView(R.layout.activity_main_layout);

//        final View rabbitView = findViewById(R.id.view_to_handle);
//
//        Button actionButton = (Button) findViewById(R.id.action_button);
//        actionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("TAG", "X [" + rabbitView.getX() + "]");
//                rabbitView.setPivotX(rabbitView.getWidth() / 2);
//                rabbitView.setPivotY(rabbitView.getHeight());
//                rabbitView.setRotation(30);
//                Log.e("TAG", "X [" + rabbitView.getX() + "]");
//            }
//        });
    }

    private void initCalculationHelper() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        MagicCalculationHelper.initialize(wm.getDefaultDisplay());
    }

}
