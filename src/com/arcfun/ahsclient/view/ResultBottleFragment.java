package com.arcfun.ahsclient.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.utils.LogUtils;

public class ResultBottleFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Result|Bottle";
    private OnActionCallBack mListener;
    private int mIndex;
    //private TextView mResultTips, mResultWeight, mResultSum, mResultTotal;
    private Button mFinishBtn;

    public ResultBottleFragment() {
    }

    public ResultBottleFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_open_result2_fragment,
                container, false);
        /*mResultTips = (TextView) view.findViewById(R.id.result_unit);
        mResultWeight = (TextView) view.findViewById(R.id.result_weight);
        mResultSum = (TextView) view.findViewById(R.id.result_sum);
        mResultTotal = (TextView) view.findViewById(R.id.total_credit);*/
        mFinishBtn = (Button) view.findViewById(R.id.open_finish);
        mFinishBtn.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.open_finish:
            if (mListener != null) {
                // mListener.onUpdate(FRAGMENT_LOGIN_WEIXIN_1);
            }
            break;

        default:
            break;
        }
    }

}