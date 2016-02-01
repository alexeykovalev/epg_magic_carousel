package com.sss.magicwheel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.WindowManager;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.manager.decor.WheelFrameItemDecoration;
import com.sss.magicwheel.manager.decor.WheelSectorRayItemDecoration;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectWheelPage();
        setContentView(R.layout.activity_main_layout);
    }

    private void injectWheelPage() {
        Fragment wheelFragment = new FragmentWheelPage();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragments_container, wheelFragment)
                .commit();
    }
}
