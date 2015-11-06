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

    private final LineTo sectorBorderOne;
    private final LineTo sectorBorderTwo;

    public ClipData(RectF outerOvalForArc,
                    RectF innerOvalForArc,
                    LineTo sectorBorderOne,
                    LineTo sectorBorderTwo) {
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

    public LineTo getSectorBorderOne() {
        return sectorBorderOne;
    }

    public LineTo getSectorBorderTwo() {
        return sectorBorderTwo;
    }



    @Deprecated
    public static class LineTo {

        private final PointF first;
        private final PointF second;

        public LineTo(PointF first, PointF second) {
            this.first = first;
            this.second = second;
        }

        public static LineTo ofStub() {
            return new LineTo(null, null);
        }

        public PointF getFirst() {
            return first;
        }

        public PointF getSecond() {
            return second;
        }
    }

}
