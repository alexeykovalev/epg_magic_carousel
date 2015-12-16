package com.sss.magicwheel.manager.second;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.sss.magicwheel.manager.WheelUtils;

/**
 * @author Alexey Kovalev
 * @since 14.12.2015.
 */
@Deprecated
final class WheelLayoutDataHolder {

    double mRequestedScrollAngle;
    WheelRotationDirection mRotationDirection;

    /**
     * Starting from this angle new children will be added to circle.
     */
    double mAngleToStartLayout;

    /**
     * Current position on the adapter to get the next item.
     */
    int mNextLayoutPosition;

    /**
     * @return true if there are more items in the data adapter
     */
    boolean hasMore(RecyclerView.State state) {
        return mNextLayoutPosition >= 0 && mNextLayoutPosition < state.getItemCount();
    }

    /**
     * Gets the view for the next element that we should layout.
     * Also updates current item index to the next item, based on {@code mItemFetchDirection}
     *
     * @return The next element that we should layout.
     */
    View next(RecyclerView.Recycler recycler) {
        final View view = recycler.getViewForPosition(mNextLayoutPosition);
        mNextLayoutPosition += mRotationDirection.adapterPositionIncrementation;
        return view;
    }

    @Override
    public String toString() {
        return "LayoutState{" +
                ", mRequestedScrollAngle=" + WheelUtils.radToDegree(mRequestedScrollAngle) +
                ", mAngleToStartLayout=" + WheelUtils.radToDegree(mAngleToStartLayout) +
                ", mNextLayoutPosition=" + mNextLayoutPosition +
                '}';
    }

}
