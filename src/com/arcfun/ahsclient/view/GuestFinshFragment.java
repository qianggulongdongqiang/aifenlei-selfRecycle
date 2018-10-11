package com.arcfun.ahsclient.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.utils.LogUtils;

public class GuestFinshFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Guest|Finsh";
    private OnActionCallBack mListener;
    private int mIndex;
    private Button mFinishBtn;

    public GuestFinshFragment() {
    }

    public GuestFinshFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_guest_finish_fragment, container,
                false);
        mFinishBtn = (Button) view.findViewById(R.id.result_finish);
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