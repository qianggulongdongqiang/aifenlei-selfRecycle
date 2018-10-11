package com.arcfun.ahsclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.BannerPagerAdapter;
import com.arcfun.ahsclient.data.DeviceInfo;
import com.arcfun.ahsclient.data.QrCode;
import com.arcfun.ahsclient.data.ResultInfo;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.ui.GuestWelcomeDialog.OnChooseListener;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.reader.base.CMD;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class HomeMainActivity extends AhsBaseActivity implements
        OnClickListener, OnChooseListener, OnLongClickListener, Listener {
    private static final String TAG = "Home";
    private Button mJoinFree, mJoinCredit;
    private ViewPager mViewPager;
    private BannerPagerAdapter mAdapter;
    private LinearLayout mJoinLayout, mExceptionLayout;
    private List<String> mBannerList;
    private AsyncTask<String, Void, String> mTask;
    private float mBaseWeight = 0f;
    private QrCode mCode;
    private String mToken;
    private int mPageCount = 0;
    private boolean isFull;

    private int mDiffDownTime = 5 * 1000;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_SCROLL_MSG:
                int next = mViewPager.getCurrentItem() + 1;
                mViewPager.setCurrentItem(mPageCount == 0 ? 0 : next
                        % mPageCount);
                startLoop(mDiffDownTime);
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_home_main);
        ((AhsApplication) getApplication()).addActivity(this);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFull = SharedPreferencesUtils.getState(this) == 40;
        updateState(isFull);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initSerialPort1();
            }
        }).start();
        mReaderHelper1.setListener(this);
        requestSlideList(Utils.buildSlideListJson());
        if (!SharedPreferencesUtils.getRegister(this)) {
            requestSetPushCode(this, Utils.buildPushCode(mToken,
                    Utils.getRegistrationID(this)));
        }
        mPageCount = mBannerList.size();
        LogUtils.d(TAG, "onResume " + mPageCount);
        startLoop(mDiffDownTime);
    }

    private void updateState(boolean full) {
        mJoinLayout.setVisibility(full ? View.GONE : View.VISIBLE);
        mExceptionLayout.setVisibility(full ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReaderHelper1.setListener(null);
        mHandler.removeMessages(MSG_SCROLL_MSG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTask != null) {
            mTask.cancel(false);
        }
    }

    private void initData() {
        mBannerList = new ArrayList<String>();
        mToken = SharedPreferencesUtils.getToken(getApplicationContext());
        if (mToken.isEmpty()) {
            requestLogin(Utils.buildLoginJson(Utils.getImei(this)));
        }
    }

    private void initView() {
        mJoinLayout = (LinearLayout) findViewById(R.id.join_ll);
        mExceptionLayout = (LinearLayout) findViewById(R.id.join_exception);
        mJoinFree = (Button) findViewById(R.id.join_free);
        mJoinCredit = (Button) findViewById(R.id.join_credit);
        mViewPager = (ViewPager) findViewById(R.id.banners);
        mAdapter = new BannerPagerAdapter(this, mBannerList);
        mViewPager.setAdapter(mAdapter);
        mJoinFree.setOnClickListener(this);
        mJoinCredit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.join_free:
            if (mToken.isEmpty()) {
                Utils.showMsg(this, getResources().getString(R.string.network_exception));
                requestLogin(Utils.buildLoginJson(Utils.getImei(this)));
                if (!SharedPreferencesUtils.getDebug(this)) {
                    return;
                }
            }
            GuestWelcomeDialog dialog = new GuestWelcomeDialog(this);
            dialog.show();
            break;
        case R.id.join_credit:
            if (mToken.isEmpty()) {
                Utils.showMsg(this, getResources().getString(R.string.network_exception));
                requestLogin(Utils.buildLoginJson(Utils.getImei(this)));
                if (!SharedPreferencesUtils.getDebug(this)) {
                    return;
                }
            }
            mHandler.removeMessages(MSG_SCROLL_MSG);
            Intent intent = new Intent(this, LoginMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            break;

        default:
            break;
        }
    }

    public void startLoop(int delay) {
        mHandler.removeMessages(MSG_SCROLL_MSG);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL_MSG, delay);
    }

    protected void requestSlideList(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_SLIDE_LIST;
        mTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null || data.length == 0)
                    return "";

                Utils.dumpJson2Db(getApplicationContext(), data, "slides");
                String result = new String(data);
                LogUtils.d(TAG, "SlideList:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                List<String> banners;
                if (result == null || result.isEmpty()) {
                    byte[] raw = Utils.getdumpFromDb(getApplicationContext(),
                            "slides");
                    if (raw != null) {
                        LogUtils.d(TAG, "onPostExecute slides from Db");
                        result = new String(raw);
                    }
                    Toast.makeText(HomeMainActivity.this,
                            getString(R.string.network_exception),
                            Toast.LENGTH_LONG).show();
                }
                banners = Utils.parseSlideList(result);
                if (banners != null && banners.size() > 0) {
                    mBannerList.clear();
                    for (String banner : banners) {
                        mBannerList.add(banner);
                    }
                    mPageCount = mBannerList.size();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }.execute(url);
    }

    private void requestLogin(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.DEVICE_LOGIN;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "requestLogin: " + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    DeviceInfo info = Utils.parseLoginResult(result);
                    if (info != null && info.getCode() == Utils.RESULT_OK) {
                        mToken = info.getToken();
                        SharedPreferencesUtils.setDeviceInfo(
                                HomeMainActivity.this, info);
                        requestUrl(Utils.buildQrJson(info.getToken()));
                    }
                }
            }
        }.execute(url);
    }

    private void requestUrl(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_QR_CODE;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    mCode = Utils.parseQrResult(result);
                    if (mCode != null && mCode.getCode() == Utils.RESULT_OK) {
                        LogUtils.d(TAG, "requestUrl: " + mCode.getUrl());
                        buildQrFile(mCode.getUrl());
                        SharedPreferencesUtils.setQrCode(HomeMainActivity.this,
                                mCode.getUrl());
                    }
                }
            }
        }.execute(url);
    }

    private void requestSetPushCode(final Context c, final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.SET_PUSH_CODE;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "requestSetPushCode:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                ResultInfo resultInfo = null;
                if (result != null) {
                    resultInfo = Utils.parsePushCode(result);
                }
                if (resultInfo != null) {
                    LogUtils.d(TAG, "requestSetPushCode Registration Id sucess.");
                    SharedPreferencesUtils.setRegister(c,
                            resultInfo.getCode() == Utils.RESULT_OK);
                }
            }
        }.execute(url);
    }

    private void buildQrFile(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utils.createQRcode(url, Constancts.PIX_QR_CODE,
                        Constancts.PIX_QR_CODE, null);
            }
        }).start();
    }

    @Override
    public void onNext(int type) {
        ((AhsApplication) getApplication()).setWeight(mBaseWeight);
        if (type == 1) {
            Intent work = new Intent(this, WorkMainActivity.class);
            work.putExtra("isGuest", true);
            work.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(work);
        } else {
            if (mToken.isEmpty()) {
                Utils.showMsg(this, getResources().getString(R.string.network_exception));
                requestLogin(Utils.buildLoginJson(Utils.getImei(this)));
                return;
            }
            Intent intent = new Intent(this, LoginMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    @Override
    public void onLostConnect(int type) {}

    @Override
    public void onRawInfo(int type, byte[] btData) {}

    @Override
    public void onDEVInfo(int type, byte[] btData) {
        String[] data = StringTool.encodeHexToArrays(btData);
        handleDevInfo(data);
    }

    private void handleDevInfo(String[] data) {
        if (data == null || data.length < Constancts.LENGTH_DEV) {
            return;
        }
        if (CMD.NAME_UNSOLICETD_SYNC.equals(data[1])) {
            if (isFull != data[2].equals("01")) {
                isFull = data[2].equals("01");
                String token = SharedPreferencesUtils.getToken(this);
                SharedPreferencesUtils.setState(this, 40);
                requestSyncState(Utils.buildStateCode(token, 40));
                updateState(isFull);
            }
        }
        if (CMD.NAME_UNSOLICETD_SYNC.equals(data[1])
                || CMD.NAME_UNSOLICETD_WEIGHT.equals(data[1])) {
            mBaseWeight = Utils.getWeight(data);
        }
    }

    @Override
    public void onEPCInfo(int type, byte[] btData) {}

    @Override
    public void onQRCInfo(int type, byte[] btData) {}

}