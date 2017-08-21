# Magic EPG
Unusual RecyclerView with custom LayoutManager

## Introduction
This demo app unleashes not trivial usage of `RecyclerView` component

The original idea behind it was to create *EPG* (Electronic Page Guide) where channel assets would be a sectors of the rotating 
carousel and channel's content would be scrollable horizontal stripe of assets with ability to select and playback choosen *TV/VOD* item

**It's worth mentioning that current implementation doesn't strictly bound to the original idea of component and existing 
codebase with minor changes might be successfully applied to any `Folder-Assets` data models**

Carousel is smooth scrollable in `up/down` directions with ability to smoothly approach randomly selected item to selection 
window (the gap in middle part of the carousel) with subsequent displaying item's content in dedicated horizontal stripe
