package com.magicwheel;

import android.net.Uri;

import com.magicwheel.entity.Color;
import com.magicwheel.entity.WheelDataItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Takes responsibility of loading wheel's data items and resolving
 * position which has to be selected in wheel by default.
 *
 * @author Alexey Kovalev
 * @since 29.03.2017
 */
public final class WheelDataLoader {

    public static final int NOT_VALID_POSITION = Integer.MIN_VALUE;

    /**
     * From this position items in channels wheel will start layout.
     * For now it's just a plain constant but it might be computed dynamically.
     * <p/>
     * By default we start wheel page by displaying information from "My Channel" channel
     */
    private static final int DEFAULT_POSITION_TO_START_WHEELS_LAYOUT = 0;


    public WheelData loadWheelData() {
        Color redColor = Color.ofRgb(255, 0, 0);
//        Uri sampleImageUri = Uri.parse("http://1821662466.rsc.cdn77.org/images/google_apps_education.jpg");
        Uri sampleImageUri = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/M82_HST_ACS_2006-14-a-large_web.jpg/1280px-M82_HST_ACS_2006-14-a-large_web.jpg");
        List<WheelDataItem> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new WheelDataItem("Title_" + i, redColor, sampleImageUri));
        }
        return new WheelData(items, DEFAULT_POSITION_TO_START_WHEELS_LAYOUT);
    }

    public static final class WheelData {

        private final List<WheelDataItem> wheelDataItems;
        private final int dataItemPositionToSelect;

        public WheelData(List<WheelDataItem> wheelDataItems, int dataItemPositionToSelect) {
            this.wheelDataItems = wheelDataItems;
            this.dataItemPositionToSelect = dataItemPositionToSelect;
        }

        public List<WheelDataItem> getWheelDataItems() {
            return Collections.unmodifiableList(wheelDataItems);
        }

        public int getDataItemPositionToSelect() {
            return dataItemPositionToSelect;
        }

    }

}
