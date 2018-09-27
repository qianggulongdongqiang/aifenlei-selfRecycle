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
import com.arcfun.ahsclient.utils.Utils;

public class RFIDEnsureFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "RFID|Ensure";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mAccount, mName, mNumber, mMsg;
    private Button mOk, mBack;

    public RFIDEnsureFragment() {
    }

    public RFIDEnsureFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_login_ensure_fragment,
                container, false);
        mAccount = (TextView) view.findViewById(R.id.account_title);
        mName = (TextView) view.findViewById(R.id.account_name);
        mNumber = (TextView) view.findViewById(R.id.account_number);
        mMsg = (TextView) view.findViewById(R.id.account_msg);
        mOk = (Button) view.findViewById(R.id.account_login);
        mBack = (Button) view.findViewById(R.id.account_switch);
        mOk.setOnClickListener(this);
        mBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAccount.setText(getActivity().getResources().getString(
                R.string.login_rfid_title2));
        mBack.setText(getString(R.string.login_resume));
        mMsg.setText(getString(R.string.login_rfid_title3_msg));
        mName.setText("Android");
        mNumber.setText(Utils.formatPhoneNumber("18521365719"));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged mIndex = " + mIndex);
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
                // mListener.onUpdate(FRAGMENT_LOGIN_PHONE_1);
            }
            break;

        default:
            break;
        }
    }

}