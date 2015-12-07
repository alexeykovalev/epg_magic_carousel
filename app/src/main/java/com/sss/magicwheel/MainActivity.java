package com.sss.magicwheel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.sss.magicwheel.manager.CircleConfig;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelDataItem;
import com.sss.magicwheel.manager.WheelLayoutManager;
import com.sss.magicwheel.manager.WheelUtils;
import com.sss.magicwheel.util.MagicCalculationHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int DEFAULT_SECTOR_ANGLE_IN_DEGREE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initCalculationHelper();
        setContentView(R.layout.activity_main_layout);

        final RecyclerView wheelContainer = (RecyclerView) findViewById(R.id.wheel_container);
        wheelContainer.setLayoutManager(new WheelLayoutManager(this, createCircleConfig()));
        wheelContainer.setAdapter(createWheelAdapter());


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

    private RecyclerView.Adapter createWheelAdapter() {
        List<WheelDataItem> items = new ArrayList<>();
        items.add(new WheelDataItem("first"));
        items.add(new WheelDataItem("second"));
        items.add(new WheelDataItem("third"));
        items.add(new WheelDataItem("fourth"));
        items.add(new WheelDataItem("fifth"));

        return new WheelAdapter(this, items);
    }

    @Deprecated
    private void initCalculationHelper() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        MagicCalculationHelper.initialize(wm.getDefaultDisplay());
    }


    private CircleConfig createCircleConfig() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        Point screenSize = calcScreenSize(wm.getDefaultDisplay());
        final int screenWidth = screenSize.x;
        final int screenHeight = screenSize.y;

        final Point circleCenter = new Point(0, screenHeight / 2);

        // TODO: 03.12.2015 Not good hardcoded values
        final int innerRadius = screenHeight / 2 + 50;
        final int outerRadius = innerRadius + 400;

        CircleConfig.AngularRestrictions angularRestrictions = new CircleConfig.AngularRestrictions(
                WheelUtils.degreeToRadian(DEFAULT_SECTOR_ANGLE_IN_DEGREE),
                Math.PI / 2,
                -Math.PI / 2
        );

        return new CircleConfig(circleCenter, outerRadius, innerRadius, angularRestrictions);
    }

    // TODO: 03.12.2015 Wheel boundaries might be not restricted by screen but by custom rectangle instead
    private Point calcScreenSize(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size;
    }

}
