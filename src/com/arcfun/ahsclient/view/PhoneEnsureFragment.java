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
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.Utils;

public class PhoneEnsureFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Phone|Ensure";
    private OnActionCallBack mListener;
    private int mIndex;
    private OwnerInfo mInfo;
    private TextView mAccount, mName, mNumber;
    private Button mOk, mBack;

    public PhoneEnsureFragment() {
    }

    public PhoneEnsureFragment(OnActionCallBack listener, OwnerInfo info,
            int index) {
        this.mListener = listener;
        this.mInfo = info;
        this.mIndex = index;
    }

    public void setInfo(OwnerInfo info) {
        this.mInfo = info;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_login_ensure_fragment,
                container, false);
        mAccount = (TextView) view.findViewById(R.id.account_title);
        mName = (TextView) view.findViewById(R.id.account_name);
        mNumber = (TextView) view.findViewById(R.id.account_number);
        mOk = (Button) view.findViewById(R.id.account_login);
        mBack = (Button) view.findViewById(R.id.account_switch);
        mOk.setOnClickListener(this);
        mBack.setOnClickListener(this);
        LogUtils.d(TAG, "onCreateView " + mIndex);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccount.setText(getActivity().getResources().getString(
                R.string.login_phone_title1));
        if (mInfo != null) {
            mName.setText(mInfo.getNickName());
            mNumber.setText("(" + Utils.formatPhoneNumber(mInfo.getMobile())
                    + ")");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged mIndex = " + mIndex);
        if (mInfo != null && !hidden) {
            mName.setText(mInfo.getNickName());
            mNumber.setText("(" + Utils.formatPhoneNumber(mInfo.getMobile())
                    + ")");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.account_login:
            if (mListener != null) {
                mListener.onUpdate(mIndex + 1, null);
            }
            break;
        case R.id.account_switch:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_LOGIN_PHONE_1, null);
            }
            break;

        default:
            break;
        }
    }

}