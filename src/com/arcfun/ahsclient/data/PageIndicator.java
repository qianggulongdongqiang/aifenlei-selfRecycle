package com.arcfun.ahsclient.data;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public interface PageIndicator extends OnPageChangeListener {
    /**
     * Bind the indicator to a ViewPager.
     * 
     * @param view
     */
    void setViewPager(ViewPager view);

    /**
     * Bind the indicator to a ViewPager.
     * 
     * @param view
     * @param initialPosition
     */
    void setViewPager(ViewPager view, int initialPosition);

    /**
     * <p>
     * Set the current page of both the ViewPager and indicator.
     * </p>
     * <p/>
     * <p>
     * This <strong>must</strong> be used if you need to set the page before the
     * views are drawn on screen (e.g., default start page).
     * </p>
     * 
     * @param item
     */
    void setCurrentItem(int item);

    /**
     * Set a page change listener which will receive forwarded events.
     * 
     * @param listener
     */
    void setOnPageChangeListener(OnPageChangeListener listener);

    /**
     * Notify the indicator that the fragment list has changed.
     */
    void notifyDataSetChanged();
}
