package com.arcfun.ahsclient.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.data.ProductAndOwnerInfo;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.Utils;

public class ResultFinshFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Result|Finsh";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mOwner, mCredit, mTotal;
    private Button mFinishBtn;
    private ProductAndOwnerInfo mInfo;

    public ResultFinshFragment() {
    }

    public ResultFinshFragment(OnActionCallBack listener,
            ProductAndOwnerInfo info, int index) {
        this.mListener = listener;
        this.mIndex = index;
        this.mInfo = info;
    }

    public void setInfo(ProductAndOwnerInfo mInfo) {
        this.mInfo = mInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_finish_fragment, container,
                false);
        mOwner = (TextView) view.findViewById(R.id.finish_owner);
        mCredit = (TextView) view.findViewById(R.id.finish_credit);
        mTotal = (TextView) view.findViewById(R.id.finish_total);
        mFinishBtn = (Button) view.findViewById(R.id.result_finish);
        mFinishBtn.setOnClickListener(this);
        OwnerInfo owner = mInfo.getOwnerInfo();
        if (owner != null) {
            mOwner.setText(owner.getNickName() + "("
                    + Utils.formatPhoneNumber(owner.getMobile()) + ")");
            mCredit.setText(String.valueOf(mInfo.getCredit()));
            mTotal.setText(String.valueOf(owner.getScore()));
        }

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
        case R.id.result_finish:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_EXIT, null);
            }
            break;

        default:
            break;
        }
    }

}