package com.arcfun.ahsclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.ImageLoader;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.ui.PopWelcomeDialog.OnWelcomeListener;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class HomeListActivity extends BaseActivity implements Listener,
        OnWelcomeListener {
    // ABC11111111 ABC1111112
    private static String[] rfids = {"ABC11111111"};
    private List<String> mRfidList = new ArrayList<String>();
    private HashMap<String, Float> epcList = null;
    private boolean mIsChecked = false;// RFID
    private String[] mBestRfid = null;
    private List<OwnerInfo> mOwnerList;
    private OwnerInfo mBestInfo;
    private ImageView mBanner1, mBanner2, mBanner3;
    private List<ImageView> mBannerList = new ArrayList<ImageView>();
    private ImageLoader mImageLoader;
    private Dialog mDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            String[] data = (String[]) msg.obj;
            switch (msg.what) {
                case MSG_UPDATE_ACCOUNT:
                    try {
                        updateEpcMap(data);
                    } catch (Exception e) {
                        Utils.showMsg(HomeListActivity.this, e.toString());
                    }
                    break;
                case MSG_UPDATE_DEBUG:
                    Utils.showMsg(HomeListActivity.this, data[0] + data[1]);
                    break;
                case MSG_UPDATE_WELCOME:
                    createDialog();
                    break;
                case MSG_UPDATE_WELCOME_LIST:
                    getBestRfid();
                    getCustomerByRfids(Utils.buildRfidJson(getRfidList()), true);
                    break;
                case MSG_UPDATE_RFID_NOEXIST:
                    handleRfidNoExist();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ((AhsApplication) getApplication()).addActivity(this);

        mImageLoader = new ImageLoader();
        //bindView();
        initView();
        requestSlideList(Utils.buildSlideListJson(), mImageLoader, mBannerList);
        initData();
    }

    private void initData() {
        mBestInfo = null;//reset best info
        try {
            requestURL();
            mReaderHelper2.setListener(this);
            intPort();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCustomerByRfids(Utils.buildRfidJson(getRfidList()), false);
        getBestCustomerByRfid(Utils.buildRfidJson(getBestRfid()));
        mHandler.removeMessages(MSG_UPDATE_RFID_NOEXIST);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(
                MSG_UPDATE_RFID_NOEXIST), 2000);
    }

    private void initView() {
        LinearLayout title = (LinearLayout) findViewById(R.id.title_ll);
        title.setVisibility(View.GONE);
        LinearLayout layout = (LinearLayout) findViewById(R.id.slide_List_layout);
        mBanner1 = (ImageView) findViewById(R.id.banner1);
        mBanner2 = (ImageView) findViewById(R.id.banner2);
        mBanner3 = (ImageView) findViewById(R.id.banner3);
        mBannerList.add(mBanner1);
        mBannerList.add(mBanner2);
        mBannerList.add(mBanner3);
        layout.setOnClickListener(this);
    }

    private void getBestCustomerByRfid(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_CUSTOMER_BY_RFIDS;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                LogUtils.d(TAG, "BestCustomer data =" + data);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "BestCustomer:" + result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    List<OwnerInfo> best = Utils.parseOwnerData(result);
                    if (best != null && best.size() > 0) {
                        mBestInfo = best.get(0);
                        handleDialogCreate();
                    }
                }
            }
        }.execute(url);
    }

    protected void getCustomerByRfids(final String json, final boolean show) {
        String url = HttpRequest.URL_HEAD + HttpRequest.GET_CUSTOMER_BY_RFIDS;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                LogUtils.d(TAG, "Customer data =" + data);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "Customer:" + result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                if (result != null && show) {
                    mOwnerList = Utils.parseOwnerData(result);
                    createListDialog(mOwnerList);
                }
            }
        }.execute(url);
    }

    @Override
    public void onClick(View v) {}

    private void handleRfidNoExist () {
        if (mBestInfo == null) {
            if (mOwnerList != null && mOwnerList.size() > 0) {
                mBestInfo = mOwnerList.get(0);
                handleDialogCreate();
            }
        }
    }

    private List<String> getRfidList() {
        if (epcList == null) {
            epcList = new HashMap<String, Float>();
        }
        if (epcList.size() == 0) {
            for (int i = 0; i < rfids.length; i++) {
                epcList.put(rfids[i], -10.0f * (9 + i));
            }
        }
        LogUtils.d(TAG, "getRfidList epcList=" + epcList.size() + ",rfid=" + rfids.length);
        if (epcList.size() > 0) {
            mRfidList.clear();
            Iterator<String> iterator = epcList.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key != null) {
                    mRfidList.add(key);
                }
            }
        }
        return mRfidList;
    }

    private void handleDialogCreate() {
        LogUtils.d(TAG, "handleDialogCreate()");
        mIsChecked = false;
        mHandler.removeMessages(MSG_UPDATE_WELCOME);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(
                MSG_UPDATE_WELCOME), 100);
    }

    private void handleListDialogCreate() {
        mHandler.removeMessages(MSG_UPDATE_WELCOME_LIST);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(
                MSG_UPDATE_WELCOME_LIST), 1000);
    }

    private void createDialog() {
        mIsChecked = true;
        if (mBestInfo != null && !this.isFinishing()) {
            if (mDialog != null && mDialog.isShowing()) {
                return;
            }
            mDialog = new PopWelcomeDialog(this, mBestInfo);
            mDialog.show();
        }
    }

    private void createListDialog(List<OwnerInfo> infos) {
        mIsChecked = true;
        if (infos != null && infos.size() > 0 && !this.isFinishing()) {
            Dialog dialog = new PopWelcomeListDialog(this, infos);
            dialog.show();
        }
    }

    private String getBestRfid() {
        LogUtils.d(TAG, "best=" + (mBestRfid !=null ? mBestRfid[1]:
                "null") + ", list=" + mRfidList.get(0));
        if (mBestRfid != null) {
            return mBestRfid[1];
        }
        return mRfidList.get(mRfidList.size() - 1);
    }

    private void updateEpcMap(String[] info) {
        if (epcList == null) {
            epcList = new HashMap<String, Float>();
        }
        float value = Float.valueOf(info[0]);
        if (mBestRfid == null || Float.valueOf(mBestRfid[0]) < value) {
            mBestRfid = info;
        }
        if (epcList.isEmpty() || !epcList.containsKey(info[1])) {
            epcList.put(info[1], value);
        }
    }

    private void clear() {
        epcList.clear();
    }

    @Override
    public void onLostConnect(int type) {
        Toast.makeText(this, getResources().getString(R.string.rs232_lost_connect),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRawInfo(int type, byte[] receiveData) {
        /*String[] raw = new String[2];
        Message msg = mHandler.obtainMessage(MSG_UPDATE_DEBUG);
        raw[0] = "" + receiveData.length;
        raw[1] = "\n" + StringTool.byteArrayToString(receiveData, 0, receiveData.length);
        msg.obj = raw;
        msg.sendToTarget();*/
    }

    @Override
    public void onEPCInfo(int type, byte[] epcData) {
        if (mIsChecked) {
            return;
        }
        String[] epc = new String[2];
        String strLog = StringTool.byteArrayToString(epcData, 0, Utils.LENGTH_EPC);
        epc[0] = Utils.decode(StringTool.byteArrayToString(epcData,
                Utils.LENGTH_RAW_RFID - Utils.LENGTH_RSSI - 1, Utils.LENGTH_RSSI));
        epc[1] = Utils.decode(strLog.replaceAll(" ", ""));
        try {
            updateEpcMap(epc);
        } catch (Exception e) {
            Utils.showMsg(HomeListActivity.this, e.toString());
        }
    }

    @Override
    public void onDEVInfo(int type, byte[] btAryReceiveData) {}

    @Override
    public void onQRCInfo(int type, byte[] btData) {
    }

    @Override
    public void onSuccess(int type, OwnerInfo info) {}

    @Override
    public void onNext(int type) {
        mIsChecked = false;
        LogUtils.d(TAG, "onNext type = " + type);
        if (type == 1) {
            clear();
        }
        handleListDialogCreate();
    }

    @Override
    protected void onDestroy() {}
}