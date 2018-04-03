package com.github.vicianm.stickyviewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * {@link ViewPager.OnPageChangeListener} which synchronizes current selection
 * of header with the content of {@link ViewPager}.
 */
public class HeaderPagerSynchronizer implements ViewPager.OnPageChangeListener {

    private float SMOOTH_SCROLL_SPEED_PER_PX = 0.5f;

    private Context context;

    private VerticalPagerAdapter demoPagerAdapter;

    private StickyHeaderAdapter demoHeaderAdapter;

    private RecyclerView recyclerView;

    private int currentPageIndex;

    public HeaderPagerSynchronizer(Context context, VerticalPagerAdapter demoPagerAdapter, StickyHeaderAdapter demoHeaderAdapter, RecyclerView recyclerView) {
        this.context = context;
        this.demoPagerAdapter = demoPagerAdapter;
        this.demoHeaderAdapter = demoHeaderAdapter;
        this.recyclerView = recyclerView;

        Object currentPageObject = this.demoPagerAdapter.getPrimaryItemObject();
        currentPageIndex = this.demoPagerAdapter.getItemPosition(currentPageObject);

        updateUiHeader(0);
    }

    @Override
    public void onPageSelected(int position) {
        updateUiHeader(position);
    }

    protected void updateUiHeader(int position) {
        int posOld = currentPageIndex;
        int posNew = position;
        updateAdapterData(posNew, posOld);
        currentPageIndex = posNew;
        scrollToCurrentHeader();
    }

    protected void updateAdapterData(int posNew, int posOld) {
        if (posNew == posOld) {
            // no header needs to be updated
        } else if (posNew > posOld) {
            // new headers need to be added
            for (int i = posOld+1; i<=posNew; i++) {
                demoHeaderAdapter.addItem("Header item " + i);
            }
        } else {
            // old headers need to be removed
            for (int i = posOld; i>posNew; i--) {
                demoHeaderAdapter.removeItem(i);
            }
        }
    }

    protected void scrollToCurrentHeader() {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(context) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_END;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                // TODO what's the default scroll speed
                return SMOOTH_SCROLL_SPEED_PER_PX;
            }
        };
        smoothScroller.setTargetPosition(currentPageIndex);
        recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
