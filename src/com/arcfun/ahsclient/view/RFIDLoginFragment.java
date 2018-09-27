package com.arcfun.ahsclient.view;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderHelper.Listener;

public class RFIDLoginFragment extends BaseLoginFragment implements
        OnClickListener, Listener {
    private static final String TAG = "RFID|Login";
    private OnActionCallBack mListener;
    private int mIndex;
    private ImageView mSwitchQr;
    private TextView mSwitchPhone;

    public RFIDLoginFragment() {
    }

    public RFIDLoginFragment(OnActionCallBack listener, int index) {
        this.mListener = listener;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_rfid_fragment, container,
                false);
        mSwitchQr = (ImageView) view.findViewById(R.id.switch_qr);
        mSwitchPhone = (TextView) view.findViewById(R.id.switch_rfid_phone);
        mSwitchQr.setOnClickListener(this);
        mSwitchPhone.setOnClickListener(this);
        Drawable right = getResources().getDrawable(R.drawable.icon_enter);
        int size = getResources().getDimensionPixelSize(R.dimen.enter_btn_size);
        right.setBounds(0, 0, size, size);
        mSwitchPhone.setCompoundDrawables(null, null, right, null);
        LogUtils.d(TAG, "onCreateView " + mIndex);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG, "onActivityCreated " + mIndex);
        try {
            mReaderHelper2 = ReaderHelper.getEpcHelper();
            initSerialPort2();
            mReaderHelper2.setListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged hidden = " + hidden);
        if (hidden) {
            closeSerialPort2();
            mReaderHelper2.setListener(null);
            getCustomerByRfids("");//TODO
        } else {
            initSerialPort2();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.switch_qr:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_LOGIN_WEIXIN_1, null);
            }
            break;
        case R.id.switch_rfid_phone:
            if (mListener != null) {
                mListener.onUpdate(FRAGMENT_LOGIN_PHONE_1, null);
            }
            break;

        default:
            break;
        }
    }

    private void getCustomerByRfids(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_CUSTOMER_BY_RFIDS;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                LogUtils.d(TAG, "customer data =" + data);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "customer:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
            };
        }.execute(url);
    }

    @Override
    public void onLostConnect(int type) {
        Utils.showMsg(getActivity(), "onLostConnect");
    }

    @Override
    public void onRawInfo(int type, byte[] receiveData) {
    }

    @Override
    public void onEPCInfo(int type, byte[] receiveData) {
        String data = StringTool.byteArrayToString(receiveData, 0,
                receiveData.length);
        Utils.showMsg(getActivity(), "onEPCInfo:" + data);
    }

    @Override
    public void onDEVInfo(int type, byte[] receiveData) {}

    @Override
    public void onQRCInfo(int type, byte[] btData) {
    }
}