package com.sss.magicwheel.coversflow.widget;

import com.sss.magicwheel.coversflow.entity.CoverEntity;

/**
 * Defines general contract for view which is going to be a coverView
 * in {@link HorizontalCoversFlowView} container.
 *
 * @author Alexey Kovalev
 * @since 24.02.2016.
 */
public interface IHorizontalCoverView {

    void bind(CoverEntity coverEntity);

    /**
     * Should return {@code true} in case when {@code View} implementing this interface
     * is a fake one (does not renders data item) but plays role of offset from screen edge
     * either left of right.
     */
    boolean isOffsetCover();
}
