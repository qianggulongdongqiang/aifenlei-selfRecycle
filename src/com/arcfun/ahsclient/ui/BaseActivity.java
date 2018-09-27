package com.arcfun.ahsclient.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.GoodInfo;
import com.arcfun.ahsclient.data.ImageLoader;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.reader.helper.ReaderHelper;
import com.uhf.uhf.serialport.SerialPort;

public class BaseActivity extends Activity implements OnClickListener {
    protected static final String TAG = "BaseActivity";
    protected ReaderHelper mReaderHelper1, mReaderHelper2;
    private SerialPort mSerialPort1, mSerialPort2;
    protected TextView mHomeBtn, mExchangeBtn, mTransactionBtn;
    protected View mExchangeView, mTransactionView;
    private Button mLoginBtn;
    protected List<GoodInfo> mInfos = new ArrayList<GoodInfo>();

    protected static final int MSG_UPDATE_WEIGHT =1;
    protected static final int MSG_UPDATE_ACCOUNT=2;
    protected static final int MSG_UPDATE_DEBUG=3;
    protected static final int MSG_UPDATE_WELCOME=4;
    protected static final int MSG_UPDATE_WELCOME_LIST=5;
    protected static final int MSG_UPDATE_RFID_NOEXIST=6;

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
        mHomeBtn = (TextView) findViewById(R.id.main_btn);
        mExchangeBtn = (TextView) findViewById(R.id.exchange_btn);
        mTransactionBtn = (TextView) findViewById(R.id.transaction_btn);
        mExchangeView = findViewById(R.id.exchange_view);
        mTransactionView = findViewById(R.id.transaction_view);
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mHomeBtn.setOnClickListener(this);
        mTransactionBtn.setOnClickListener(this);
        mExchangeBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {}

    protected void intPort() {
        try { //1
            mSerialPort1 = new SerialPort(
                    new File(Utils.SERIAL_PROT_1), Utils.BAUD_RATE_DEF, 0);
            mReaderHelper1.setReader(mSerialPort1.getInputStream(),
                    mSerialPort1.getOutputStream(), Constancts.TYPE_DEV);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.rs232_error)
                    + Utils.SERIAL_PROT_1, Toast.LENGTH_SHORT).show();
        }
        try { //2
            mSerialPort2 = new SerialPort(
                    new File(Utils.SERIAL_PROT_2), Utils.BAUD_RATE_UHF, 0);
            mReaderHelper2.setReader(mSerialPort2.getInputStream(),
                    mSerialPort2.getOutputStream(), Constancts.TYPE_EPC);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.rs232_error)
                    + Utils.SERIAL_PROT_2, Toast.LENGTH_SHORT).show();
        }
    }

    protected void requestURL() {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_SEC_GOOD;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], "");
                if (data == null || data.length == 0) return "";

                Utils.dumpJson2Db(getApplicationContext(), data, "goods");
                String result = new String(data);
                LogUtils.d(TAG, "SecGoods:" + result);
                mInfos = Utils.parseJsonData(result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result == null || result.isEmpty()) {
                    byte[] raw = Utils.getdumpFromDb(getApplicationContext(), "goods");
                    if (raw != null) {
                        LogUtils.d(TAG, "onPostExecute goods from Db");
                        String dump = new String(raw);
                        mInfos = Utils.parseJsonData(dump);
                    }
                    Toast.makeText(BaseActivity.this, getString(R.string.network_exception),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }.execute(url);
    }

    protected void requestSlideList(final String json, final ImageLoader loader,
            final List<ImageView> views) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_SLIDE_LIST;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null || data.length == 0) return "";

                Utils.dumpJson2Db(getApplicationContext(), data, "slides");
                String result = new String(data);
                LogUtils.d(TAG, "SlideList:" + result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (views == null || loader == null) {
                    return;
                }
                if (result == null || result.isEmpty()) {
                    byte[] raw = Utils.getdumpFromDb(getApplicationContext(), "slides");
                    if (raw != null) {
                        LogUtils.d(TAG, "onPostExecute slides from Db");
                        result = new String(raw);
                    }
                    Toast.makeText(BaseActivity.this, getString(R.string.network_exception),
                            Toast.LENGTH_LONG).show();
                }
                List<String> images = Utils.parseSlideList(result);
                for (int j = 0; j < images.size(); j++) {
                    loader.loadImage(images.get(j), views.get(j));
                }
            }
        }.execute(url);
    }

}