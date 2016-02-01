package com.sss.magicwheel;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.sss.magicwheel.entity.WheelConfig;
import com.sss.magicwheel.entity.WheelDataItem;
import com.sss.magicwheel.manager.WheelAdapter;
import com.sss.magicwheel.manager.WheelComputationHelper;
import com.sss.magicwheel.manager.WheelOfFortuneLayoutManager;
import com.sss.magicwheel.manager.decor.WheelFrameItemDecoration;
import com.sss.magicwheel.manager.decor.WheelSectorRayItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Kovalev
 * @since 01.02.2016.
 */
public final class FragmentWheelPage extends Fragment {

    private static final int DEFAULT_SECTOR_ANGLE_IN_DEGREE = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WheelComputationHelper.initialize(createCircleConfig());

        final View rootView = inflater.inflate(R.layout.fragment_wheel_page_layout, container, false);
        final RecyclerView wheelContainerView = (RecyclerView) rootView.findViewById(R.id.wheel_container);
        initWheelContainer(wheelContainerView);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int containerHeight = ((MainActivity) getActivity()).getAvailableSpace();
            }
        });

        return rootView;
    }

    private void initWheelContainer(RecyclerView wheelContainerView) {
        wheelContainerView.setLayoutManager(new WheelOfFortuneLayoutManager());
        wheelContainerView.setAdapter(createWheelAdapter(createDataSet()));
        addWheelItemDecorations(wheelContainerView);
    }

    private void addWheelItemDecorations(RecyclerView wheelContainerView) {
        wheelContainerView.addItemDecoration(new WheelFrameItemDecoration(getActivity()));
        wheelContainerView.addItemDecoration(new WheelSectorRayItemDecoration(getActivity()));
//        wheelContainerView.addItemDecoration(new WheelSectorLeftEdgeColorItemDecoration(getActivity()));
    }

    private WheelAdapter createWheelAdapter(List<WheelDataItem> adapterDataSet) {
        return new WheelAdapter(getActivity(), adapterDataSet);
    }

    private List<WheelDataItem> createDataSet() {
        List<WheelDataItem> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(new WheelDataItem("item.Num [" + i + "]"));
        }
        return Collections.unmodifiableList(items);
    }

    private WheelConfig createCircleConfig() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        Point screenSize = calcScreenSize(wm.getDefaultDisplay());
        final int screenWidth = screenSize.x;
        final int screenHeight = screenSize.y;

        Log.e("TAG", "Sh Fragment [" + screenHeight + "]");

        final PointF circleCenter = new PointF(0, screenHeight / 2 );

        // TODO: 03.12.2015 Not good hardcoded values
        final int outerRadius =  screenHeight / 2;
        final int innerRadius = outerRadius - 200;

        WheelConfig.AngularRestrictions angularRestrictions = new WheelConfig.AngularRestrictions(
                WheelComputationHelper.degreeToRadian(DEFAULT_SECTOR_ANGLE_IN_DEGREE),
                Math.PI / 2,
                -Math.PI / 2
        );

        return new WheelConfig(circleCenter, outerRadius, innerRadius, angularRestrictions);
    }

    // TODO: 03.12.2015 Wheel boundaries might be not restricted by screen but by custom rectangle instead
    private Point calcScreenSize(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size;
    }

}
