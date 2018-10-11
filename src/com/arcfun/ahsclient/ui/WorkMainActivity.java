package com.arcfun.ahsclient.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.data.PackageInfo;
import com.arcfun.ahsclient.data.ProductAndOwnerInfo;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.CountDownTimerHelper;
import com.arcfun.ahsclient.utils.CountDownTimerHelper.OnFinishListener;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.arcfun.ahsclient.view.BaseLoginFragment.OnActionCallBack;
import com.arcfun.ahsclient.view.GuestFinshFragment;
import com.arcfun.ahsclient.view.OpenBottleFragment;
import com.arcfun.ahsclient.view.OpenPackageFragment;
import com.arcfun.ahsclient.view.ResultBottleFragment;
import com.arcfun.ahsclient.view.ResultFinshFragment;
import com.arcfun.ahsclient.view.ResultPackageFragment;
import com.reader.base.CMD;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class WorkMainActivity extends AhsBaseActivity implements Listener,
        OnFinishListener, OnActionCallBack {
    private static final String TAG = "Work|Main";
    private ImageView mStep2, mStep2End, mStep3;
    private ImageView mBack;
    private TextView mType, mLable;
    /** bottle */
    private OpenBottleFragment mBottleOpenFragment;
    private ResultBottleFragment mBottleResultFragment;

    /** package */
    private OpenPackageFragment mPackageOpenFragment;
    private ResultPackageFragment mPackageResultFragment;

    /** finish */
    private ResultFinshFragment mResultFinishFragment;
    private GuestFinshFragment mGuestFinishFragment;
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private Button mCountDown;
    private CountDownTimerHelper mHelper;

    public static final String POSITION = "position";
    public static final int FRAGMENT_PACKAGE_IN = 50;
    public static final int FRAGMENT_PACKAGE_ENSURE = FRAGMENT_PACKAGE_IN + 1;

    public static final int FRAGMENT_BOTTLE_IN = 60;
    public static final int FRAGMENT_BOTTLE_ENSURE = FRAGMENT_BOTTLE_IN + 1;

    public static final int FRAGMENT_OPEN_DEFAULT = FRAGMENT_PACKAGE_IN;
    public static final int FRAGMENT_FINISH = 70;
    public static final int FRAGMENT_GUEST_FINISH = 80;
    public static final int FRAGMENT_EXIT = 100;

    private int mPosition = FRAGMENT_OPEN_DEFAULT;
    public static final int PERIOD = 200 * 1000;
    public static final int INTERVAL = 1 * 1000;
    private String mToken;
    private OwnerInfo mOwnerInfo;
    private PackageInfo mPackageInfo;
    private int mMachineType = 0;// package
    protected boolean isGuest;
    private boolean isFull = false;
    private float mBase = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_open_home);
        ((AhsApplication) getApplication()).addActivity(this);
        mManager = getSupportFragmentManager();
        initData();
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initSerialPort1();
                openOrClosesHouse(true);
            }
        }).start();
    }

    private void openOrClosesHouse(boolean isOpen) {
        try {
            mReaderHelper1.getReader().sendOpenHouse(isOpen);
        } catch (Exception e) {
        }
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.open_back);
        mType = (TextView) findViewById(R.id.work_type);
        mLable = (TextView) findViewById(R.id.barcode_text);
        mStep2 = (ImageView) findViewById(R.id.open_step2);
        mStep2End = (ImageView) findViewById(R.id.open_step2_divider);
        mStep3 = (ImageView) findViewById(R.id.open_step3);

        mCountDown = (Button) findViewById(R.id.open_count_down);
        mHelper = new CountDownTimerHelper(mCountDown, PERIOD, INTERVAL);
        mHelper.setOnFinishListener(this);
        mBack.setOnClickListener(this);
        setMachineType(mMachineType);
        showFragment(FRAGMENT_OPEN_DEFAULT);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            isGuest = intent.getBooleanExtra("isGuest", false);
            mOwnerInfo = intent.getParcelableExtra("owner_info");
            if (mOwnerInfo == null) {
                mOwnerInfo = new OwnerInfo(0, "Guest", 0, "");
            }
        }
        mMachineType = SharedPreferencesUtils.getMachineType(this);
        mPackageInfo = SharedPreferencesUtils.getMachineInfo(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToken = SharedPreferencesUtils.getToken(this);
        String lable = SharedPreferencesUtils
                .getSignature(getApplicationContext());
        mLable.setText(getString(R.string.title_des4, lable));
        mHelper.start();
        mReaderHelper1.setListener(this);
        mBase = ((AhsApplication) getApplication()).getWeight();;
        LogUtils.d(TAG, "onResume base= " + mBase);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHelper.stop();
        mReaderHelper1.setListener(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_barcode:
            showFragment(getNextId(mPosition));
            break;
        case R.id.open_back:
            if (mPosition == FRAGMENT_OPEN_DEFAULT) {
                if (mOwnerInfo != null && mOwnerInfo.getVendor() > 0) {
                    return;
                }
                try {
                    mReaderHelper1.getReader().sendOpenHouse(false);
                } catch (Exception e) {
                    Utils.showMsg(this, "Close House :" + e.toString());
                }
                if (isGuest) {
                    onBackPressed();
                    return;
                }
                Intent intent = new Intent(WorkMainActivity.this,
                        LoginMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            } else if (mPosition == FRAGMENT_FINISH ||
                    mPosition == FRAGMENT_GUEST_FINISH) {
                try {
                    mReaderHelper1.getReader().sendOpenHouse(false);
                } catch (Exception e) {
                    Utils.showMsg(this, "Close House :" + e.toString());
                }
                Intent intent = new Intent(WorkMainActivity.this,
                        HomeMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            }
            showFragment(getLastId(mPosition));
            break;

        default:
            showFragment(getLastId(mPosition));
            break;
        }
    }

    /**
     * 0 - package -weight; 1 - package - piece; 2 - bottle
     */
    private void setMachineType(int type) {
        switch (type) {
        case 0:
            mType.setText(getString(R.string.status_bar_work2));
            break;
        case 1:
            mType.setText(getString(R.string.status_bar_work3));
            break;
        case 2:
            mType.setText(getString(R.string.status_bar_work1));

        default:
            mType.setText(getString(R.string.status_bar_work2));
            break;
        }
    }

    /**
     * update progress 0-default 1-doing 2-done
     * 
     * @param isDone
     */
    private void updateProgress(int type) {
        switch (type) {
        case 0:
            mStep2.setImageResource(R.drawable.icon_uncompleted);
            mStep2End.setImageResource(R.drawable.line2);
            mStep3.setImageResource(R.drawable.icon_uncompleted);
            break;
        case 1:
            mStep2.setImageResource(R.drawable.icon_completed);
            mStep2End.setImageResource(R.drawable.line1);
            mStep3.setImageResource(R.drawable.icon_uncompleted);
            break;
        case 2:
            mStep2.setImageResource(R.drawable.icon_completed);
            mStep2End.setImageResource(R.drawable.line1);
            mStep3.setImageResource(R.drawable.icon_completed);
            break;

        default:
            break;
        }
    }

    private int getLastId(int index) {
        switch (index) {
        case FRAGMENT_PACKAGE_ENSURE:
            return FRAGMENT_PACKAGE_IN;
        case FRAGMENT_BOTTLE_ENSURE:
            return FRAGMENT_BOTTLE_IN;
        case FRAGMENT_FINISH:
            switch (mMachineType) {
            case 0:
                return FRAGMENT_PACKAGE_ENSURE;
            case 1:
                return FRAGMENT_PACKAGE_ENSURE;
            case 2:
                return FRAGMENT_BOTTLE_ENSURE;

            default:
                return FRAGMENT_PACKAGE_ENSURE;
            }

        default:
            return FRAGMENT_OPEN_DEFAULT;
        }
    }

    private int getNextId(int index) {
        return ++index;
    }

    @Override
    public void onLostConnect(int type) {
        Toast.makeText(this,
                getResources().getString(R.string.rs232_lost_connect),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRawInfo(int type, byte[] receiveData) {
        String data = StringTool.encodeHex(receiveData);
        String[] raw = new String[2];
        Message msg = mHandler.obtainMessage(MSG_UPDATE_DEBUG);
        raw[0] = "time: " + Utils.getTime();
        raw[1] = "\n" + receiveData.length + ", [0]" + type + "\n" + data;
        msg.obj = raw;
        msg.sendToTarget();
    }

    @Override
    public void onEPCInfo(int type, byte[] epcData) {
    }

    @Override
    public void onDEVInfo(int type, byte[] devData) {
        String[] data = StringTool.encodeHexToArrays(devData);
        handleDevInfo(data);
        if (mPackageOpenFragment != null) {
            mPackageOpenFragment.handleDevInfo(data, mBase);
        }
    }

    @Override
    public void onQRCInfo(int type, byte[] btData) {
    }

    private void handleDevInfo(String[] data) {
        if (data == null || data.length < Constancts.LENGTH_DEV) {
            return;
        }
        if (CMD.NAME_UNSOLICETD_SYNC.equals(data[1])) {
            if (isFull != data[2].equals("01")) {
                isFull = data[2].equals("01");
                SharedPreferencesUtils.setState(this, 40);
                String token = SharedPreferencesUtils.getToken(this);
                requestSyncState(Utils.buildStateCode(token, 40));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
        mPackageOpenFragment = null;
        mPackageResultFragment = null;
        mBottleOpenFragment = null;
        mBottleResultFragment = null;
        mResultFinishFragment = null;
        mGuestFinishFragment = null;
        openOrClosesHouse(false);

    }

    @Override
    public void onUpdate(int index, Parcelable value) {
        LogUtils.d(TAG, "onUpdate " + index);
        if (value != null) {
            mOwnerInfo = (OwnerInfo) value;
            LogUtils.d(TAG, "onUpdate " + mOwnerInfo.toString());
        }
        if (index >= FRAGMENT_EXIT) {
            mOwnerInfo = null;
            Intent intent = new Intent(WorkMainActivity.this,
                    HomeMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        showFragment(index);
    }

    @Override
    public void onFinish() {
        if (mPosition == FRAGMENT_BOTTLE_IN ||
                mPosition == FRAGMENT_PACKAGE_IN) {
            if (mOwnerInfo != null && mOwnerInfo.getVendor() > 0) {
                addOrder(buildProductJson());
                return;
            }
        }
        onBackPressed();
        try {
            mReaderHelper1.getReader().sendOpenHouse(false);
        } catch (Exception e) {
            Utils.showMsg(this, "onFinish close:" + e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(POSITION, mPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        showFragment(savedInstanceState.getInt(POSITION));
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void showFragment(int index) {
        LogUtils.d(TAG, "showFragment" + index);
        // update action bar
        if (index == FRAGMENT_PACKAGE_ENSURE || index == FRAGMENT_BOTTLE_ENSURE) {
            updateProgress(1);
            try {
                mReaderHelper1.getReader().sendOpenHouse(false);
            } catch (Exception e) {
            }
        } else if (index == FRAGMENT_PACKAGE_IN || index == FRAGMENT_BOTTLE_IN) {
            updateProgress(0);
            try {
                mReaderHelper1.getReader().sendOpenHouse(true);
            } catch (Exception e) {
            }
        } else if (index == FRAGMENT_FINISH || index == FRAGMENT_GUEST_FINISH) {
            updateProgress(2);
        }

        mTransaction = mManager.beginTransaction();
        hideAllFragment();
        mPosition = index;
        switch (index) {
        case FRAGMENT_PACKAGE_IN:
            if (mPackageOpenFragment == null) {
                mPackageOpenFragment = new OpenPackageFragment(this,
                        mPackageInfo, mOwnerInfo, index);
                mTransaction.add(R.id.content, mPackageOpenFragment);
            } else {
                mPackageOpenFragment.setOwnerInfo(mOwnerInfo);
                mTransaction.show(mPackageOpenFragment);
            }
            break;
        case FRAGMENT_PACKAGE_ENSURE:
            if (mOwnerInfo != null) {
                mPackageInfo.setWeight(mOwnerInfo.getVendor());
            }
            if (mPackageResultFragment == null) {
                mPackageResultFragment = new ResultPackageFragment(this,
                        mPackageInfo, mOwnerInfo, index);
                mTransaction.add(R.id.content, mPackageResultFragment);
            } else {
                mPackageResultFragment.setPackageInfo(mPackageInfo, mOwnerInfo);
                mTransaction.show(mPackageResultFragment);
            }
            break;
        case FRAGMENT_BOTTLE_IN:
            if (mBottleOpenFragment == null) {
                mBottleOpenFragment = new OpenBottleFragment(this, index);
                mTransaction.add(R.id.content, mBottleOpenFragment);
            } else {
                mTransaction.show(mBottleOpenFragment);
            }
            break;
        case FRAGMENT_BOTTLE_ENSURE:
            if (mBottleResultFragment == null) {
                mBottleResultFragment = new ResultBottleFragment(this, index);
                mTransaction.add(R.id.content, mBottleResultFragment);
            } else {
                mTransaction.show(mBottleResultFragment);
            }
            break;
        case FRAGMENT_FINISH:
            ProductAndOwnerInfo product = new ProductAndOwnerInfo(
                    mPackageInfo.getTotal(), mOwnerInfo);
            if (mResultFinishFragment == null) {
                mResultFinishFragment = new ResultFinshFragment(this, product,
                        index);
                mTransaction.add(R.id.content, mResultFinishFragment);
            } else {
                mResultFinishFragment.setInfo(product);
                mTransaction.show(mResultFinishFragment);
            }
            break;
        case FRAGMENT_GUEST_FINISH:
            if (mGuestFinishFragment == null) {
                mGuestFinishFragment = new GuestFinshFragment(this, index);
                mTransaction.add(R.id.content, mGuestFinishFragment);
            } else {
                mTransaction.show(mGuestFinishFragment);
            }
            break;
        }

        mTransaction.commit();
    }

    /** must commit end */
    public void hideAllFragment() {
        if (mPackageOpenFragment != null) {
            mTransaction.hide(mPackageOpenFragment);
        }
        if (mPackageResultFragment != null) {
            mTransaction.hide(mPackageResultFragment);
        }
        if (mBottleOpenFragment != null) {
            mTransaction.hide(mBottleOpenFragment);
        }
        if (mBottleResultFragment != null) {
            mTransaction.hide(mBottleResultFragment);
        }
        if (mResultFinishFragment != null) {
            mTransaction.hide(mResultFinishFragment);
        }
        if (mGuestFinishFragment != null) {
            mTransaction.hide(mGuestFinishFragment);
        }
    }

    private String buildProductJson() {
        JSONObject object = new JSONObject();
        try {
            JSONObject good = new JSONObject();
            good.put("id", String.valueOf(mPackageInfo.getId()));
            good.put("num", Utils.formatDouble(mPackageInfo.getWeight()));
            good.put("name", mPackageInfo.getName());
            JSONArray array = new JSONArray();
            array.put(good);

            object.put("token", mToken);
            if (mOwnerInfo != null && mOwnerInfo.getId() > 0) {
                object.put("user_id", mOwnerInfo.getId());
            }
            object.put("goods", array);
            object.put("type", "4");
        } catch (JSONException e) {
            LogUtils.e(TAG, "buildProductJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildProductJson:" + object.toString());
        return object.toString();
    }

    private void addOrder(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.ADD_ORDER;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                LogUtils.d(TAG, "addOrder data =" + data);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "addOrder:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    if (mOwnerInfo.getId() > 0) {
                        onUpdate(FRAGMENT_FINISH, null);
                        mHelper.start();
                    } else {
                        onUpdate(FRAGMENT_GUEST_FINISH, null);
                    }
                }
            }
        }.execute(url);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            String[] data = (String[]) msg.obj;
            switch (msg.what) {
            case MSG_UPDATE_DEBUG:
                Utils.showMsg(WorkMainActivity.this, "work raw:" + data[0]
                        + data[1]);
                break;

            default:
                break;
            }
        }
    };

}