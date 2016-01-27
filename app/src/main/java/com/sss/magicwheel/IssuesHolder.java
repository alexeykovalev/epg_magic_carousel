package com.sss.magicwheel;

/**
 * @author Alexey Kovalev
 * @since 27.01.2016.
 */
public class IssuesHolder {

    private IssuesHolder() {
    }

    private void implementSectorRaysOnWheel() {

        //  1. override dispatchDraw() on WheelBigWrapperView
        // we have to increase WheelBigWrapperView width and height in order to draw outside
        // actual canvas bounds. And also position SectorWrapperView using left and top margins

        //  2. make rays layout directly in WheelOfFortuneLayoutManager
        // after each rotation we need to traverse all added children check if it's ray - remove all of them
        // and then again add rays to appropriate positions (layout and rotate actually)
    }

    private void implementOnEachSectorColoredStripeOnLeftSide() {
        // draw circle ring on canvas using dispatchDraw() on WheelBigWrapperView
    }

    private void implementTwoBoldFrameCirclesOnBottomOfWheel() {

    }

    private void roadmapFor_28_01_2016() {
        // 1. For each sector draw colored left edge (implementOnEachSectorColoredStripeOnLeftSide issue)
        // 2. Layout rays using LayoutManager (implementSectorRaysOnWheel issue)
    }
}
