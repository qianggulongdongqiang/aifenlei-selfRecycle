package com.arcfun.ahsclient.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.reader.base.CMD;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class OpenPackageFragment extends BaseLoginFragment implements
        OnClickListener, OnLongClickListener, Listener {
    private static final String TAG = "Open|Package";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mOpenTips, mOpenMsg, mOpenStart, mOpenEnd;
    private Button mFinishBtn;
    private OwnerInfo mOwnerInfo;
    private float mWeight = 0;
    protected static final int MSG_UPDATE_WEIGHT = 0;

    public OpenPackageFragment() {
    }

    public void setOwnerInfo(OwnerInfo info) {
        this.mOwnerInfo = info;
    }

    public OpenPackageFragment(OnActionCallBack listener, OwnerInfo info,
            int index) {
        this.mListener = listener;
        this.mOwnerInfo = info;
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
        mFinishBtn.setOnLongClickListener(this);
        mOpenTips.setText(getString(R.string.open_msg2));
        mOpenStart.setText(getString(R.string.open_result_s2));
        mOpenEnd.setText(getString(R.string.open_result_e2));
        updateNum(0);
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

    private void updateNum(float num) {
        mOpenMsg.setText(String.valueOf(num));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.open_finish:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_PACKAGE_ENSURE, mOwnerInfo);
            }
            break;

        default:
            break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        updateNum(mWeight += 60);
        if (mOwnerInfo != null) {
            mOwnerInfo.setVendor(mWeight);
        }
        return true;
    }

    @Override
    public void onLostConnect(int type) {
        Utils.showMsg(getActivity(), "onLostConnect");
    }

    @Override
    public void onRawInfo(int type, byte[] btData) {
        
    }

    @Override
    public void onDEVInfo(int type, byte[] devData) {
        String[] data = StringTool.encodeHexToArrays(devData);
        handleDevInfo(data);
    }

    @Override
    public void onEPCInfo(int type, byte[] btData) {}

    @Override
    public void onQRCInfo(int type, byte[] btData) {}

    public void handleDevInfo(String[] data) {
        if (data != null && data.length == Constancts.LENGTH_DEV) {
            if (CMD.NAME_UNSOLICETD_WEIGHT.equals(data[1])) {
                mWeight = Utils.getWeight(data);
                if (mOwnerInfo != null) {
                    mOwnerInfo.setVendor(mWeight);
                }
                mHandler.obtainMessage(MSG_UPDATE_WEIGHT).sendToTarget();
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_WEIGHT:
                updateNum(mWeight);
                break;

            default:
                break;
            }
        }
    };
}