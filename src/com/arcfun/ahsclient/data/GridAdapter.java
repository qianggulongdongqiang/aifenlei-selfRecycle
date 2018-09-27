package com.arcfun.ahsclient.data;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.ImageLoader.ViewHolder;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.Utils;

public class GridAdapter extends BaseAdapter {
    private static final String TAG = "GridAdapter";
    private GoodInfo[] mInfoList;
    private Context mContext;
    private ImageLoader mImageLoader;
    private int mPage = -1;
    private int mSelection = -1;

    public GridAdapter(Context context, List<GoodInfo> goods, int page) {
        this.mContext = context;
        mPage = page;
        int start = page * Utils.MAX_GRID_VIEW_NUM;
        int end = (page + 1) * Utils.MAX_GRID_VIEW_NUM;
        int number = Math.min((goods.size() - start), Utils.MAX_GRID_VIEW_NUM);
        LogUtils.d(TAG, "start=" + start + ",end=" + end + ",number=" + number
                + "@" + goods.size());
        mInfoList = new GoodInfo[number];
        int index = 0;
        while ((start < goods.size()) && (start < end)) {
            mInfoList[index] = goods.get(start);
            index++;
            start++;
        }
        mImageLoader = new ImageLoader();
    }

    public int getPageCount() {
        return mPage;
    }

    public void revertSelection(int select) {
        if (mSelection != select) {
            setSelection(select);
        } else {
            resetSelection();
        }
    }

    public void setSelection(int select) {
        mSelection = select;
    }

    public void resetSelection() {
        mSelection = -1;
    }

    public boolean isSelected() {
        return mSelection != -1;
    }

    @Override
    public int getCount() {
        return mInfoList.length;
    }

    @Override
    public Object getItem(int position) {
        return mInfoList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.gridview_item, null);
            viewHolder.iv_icon = (ImageView) convertView
                    .findViewById(R.id.item_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mSelection == position) {
            viewHolder.iv_icon.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.action_bar_text_color));
        } else {
            viewHolder.iv_icon.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.transparent));
        }
        mImageLoader.loadImage(mInfoList[position].getImage_2(), this,
                viewHolder);

        return convertView;
    }

}
