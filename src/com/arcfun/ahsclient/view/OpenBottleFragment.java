package com.arcfun.ahsclient.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.utils.LogUtils;

public class OpenBottleFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Open|Bottle";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mOpenTips, mOpenMsg, mOpenStart, mOpenEnd;
    private Button mFinishBtn;
    private int weight = 0;

    public OpenBottleFragment() {
    }

    public OpenBottleFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_open1_fragment, container,
                false);
        mOpenTips = (TextView) view.findViewById(R.id.open_msg);
        mOpenMsg = (TextView) view.findViewById(R.id.open_detail);
        mOpenStart = (TextView) view.findViewById(R.id.open_detail_s);
        mOpenEnd = (TextView) view.findViewById(R.id.open_detail_e);
        mFinishBtn = (Button) view.findViewById(R.id.open_finish);
        mFinishBtn.setOnClickListener(this);
        mOpenTips.setText(getString(R.string.open_msg1));
        mOpenStart.setText(getString(R.string.open_result_s1));
        mOpenEnd.setText(getString(R.string.open_result_e1));
        updateNum(weight++);
        LogUtils.d(TAG, "onCreateView " + mIndex);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG, "onActivityCreated mIndex = " + mIndex);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        LogUtils.d(TAG, "onHiddenChanged mIndex = " + mIndex);
    }

    private void updateNum(int num) {
        mOpenMsg.setText(String.valueOf(num));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.open_finish:
            updateNum(weight++);
            if (mListener != null) {
                // mListener.onUpdate(FRAGMENT_LOGIN_WEIXIN_1);
            }
            break;

        default:
            break;
        }
    }

}