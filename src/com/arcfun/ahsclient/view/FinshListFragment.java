package com.arcfun.ahsclient.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.PackageInfo;
import com.arcfun.ahsclient.utils.LogUtils;

public class FinshListFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Finsh|Finsh";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mResultType, mResultUnit, mResultWeight, mResultSum,
            mResultTotal;
    private Button mFinishBtn;
    private PackageInfo mPackageInfo;

    public FinshListFragment() {
    }

    public FinshListFragment(OnActionCallBack listener,
            PackageInfo info, int index) {
        this.mListener = listener;
        this.mIndex = index;
        this.mPackageInfo = info;
    }

    public void setInfo(PackageInfo mInfo) {
        this.mPackageInfo = mInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_finish2_fragment, container,
                false);
        mResultType = (TextView) view.findViewById(R.id.result_type);
        mResultUnit = (TextView) view.findViewById(R.id.result_unit);
        mResultWeight = (TextView) view.findViewById(R.id.result_weight);
        mResultSum = (TextView) view.findViewById(R.id.result_sum);
        mResultTotal = (TextView) view.findViewById(R.id.total_credit);
        mFinishBtn = (Button) view.findViewById(R.id.open_finish);
        mFinishBtn.setOnClickListener(this);
        if (mPackageInfo != null) {
            LogUtils.d(TAG, "result " + mPackageInfo.toString());
            mResultType.setText(mPackageInfo.getName());
            if (mPackageInfo.getId() == 30 || mPackageInfo.getId() == 33
                    || mPackageInfo.getId() == 36) {
                mResultUnit.setText(getString(R.string.history_unit, mPackageInfo.getUnit()));
            } else {
                mResultUnit.setText(getString(R.string.history_unit2, mPackageInfo.getUnit()));
            }
            mResultWeight.setText(getString(R.string.history_weight,
                    mPackageInfo.getWeight()));
            mResultSum.setText(getString(R.string.history_credit,
                    mPackageInfo.getTotal()));
            mResultTotal.setText(getString(R.string.history_credit,
                    mPackageInfo.getTotal()));
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
        case R.id.open_finish:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_EXIT, null);
            }
            break;

        default:
            break;
        }
    }

}