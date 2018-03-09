package com.github.vicianm.stickyviewpager;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * {@link ViewPager} with the ability to work in vertical scale.
 * This pager has to backed by {@link VerticalPagerAdapter} ({@see #getAdater}).
 */
public class VerticalViewPager extends ViewPager {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int mSwipeOrientation;

    private float mmLastMotionY;

    boolean childTopOverScroll = false;
    boolean childBottomOverScroll = false;

    public VerticalViewPager(Context context) {
        super(context);
        mSwipeOrientation = VERTICAL;
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSwipeOrientation(VERTICAL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(mSwipeOrientation == VERTICAL ? swapXY(event) : event);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof VerticalPagerAdapter)) {
            throw new RuntimeException("Invalid param");
        }
        super.setAdapter(adapter);
    }

    /**
     * Retrieve the current {@link VerticalPagerAdapter} supplying pages.
     *
     * @return The currently registered {@link VerticalPagerAdapter}
     */
    public VerticalPagerAdapter getAdapter() {
        return (VerticalPagerAdapter) super.getAdapter();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        MotionEvent swappedMotionEvent = mSwipeOrientation == VERTICAL ? swapXY(event) : event;

        childTopOverScroll = false;
        childBottomOverScroll = false;

        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;

        Object child = getAdapter().getPrimaryItemObject();
        ScrollView childScrollView = (child instanceof ScrollView) ? (ScrollView) child : null;

        if (action == MotionEvent.ACTION_MOVE) {

            // Treat scroll view differently.
            // Intercept only if ScrollView is scrolled to the very top/bottom.
            // Otherwise let ScrollView do its work first - just scroll according to user gesture.
            if (childScrollView != null) {

                final float y = swappedMotionEvent.getY(0);
                final float dy = y - mmLastMotionY;
                if (isScrolledToTop(childScrollView) && dy < 0) {
                    childTopOverScroll = true;
                }
                if (isScrolledToBottom(childScrollView) && dy > 0) {
                    childBottomOverScroll = true;
                }
            }
        }

        mmLastMotionY = swappedMotionEvent.getY(0);

        if (childScrollView == null || childTopOverScroll || childBottomOverScroll || action == MotionEvent.ACTION_DOWN) {
            return super.onInterceptTouchEvent(swappedMotionEvent);
        } else {
            return false;
        }
    }

    protected boolean isScrolledToTop(ScrollView scrollView) {
        return scrollView.getScrollY() == 0;
    }

    protected boolean isScrolledToBottom(ScrollView scrollView) {
        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        View view = scrollView.getChildAt(getChildCount()-1);

        int diff = (view.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()));
        return diff <= 0;

    }

    public void setSwipeOrientation(int swipeOrientation) {
        if (swipeOrientation == HORIZONTAL || swipeOrientation == VERTICAL)
            mSwipeOrientation = swipeOrientation;
        else
            throw new IllegalStateException("Swipe Orientation can be either CustomViewPager.HORIZONTAL" +
                    " or CustomViewPager.VERTICAL");
        initSwipeMethods();
    }

    private void initSwipeMethods() {
        if (mSwipeOrientation == VERTICAL) {
            // The majority of the work is done over here
            setPageTransformer(true, new VerticalPageTransformer());
            // The easiest way to get rid of the overscroll drawing that happens on the left and right
            setOverScrollMode(OVER_SCROLL_NEVER);
        }
    }

    private MotionEvent swapXY(MotionEvent event) {

        MotionEvent clone = MotionEvent.obtain(event);

        float width = getWidth();
        float height = getHeight();

        float newX = (event.getY() / height) * width;
        float newY = (event.getX() / width) * height;

        clone.setLocation(newX, newY);
        return clone;
    }

    private class VerticalPageTransformer implements PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            if (position < -1) {
                // This page is way off-screen to the left
                page.setAlpha(0);
            } else if (position <= 1) {
                page.setAlpha(1);

                // Counteract the default slide transition
                page.setTranslationX(page.getWidth() * -position);

                // set Y position to swipe in from top
                float yPosition = position * page.getHeight();
                page.setTranslationY(yPosition);
            } else {
                // This page is way off screen to the right
                page.setAlpha(0);
            }
        }
    }

}
