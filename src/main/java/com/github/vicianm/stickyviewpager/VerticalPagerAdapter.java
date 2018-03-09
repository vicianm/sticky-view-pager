package com.github.vicianm.stickyviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

/**
 * {@link PagerAdapter} with the ability to return currently active
 * page container (container of a primary item).
 *
 * @see #getPrimaryItemObject()
 */
public abstract class VerticalPagerAdapter extends PagerAdapter {

    protected Context mContext;

    protected Object primaryItemObject;

    public VerticalPagerAdapter(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        this.primaryItemObject = object;
        super.setPrimaryItem(container, position, object);
    }

    /**
     * @return Currently active pager object (View).
     */
    public Object getPrimaryItemObject() {
        return primaryItemObject;
    }
}
