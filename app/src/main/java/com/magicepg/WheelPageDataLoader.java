package com.magicepg;

import android.net.Uri;

import com.magicepg.coversflow.CoverEntity;
import com.magicepg.func.Consumer;
import com.magicepg.wheel.entity.WheelDataItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entity.Color;


/**
 * Takes responsibility of loading wheel's data items and resolving
 * position which has to be selected in wheel by default.
 *
 * @author Alexey Kovalev
 * @since 29.03.2017
 */
public final class WheelPageDataLoader {

    /**
     * From this position items in wheel will start layout.
     * For now it's just a plain constant but it might be computed dynamically.
     */
    private static final int DEFAULT_POSITION_TO_START_WHEELS_LAYOUT = 0;


    public void loadWheelData(Consumer<WheelData> wheelDataConsumer) {
        Color redColor = Color.ofRgb(255, 0, 0);
//        Uri sampleImageUri = Uri.parse("http://1821662466.rsc.cdn77.org/images/google_apps_education.jpg");
        Uri sampleImageUri = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/M82_HST_ACS_2006-14-a-large_web.jpg/1280px-M82_HST_ACS_2006-14-a-large_web.jpg");
        List<WheelDataItem> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new WheelDataItem("Title " + i, redColor, sampleImageUri));
        }
        wheelDataConsumer.accept(new WheelData(items, DEFAULT_POSITION_TO_START_WHEELS_LAYOUT));
    }

    public void loadCoverEntitiesByWheelItem(WheelDataItem selectedWheelItem, Consumer<List<CoverEntity>> resConsumer) {
        List<CoverEntity> res = new ArrayList<>();
        Uri sampleImageUri = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/c/ce/M82_HST_ACS_2006-14-a-large_web.jpg/1280px-M82_HST_ACS_2006-14-a-large_web.jpg");
        for (int i = 0; i < 20; i++) {
            res.add(new CoverEntity("Cover title " + i, sampleImageUri, Color.BLACK));
        }
        resConsumer.accept(res);
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
