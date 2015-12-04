package com.sss.magicwheel.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * @author Alexey Kovalev
 * @since 03.12.2015.
 */
public final class WrappedWheelView extends ViewGroup {

    private static final String TAG = WrappedWheelView.class.getCanonicalName();

    private static final int DEFAULT_SECTOR_ANGLE_IN_DEGREE = 20;
    public static final int NOT_DEFINED_VALUE = Integer.MIN_VALUE;

    private static final double DEGREE_TO_RAD_COEF = Math.PI / 180;

    private final ComputationHelper computationHelper;


    public WrappedWheelView(Context context) {
        this(context, null);
    }

    public WrappedWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrappedWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        computationHelper = new ComputationHelper(DEFAULT_SECTOR_ANGLE_IN_DEGREE);
    }

    private final class ComputationHelper {

        private final double sectorAngle;

        private final int innerRadius;
        private final int outerRadius;

        private int sectorWrapperViewWidth = NOT_DEFINED_VALUE;
        private int sectorWrapperViewHeight = NOT_DEFINED_VALUE;

        public ComputationHelper(int sectorAngleInDegree) {
            this.sectorAngle = degreeToRadian(sectorAngleInDegree);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

            Point screenSize = calcScreenSize(wm.getDefaultDisplay());
            final int screenWidth = screenSize.x;
            final int screenHeight = screenSize.y;

            // TODO: 03.12.2015 Not good hardcoded values
            innerRadius = screenHeight / 2 + 50;
            outerRadius = innerRadius + 400;
        }

        private double degreeToRadian(int sectorAngleInDegree) {
            return sectorAngleInDegree * DEGREE_TO_RAD_COEF;
        }

        // TODO: 03.12.2015 Wheel boundaries might be not restricted by screen but by custom rectangle instead
        private Point calcScreenSize(Display display) {
            Point size = new Point();
            display.getSize(size);
            return size;
        }

        /**
         * Width of the view which wraps the sector.
         */
        public int getSectorWrapperViewWidth() {
            if (sectorWrapperViewWidth == NOT_DEFINED_VALUE) {
                sectorWrapperViewWidth = computeViewWidth();
            }
            return sectorWrapperViewWidth;
        }

        private int computeViewWidth() {
            final double viewLeftPos = innerRadius - innerRadius * Math.cos(sectorAngle / 2);
            return (int) (outerRadius - viewLeftPos);
        }

        /**
         * Height of the view which wraps the sector.
         */
        public int getSectorWrapperViewHeight() {
            if (sectorWrapperViewHeight == NOT_DEFINED_VALUE) {
                sectorWrapperViewHeight = computeViewHeight();
            }
            return sectorWrapperViewHeight;
        }

        private int computeViewHeight() {
            final double halfHeight = outerRadius * Math.sin(sectorAngle / 2);
            return (int) (2 * halfHeight);
        }

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
//        return new WheelLayoutParams(computationHelper.getSectorWrapperViewWidth(), computationHelper.getSectorWrapperViewHeight());
        return new WheelLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new WheelLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new WheelLayoutParams(p);
    }


    public static final class WheelLayoutParams extends ViewGroup.LayoutParams {



        public WheelLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public WheelLayoutParams(int width, int height) {
            super(width, height);
        }

        public WheelLayoutParams(LayoutParams source) {
            super(source);
        }
    }

}
