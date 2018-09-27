package com.arcfun.ahsclient.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BannerPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ImageLoader mLoader;
    private List<String> mUrlList = new ArrayList<String>();

    public BannerPagerAdapter(Context c, List<String> list) {
        this.mContext = c;
        this.mUrlList = list;
        this.mLoader = new ImageLoader();
    }

    @Override
    public int getCount() {
        return mUrlList != null ? mUrlList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView image = new ImageView(mContext);
        mLoader.loadImage(mUrlList.get(position % mUrlList.size()), image);
        container.addView(image);
        return image;
    }
}