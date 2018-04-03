package com.github.vicianm.stickyviewpager;

import android.support.v7.widget.RecyclerView;

public abstract class StickyHeaderAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

//    public abstract void addItem(int position);
    public abstract void addItem(String title); // tmp

    public abstract void removeItem(int position);

}
