package com.sss.magicwheel.entity;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * @author Alexey Kovalev
 * @since 05.11.2015.
 */
@Deprecated
public class ClipData {

    private final RectF outerOvalForArc;
    private final RectF innerOvalForArc;

    private final LineBetween sectorBorderOne;
    private final LineBetween sectorBorderTwo;

    public ClipData(RectF outerOvalForArc,
                    RectF innerOvalForArc,
                    LineBetween sectorBorderOne,
                    LineBetween sectorBorderTwo) {
        this.outerOvalForArc = outerOvalForArc;
        this.innerOvalForArc = innerOvalForArc;
        this.sectorBorderOne = sectorBorderOne;
        this.sectorBorderTwo = sectorBorderTwo;
    }


    public RectF getOuterOvalForArc() {
        return outerOvalForArc;
    }

    public RectF getInnerOvalForArc() {
        return innerOvalForArc;
    }

    public LineBetween getSectorBorderOne() {
        return sectorBorderOne;
    }

    public LineBetween getSectorBorderTwo() {
        return sectorBorderTwo;
    }



    public static class LineBetween {

        private final PointF first;
        private final PointF second;

        public LineBetween(PointF first, PointF second) {
            this.first = first;
            this.second = second;
        }

        public static LineBetween ofStub() {
            return new LineBetween(null, null);
        }

        public PointF getFirst() {
            return first;
        }

        public PointF getSecond() {
            return second;
        }
    }

}
