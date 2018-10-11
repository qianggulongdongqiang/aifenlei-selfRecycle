package com.arcfun.ahsclient.ui;

import java.io.File;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.arcfun.ahsclient.data.ResultInfo;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.reader.helper.ReaderHelper;
import com.uhf.uhf.serialport.SerialPort;

public class AhsBaseActivity extends FragmentActivity implements
        OnClickListener {
    protected static final String TAG = "AhsBaseActivity";
    protected ReaderHelper mReaderHelper1, mReaderHelper2, mReaderHelper3,
            mReaderHelper4;
    protected SerialPort mSerialPort1, mSerialPort2, mSerialPort3,
            mSerialPort4;

    protected static final int MSG_UPDATE_DEBUG = 1;
    protected static final int MSG_SCROLL_MSG = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mReaderHelper1 = ReaderHelper.getDevHelper();
            mReaderHelper2 = ReaderHelper.getEpcHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void bindView() {
    }

    protected void initSerialPort1() {
        LogUtils.d(TAG, "initSerialPort1");
        try {
            mSerialPort1 = new SerialPort(new File(SharedPreferencesUtils.getPort(this, 1)),
                    Utils.BAUD_RATE_DEF, 0);
            mReaderHelper1.setReader(mSerialPort1.getInputStream(),
                    mSerialPort1.getOutputStream(), Constancts.TYPE_DEV);
        } catch (Exception e) {
        }
    }

    protected void initSerialPort2() {
        LogUtils.d(TAG, "initSerialPort2");
        try {
            mSerialPort2 = new SerialPort(new File(SharedPreferencesUtils.getPort(this, 2)),
                    Utils.BAUD_RATE_UHF, 0);
            mReaderHelper2.setReader(mSerialPort2.getInputStream(),
                    mSerialPort2.getOutputStream(), Constancts.TYPE_EPC);
        } catch (Exception e) {
        }
    }

    protected void initSerialPort3() {
        LogUtils.d(TAG, "initSerialPort3");
        try {
            mSerialPort3 = new SerialPort(new File(Utils.SERIAL_PROT_3),
                    Utils.BAUD_RATE_DEF, 0);
            mReaderHelper3.setReader(mSerialPort3.getInputStream(),
                    mSerialPort3.getOutputStream(), Constancts.TYPE_QRC);
        } catch (Exception e) {
        }
    }

    protected void initSerialPort4() {
        LogUtils.d(TAG, "initSerialPort4");
        try {
            mSerialPort4 = new SerialPort(new File(Utils.SERIAL_PROT_4),
                    Utils.BAUD_RATE_DEF, 0);
            mReaderHelper4.setReader(mSerialPort4.getInputStream(),
                    mSerialPort4.getOutputStream(), Constancts.TYPE_QRC);
        } catch (Exception e) {
        }
    }

    protected void closeSerialPort1() {
        LogUtils.d(TAG, "closeSerialPort1");
        mSerialPort1 = null;
    }

    protected void closeSerialPort2() {
        LogUtils.d(TAG, "closeSerialPort2");
        mSerialPort2 = null;
    }

    @Override
    public void onClick(View v) {
    }

    protected void requestSyncState(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.MACHINE_SET_STATE;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "requestSyncState:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                ResultInfo resultInfo = null;
                if (result != null) {
                    resultInfo = Utils.parsePushCode(result);
                }
                if (resultInfo != null) {
                    Utils.showMsg(AhsBaseActivity.this, resultInfo.getMsg());
                }
            }
        }.execute(url);
    }
}