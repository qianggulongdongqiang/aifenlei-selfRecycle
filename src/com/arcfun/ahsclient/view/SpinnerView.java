package com.arcfun.ahsclient.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.SpinnerArrayAdapter;

public class SpinnerView extends RelativeLayout {
    private Context context;
    private List<String> mListData = new ArrayList<String>();
    private ListView mListView;
    private SpinnerArrayAdapter adapter;
    private PopupWindow popupWindow;
    private TextView mSpinnerText;
    private Button mSpinnerIcon;
    private RelativeLayout mLayout;
    private LayoutInflater mInflater;
    private onSpinnerSelectedListener mListener;
    private Object mObj;
    private int currentId = 0;

    public SpinnerView(Context context) {
        this(context, null);
        this.context = context;
    }

    public interface onSpinnerSelectedListener {
        void onSelected(Object obj, int pos);
    }

    public SpinnerView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mLayout = (RelativeLayout) mInflater.inflate(R.layout.spinner_view, this);
        mSpinnerText = (TextView) findViewById(R.id.text_spinner);
        mSpinnerIcon = (Button) findViewById(R.id.image_spinner);
    }

    public void setData(List<String> data, Object obj,
            onSpinnerSelectedListener listener) {
        mListener = listener;
        mObj = obj;
        if (data == null || data.size() == 0) return;
        this.mListData = data;
        adapter = new SpinnerArrayAdapter(context, mListData);
        mSpinnerText.setText((CharSequence) adapter.getItem(0));
        mSpinnerIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWindow((View)v.getParent());
            }
        });
        mLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWindow(v);
            }
        });
    }

    public String getText() {
        return mSpinnerText.getText().toString();
    }

    public int getSelectedID() {
        return currentId;
    }

    public void setText(String text) {
        mSpinnerText.setText(text);
    }

    public void showWindow(View view) {
        mListView = (ListView) mInflater.inflate(
                R.layout.spinner_listview, this, false);
        mListView.setAdapter(adapter);
        popupWindow = new PopupWindow(view);
        popupWindow.setWidth(getWidth());
        popupWindow.setHeight(getWidth() * 2);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context
                .getResources()));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(mListView);
        popupWindow.showAsDropDown(view, 0, 0);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                    long id) {
                currentId = pos;
                mSpinnerText.setText(mListData.get(pos));
                if (mListener != null) {
                    mListener.onSelected(mObj, pos);
                }
                popupWindow.dismiss();
                popupWindow = null;
            }
        });
    }

}