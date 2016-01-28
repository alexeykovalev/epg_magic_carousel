package com.sss.magicwheel.manager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;

import com.sss.magicwheel.MainActivity;
import com.sss.magicwheel.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * @author Alexey Kovalev
 * @since 28.01.2016.
 */
public class SectorRayItemDecoration extends RecyclerView.ItemDecoration {

    private final Context context;
    private final WheelComputationHelper computationHelper;
    private final Paint drawingPaint;
    private final RotateDrawable rayDrawable;

    public SectorRayItemDecoration(Context context) {
        this.context = context;
        this.computationHelper = WheelComputationHelper.getInstance();
        this.drawingPaint = initDrawingPaint();

        RotateDrawable rDrawable;
        rDrawable = (RotateDrawable) context.getResources().getDrawable(R.drawable.rotated_ray_drawable);

//        rDrawable.setFromDegrees(-60);
//        rDrawable.setToDegrees(-60);
//        rDrawable.invalidateSelf();
//        rDrawable.invalidateDrawable(rDrawable);

        this.rayDrawable = rDrawable; //context.getResources().getDrawable(R.drawable.rotated_ray_drawable);
//        this.rayDrawable = context.getResources().getDrawable(R.drawable.wheel_sector_ray);

        /*rDrawable = new RotateDrawable();
        rDrawable.setDrawable(context.getResources().getDrawable(R.drawable.wheel_sector_ray));
        rDrawable.setPivotX(0.5f);
        rDrawable.setPivotY(0.5f);
        rDrawable.setFromDegrees(90);
        rDrawable.setToDegrees(90);*/

//        rayDrawable = rDrawable;
    }


//    private RotateDrawable createFromXml() {
//        RotateDrawable rDrawable = new RotateDrawable();
//
//        //r is a Resources object containing the layout
////id is an integer from R.drawable
//        final Resources r = context.getResources();
//        XmlPullParser parser = r.getXml(id);
//        AttributeSet attrs = Xml.asAttributeSet(parser);
//        float pivotX = attrs. getAttributeFloatValue("http://schemas.android.com/apk/res/android", "fromDegrees", -60);
//        float pivotY = attrs.getAttributeFloatValue("http://schemas.android.com/apk/res/android", "toDegrees", -60);
////d is a RotateDrawable
//        attrs.
//        try {
//            rDrawable.inflate(r, parser, attrs);
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return rDrawable;
//    }

    private Paint initDrawingPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(15);
        paint.setAntiAlias(true);
        return paint;
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final WheelOfFortuneLayoutManager.LayoutParams childLp = (WheelOfFortuneLayoutManager.LayoutParams) child.getLayoutParams();
            int childAnglePositionInDegree = (int) WheelComputationHelper.radToDegree(childLp.anglePositionInRad);
//            anglePositionInRad

            /*final int top = child.getBottom() + childLp.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);*/

            final Point childRefPointPos = getSectorReferPointInRvCoordsSystem(child);
//            canvas.drawLine(0, parent.getHeight() / 2, childRefPointPos.x, childRefPointPos.y, drawingPaint);

//            rayDrawable.setBounds(100, 100, 100 + rayDrawable.getIntrinsicWidth(), 100 + rayDrawable.getIntrinsicHeight());

//            rayDrawable.setBounds(100, 100, 500, 120);


//            rayDrawable.setFromDegrees(-60);
//            rayDrawable.setToDegrees(-60);

            canvas.save();
            canvas.rotate(-10 - childAnglePositionInDegree, childRefPointPos.x, childRefPointPos.y);
            rayDrawable.setBounds(childRefPointPos.x, childRefPointPos.y, childRefPointPos.x + 700, childRefPointPos.y + 20);
            rayDrawable.draw(canvas);
            canvas.restore();
        }

    }

    private Point getSectorReferPointInRvCoordsSystem(View child) {
        final WheelOfFortuneLayoutManager.LayoutParams childLp = (WheelOfFortuneLayoutManager.LayoutParams) child.getLayoutParams();
        double childAnglePositionInRad = childLp.anglePositionInRad;

        final int radius = computationHelper.getCircleConfig().getInnerRadius();
        final int x = (int) (radius * Math.cos(childAnglePositionInRad));
        final int y = (int) (radius * Math.sin(childAnglePositionInRad));
        Point childRefPointPosInCircleCoordsSystem = new Point(x, y);

        return WheelComputationHelper.fromCircleCoordsSystemToRecyclerViewCoordsSystem(
                computationHelper.getCircleConfig().getCircleCenterRelToRecyclerView(),
                childRefPointPosInCircleCoordsSystem
        );
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}
