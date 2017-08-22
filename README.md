# Magic EPG
Unusual RecyclerView with custom LayoutManager

## Short intro, please
This demo app unleashes not trivial usage of `RecyclerView` component

The original idea behind it was to create *EPG* (Electronic Page Guide) where channel assets would be a sectors of the rotating carousel and channel's content would be scrollable horizontal stripe of assets with ability to select and playback choosen TV/VOD item

**It's worth mentioning that current implementation doesn't strictly bound to the original idea and existing 
codebase with minor changes might be successfully applied to any `Folder-Assets` data models**

Carousel is smooth scrollable in `up/down` directions with ability to smoothly approach randomly selected item to selection 
window (the gap in middle part of the carousel) with subsequent displaying item's content in dedicated horizontal stripe

## Pictures worth thousands of words

*In static:*

![alt text](https://github.com/alexeykovalev/epg_magic_carousel/blob/master/screen_captures/static_carousel_view.png)

*In dynamic:*

![alt text](https://github.com/alexeykovalev/epg_magic_carousel/blob/master/screen_captures/dynamic_carousel_view.gif)

## Brief talk about internals

Magic starts happening inside `WheelFragment`. It cosists of 2 big parts: `WheelsContainerFrameView` and `HorizontalCoversFlowView` each of which is a custom view itself

`WheelsContainerFrameView` contains 2 wheel parts - top (represented by `TopWheelRecyclerView`) and bottom (represented by `BottomWheelRecyclerView`) with the gap between them. Both of them are descendants of `AbstractWheelRecyclerView` with their own implementation of `AbstractWheelLayoutManager` responsible for layouting `RecyclerView`'s children as the sectors inside the carousel

To have a clear picture what is going on internally please take a closer look on those foundational classes:

* `WheelsContainerFrameView`
* `AbstractWheelRecyclerView` with descendants `BottomWheelRecyclerView` and `TopWheelRecyclerView`
* `AbstractWheelLayoutManager` with descendants `BottomWheelLayoutManager` and `TopWheelLayoutManager`
* `AbstractWheelRotator`
* `HorizontalCoversFlowView`
