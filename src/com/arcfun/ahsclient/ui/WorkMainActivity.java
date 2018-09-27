package com.arcfun.ahsclient.ui;

import java.util.Arrays;

import android.content.Intent;
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
import com.arcfun.ahsclient.utils.CountDownTimerHelper;
import com.arcfun.ahsclient.utils.CountDownTimerHelper.OnFinishListener;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.arcfun.ahsclient.view.BaseLoginFragment.OnActionCallBack;
import com.arcfun.ahsclient.view.OpenBottleFragment;
import com.arcfun.ahsclient.view.OpenPackageFragment;
import com.arcfun.ahsclient.view.ResultBottleFragment;
import com.arcfun.ahsclient.view.ResultFinshFragment;
import com.arcfun.ahsclient.view.ResultPackageFragment;
import com.reader.base.StringTool;
import com.reader.helper.ReaderHelper.Listener;

public class WorkMainActivity extends AhsBaseActivity implements Listener,
        OnFinishListener, OnActionCallBack {
    private static final String TAG = "Work|Main";
    private ImageView mStep2, mStep2End, mStep3;
    private ImageView mBar, mBack;
    private TextView mType, mLable;
    /** bottle */
    private OpenBottleFragment mBottleOpenFragment;
    private ResultBottleFragment mBottleResultFragment;

    /** package */
    private OpenPackageFragment mPackageOpenFragment;
    private ResultPackageFragment mPackageResultFragment;

    /** finish */
    private ResultFinshFragment mResultFinishFragment;
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
    public static final int FRAGMENT_EXIT = 80;

    private int mPosition = FRAGMENT_OPEN_DEFAULT;
    public static final int PERIOD = 200 * 1000;
    public static final int INTERVAL = 1 * 1000;
    private OwnerInfo mOwnerInfo;
    private PackageInfo mPackageInfo;
    private int mMachineType = 0;// package
    protected boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_open_home);
        ((AhsApplication) getApplication()).addActivity(this);
        mManager = getSupportFragmentManager();
        initData();
        initView();
        initSerialPort1();
        openOrClosesHouse(true);
    }

    private void openOrClosesHouse(boolean isOpen) {
        try {
            mReaderHelper1.getReader().sendOpenHouse(true);
        } catch (Exception e) {
            Utils.showMsg(this,
                    isOpen ? "Open" : "Close" + " House :" + e.toString());
        }
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.open_back);
        mBar = (ImageView) findViewById(R.id.title_barcode);
        mType = (TextView) findViewById(R.id.work_type);
        mLable = (TextView) findViewById(R.id.barcode_text);
        mStep2 = (ImageView) findViewById(R.id.open_step2);
        mStep2End = (ImageView) findViewById(R.id.open_step2_divider);
        mStep3 = (ImageView) findViewById(R.id.open_step3);

        mCountDown = (Button) findViewById(R.id.open_count_down);
        mHelper = new CountDownTimerHelper(mCountDown, PERIOD, INTERVAL);
        mHelper.setOnFinishListener(this);
        mBack.setOnClickListener(this);
        if (Utils.DEBUG) {
            mBar.setOnClickListener(this);
        }
        setMachineType(mMachineType);
        showFragment(FRAGMENT_OPEN_DEFAULT);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            isGuest = intent.getBooleanExtra("isGuest", false);
            mOwnerInfo = intent.getParcelableExtra("owner_info");
        }
        mMachineType = SharedPreferencesUtils.getMachineType(this);
        mPackageInfo = SharedPreferencesUtils.getMachineInfo(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lable = SharedPreferencesUtils
                .getSignature(getApplicationContext());
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
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_barcode:
            showFragment(getNextId(mPosition));
            break;
        case R.id.open_back:
            if (mPosition == FRAGMENT_OPEN_DEFAULT) {
                onBackPressed();
                try {
                    mReaderHelper1.getReader().sendOpenHouse(false);
                } catch (Exception e) {
                    Utils.showMsg(this, "Close House :" + e.toString());
                }
                return;
            } else if (mPosition == FRAGMENT_FINISH) {
                Intent intent = new Intent(WorkMainActivity.this,
                        HomeMainActivity.class);
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
     * 0 - package; 1- bottle
     */
    private void setMachineType(int type) {
        switch (type) {
        case 0:
            mType.setText(getString(R.string.status_bar_work2));
            break;
        case 1:
            mType.setText(getString(R.string.status_bar_work1));
            break;

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
                return FRAGMENT_BOTTLE_ENSURE;
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
        long time = System.currentTimeMillis();
        String[] raw = new String[2];
        Message msg = mHandler.obtainMessage(MSG_UPDATE_DEBUG);
        raw[0] = "time: " + time;
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
        if (mPackageOpenFragment != null) {
            mPackageOpenFragment.handleDevInfo(data);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPackageOpenFragment = null;
        mPackageResultFragment = null;
        mBottleOpenFragment = null;
        mBottleResultFragment = null;
        mResultFinishFragment = null;
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
            startActivity(intent);
            finish();
        }
        showFragment(index);
    }

    @Override
    public void onFinish() {
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
        } else if (index == FRAGMENT_PACKAGE_IN || index == FRAGMENT_BOTTLE_IN) {
            updateProgress(0);
        } else if (index == FRAGMENT_FINISH) {
            updateProgress(2);
        }

        mTransaction = mManager.beginTransaction();
        hideAllFragment();
        mPosition = index;
        switch (index) {
        case FRAGMENT_PACKAGE_IN:
            if (mPackageOpenFragment == null) {
                mPackageOpenFragment = new OpenPackageFragment(this,
                        mOwnerInfo, index);
                mTransaction.add(R.id.content, mPackageOpenFragment);
            } else {
                mPackageOpenFragment.setOwnerInfo(mOwnerInfo);
                mTransaction.show(mPackageOpenFragment);
            }
            break;
        case FRAGMENT_PACKAGE_ENSURE:
            if (mOwnerInfo != null) {
                mPackageInfo.setWeight(mOwnerInfo.getVendor());// TODO
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
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            String[] data = (String[]) msg.obj;
            switch (msg.what) {
                case MSG_UPDATE_DEBUG:
                    Utils.showMsg(WorkMainActivity.this, "work raw:" +
                            data[0] + data[1]);
                    break;

                default:
                    break;
            }
        }
    };

}