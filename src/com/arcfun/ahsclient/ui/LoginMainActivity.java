package com.arcfun.ahsclient.ui;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.CountDownTimerHelper;
import com.arcfun.ahsclient.utils.CountDownTimerHelper.OnFinishListener;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.arcfun.ahsclient.view.BaseLoginFragment.OnActionCallBack;
import com.arcfun.ahsclient.view.PhoneEnsureFragment;
import com.arcfun.ahsclient.view.PhoneLoginFragment;
import com.arcfun.ahsclient.view.RFIDEnsureFragment;
import com.arcfun.ahsclient.view.RFIDLoginFragment;
import com.arcfun.ahsclient.view.WeixinEnsureFragment;
import com.arcfun.ahsclient.view.WeixinLoginFragment;
import com.reader.base.CMD;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class LoginMainActivity extends AhsBaseActivity implements Listener,
        OnFinishListener, OnActionCallBack, OnLongClickListener {
    private ImageView mBar, mBack;
    private TextView mLable;
    /** rfid */
    private RFIDEnsureFragment mRfidEnsureFragment;
    private RFIDLoginFragment mRFIDFragment;

    /** wechat */
    private WeixinLoginFragment mWeixinFragment;
    private WeixinEnsureFragment mWeixinEnsureFragment;

    /** phone */
    private PhoneLoginFragment mPhoneFragment;
    private PhoneEnsureFragment mPhoneEnsureFragment;

    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private Button mCountDown;
    private CountDownTimerHelper mHelper;
    /** 扫描登录 */
    private OwnerInfo mInfo;
    /** 工控机的数据 */
    private SparseArray<Float> mWeight = new SparseArray<Float>();
    private boolean isFull = false;
    private boolean isDirty = false;

    public static final String POSITION = "position";
    public static final int FRAGMENT_DEFAULT = 0;
    public static final int FRAGMENT_LOGIN_RFID_1 = FRAGMENT_DEFAULT;
    public static final int FRAGMENT_LOGIN_RFID_2 = 1;
    public static final int FRAGMENT_LOGIN_RFID_3 = 2;
    public static final int FRAGMENT_LOGIN_WEIXIN_1 = 10;
    public static final int FRAGMENT_LOGIN_WEIXIN_2 = 11;
    public static final int FRAGMENT_LOGIN_PHONE_1 = 20;
    public static final int FRAGMENT_LOGIN_PHONE_2 = 21;
    private int mPosition = FRAGMENT_DEFAULT;
    public static final int PERIOD = 200 * 1000;
    public static final int INTERVAL = 1 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_login_home);
        ((AhsApplication) getApplication()).addActivity(this);
        mManager = getSupportFragmentManager();
        initView();
        initSerialPort1();
    }

    private void initView() {
        showFragment(FRAGMENT_DEFAULT);
        mBack = (ImageView) findViewById(R.id.login_back);
        mBar = (ImageView) findViewById(R.id.title_barcode);
        mLable = (TextView) findViewById(R.id.barcode_text);
        mCountDown = (Button) findViewById(R.id.login_count_down);
        mHelper = new CountDownTimerHelper(mCountDown, PERIOD, INTERVAL);
        mHelper.setOnFinishListener(this);
        mBack.setOnClickListener(this);
        if (Utils.DEBUG) {
            mBar.setOnClickListener(this);
            mBar.setOnLongClickListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lable = SharedPreferencesUtils.getSignature(getApplicationContext());
        mLable.setText(getString(R.string.title_des4, lable));
        mHelper.start();
        mReaderHelper1.setListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHelper.stop();
        mReaderHelper1.setListener(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mPosition == FRAGMENT_LOGIN_WEIXIN_1) {
            mInfo = intent.getParcelableExtra("owner_info");
            LogUtils.d(TAG, "onNewIntent " + mInfo.toString());
            showFragment(FRAGMENT_LOGIN_WEIXIN_2);
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        showFragment(getNextId(mPosition));
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_barcode:
            try {
                mReaderHelper1.getReader().sendOpenHouse(true);
            } catch (Exception e) {
                Utils.showMsg(this, "click sendOpenHouse:" + e.toString());
            }
            break;
        case R.id.login_back:
            if (mPosition == FRAGMENT_LOGIN_RFID_1) {
                onBackPressed();
                return;
            }
            showFragment(getLastId(mPosition));
            break;

        default:
            showFragment(FRAGMENT_LOGIN_WEIXIN_1);
            break;
        }
    }

    private int getLastId(int index) {
        switch (index) {
        case FRAGMENT_LOGIN_RFID_1:
            return FRAGMENT_LOGIN_RFID_1;
        case FRAGMENT_LOGIN_WEIXIN_1:
            return FRAGMENT_LOGIN_RFID_1;
        case FRAGMENT_LOGIN_PHONE_1:
            return FRAGMENT_LOGIN_RFID_1;

        default:
            return --index;
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
    }

    @Override
    public void onEPCInfo(int type, byte[] epcData) {
        String data[] = StringTool.encodeHexToArrays(epcData);
        long time = System.currentTimeMillis();
        String[] raw = new String[2];
        Message msg = mHandler.obtainMessage(MSG_UPDATE_DEBUG);
        raw[0] = "EPC time: " + time;
        raw[1] = "\nlength=" + data.length + ", top=" + type + "\n" + Arrays.toString(data)
                + "\n" + Arrays.toString(epcData);
        msg.obj = raw;
        msg.sendToTarget();
    }

    @Override
    public void onDEVInfo(int type, byte[] devData) {
        String[] data = StringTool.encodeHexToArrays(devData);
        handleDevInfo(data);
        long time = System.currentTimeMillis();
        Message msg = mHandler.obtainMessage(MSG_UPDATE_DEBUG);
        String[] raw = new String[2];
        raw[0] = "Dev time: " + time;
        if (data[0].equals("58") && data[1].equals("03")) {
            raw[1] = "\nlength=" + data.length + ", top=" + type + "\n" + Arrays.toString(data)
                    + "\n" + Utils.getIndex(data) + ", " + Utils.getWeight(data);
        } else {
            raw[1] = "\nlength=" + data.length + ", top=" + type + "\n" + Arrays.toString(data)
                    + "\n" + Arrays.toString(devData);
        }
        msg.obj = raw;
        msg.sendToTarget();
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
                //TODO
            }
            if (isDirty != data[3].equals("01")) {
                isDirty = data[3].equals("01");
                //TODO
            }
        } else if (CMD.NAME_UNSOLICETD_WEIGHT.equals(data[1])) {
            mWeight.append(Utils.getIndex(data), Utils.getWeight(data));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRFIDFragment = null;
        mRfidEnsureFragment = null;
        mWeixinFragment = null;
        mWeixinEnsureFragment = null;
        mPhoneFragment = null;
        mPhoneEnsureFragment = null;
        closeSerialPort1();
    }

    @Override
    public void onUpdate(int index, Parcelable value) {
        LogUtils.d(TAG, "onUpdate " + index);
        if (value != null) {
            mInfo = (OwnerInfo) value;
        }
        showFragment(index);
    }

    @Override
    public void onFinish() {
        onBackPressed();
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
        LogUtils.d(TAG, "showFragment pos = " + index);
        switch (index) {
            case FRAGMENT_LOGIN_RFID_3 + 1:
            case FRAGMENT_LOGIN_WEIXIN_2 + 1:
            case FRAGMENT_LOGIN_PHONE_2 + 1:
                Intent intent = new Intent(LoginMainActivity.this,
                        WorkMainActivity.class);
                intent.putExtra("owner_info", mInfo);
                startActivity(intent);
                return;

            default:
                break;
        }
        mTransaction = mManager.beginTransaction();
        hideAllFragment();
        mPosition = index;
        switch (index) {
        case FRAGMENT_LOGIN_RFID_1:
            if (mRFIDFragment == null) {
                mRFIDFragment = new RFIDLoginFragment(this, index);
                mTransaction.add(R.id.content, mRFIDFragment);
            } else {
                mTransaction.show(mRFIDFragment);
            }
            break;
        case FRAGMENT_LOGIN_RFID_2:
            // TODO
            break;
        case FRAGMENT_LOGIN_RFID_3:
            if (mRfidEnsureFragment == null) {
                mRfidEnsureFragment = new RFIDEnsureFragment(this, index);
                mTransaction.add(R.id.content, mRfidEnsureFragment);
            } else {
                mTransaction.show(mRfidEnsureFragment);
            }
            break;
        case FRAGMENT_LOGIN_WEIXIN_1:
            if (mWeixinFragment == null) {
                mWeixinFragment = new WeixinLoginFragment(this, index);
                mTransaction.add(R.id.content, mWeixinFragment);
            } else {
                mTransaction.show(mWeixinFragment);
            }
            break;
        case FRAGMENT_LOGIN_WEIXIN_2:
            if (mWeixinEnsureFragment == null) {
                mWeixinEnsureFragment = new WeixinEnsureFragment(this, mInfo,
                        index);
                mTransaction.add(R.id.content, mWeixinEnsureFragment);
            } else {
                mWeixinEnsureFragment.setInfo(mInfo);
                mTransaction.show(mWeixinEnsureFragment);
            }
            break;
        case FRAGMENT_LOGIN_PHONE_1:
            if (mPhoneFragment == null) {
                mPhoneFragment = new PhoneLoginFragment(this, index);
                mTransaction.add(R.id.content, mPhoneFragment);
            } else {
                mTransaction.show(mPhoneFragment);
            }
            break;
        case FRAGMENT_LOGIN_PHONE_2:
            if (mPhoneEnsureFragment == null) {
                mPhoneEnsureFragment = new PhoneEnsureFragment(this, mInfo,
                        index);
                mTransaction.add(R.id.content, mPhoneEnsureFragment);
            } else {
                mPhoneEnsureFragment.setInfo(mInfo);
                mTransaction.show(mPhoneEnsureFragment);
            }
            break;
        }

        mTransaction.commit();
    }

    /** must commit end */
    public void hideAllFragment() {
        if (mRFIDFragment != null) {
            mTransaction.hide(mRFIDFragment);
        }
        if (mRfidEnsureFragment != null) {
            mTransaction.hide(mRfidEnsureFragment);
        }
        if (mWeixinFragment != null) {
            mTransaction.hide(mWeixinFragment);
        }
        if (mWeixinEnsureFragment != null) {
            mTransaction.hide(mWeixinEnsureFragment);
        }
        if (mPhoneFragment != null) {
            mTransaction.hide(mPhoneFragment);
        }
        if (mPhoneEnsureFragment != null) {
            mTransaction.hide(mPhoneEnsureFragment);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            String[] data = (String[]) msg.obj;
            switch (msg.what) {
                case MSG_UPDATE_DEBUG:
                    Utils.showMsg(LoginMainActivity.this, "login :" +
                            data[0] + data[1]);
                    break;

                default:
                    break;
            }
        }
    };

}