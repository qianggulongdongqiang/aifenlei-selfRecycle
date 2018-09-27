package com.arcfun.ahsclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.BannerPagerAdapter;
import com.arcfun.ahsclient.data.DeviceInfo;
import com.arcfun.ahsclient.data.QrCode;
import com.arcfun.ahsclient.data.ResultInfo;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;

public class HomeMainActivity extends AhsBaseActivity implements
        OnClickListener {
    private static final String TAG = "Home";
    private Button mJoinFree, mJoinCredit;
    private ViewPager mViewPager;
    private BannerPagerAdapter mAdapter;
    private List<String> mBannerList;
    private AsyncTask<String, Void, String> mTask;
    private QrCode mCode;
    private String mToken;

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
        requestSlideList(Utils.buildSlideListJson());
        if (!SharedPreferencesUtils.getRegister(this)) {
            requestSetPushCode(this, Utils.buildPushCode(mToken,
                    Utils.getRegistrationID(this)));
        }
        LogUtils.d(TAG, "onResume " + mViewPager.getChildCount());
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            requestLogin(Utils.buildLoginJson(Utils.getSerialNo()));
        }
    }

    private void initView() {
        mJoinFree = (Button) findViewById(R.id.join_free);
        mJoinCredit = (Button) findViewById(R.id.join_credit);
        mViewPager = (ViewPager) findViewById(R.id.banners);
        mAdapter = new BannerPagerAdapter(this, mBannerList);
        mViewPager.setAdapter(mAdapter);
        //slowViewPager();
        mJoinFree.setOnClickListener(this);
        mJoinCredit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.join_free:
            if (mToken.isEmpty()) {
                Utils.showMsg(this, getResources().getString(R.string.network_exception));
                return;
            }
            Intent work = new Intent(this, WorkMainActivity.class);
            work.putExtra("isGuest", true);
            startActivity(work);
            break;
        case R.id.join_credit:
            if (mToken.isEmpty()) {
                Utils.showMsg(this, getResources().getString(R.string.network_exception));
                return;
            }
            Intent intent = new Intent(this, LoginMainActivity.class);
            startActivity(intent);
            break;

        default:
            break;
        }
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
                    for (String banner : banners) {
                        mBannerList.add(banner);
                    }
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
}