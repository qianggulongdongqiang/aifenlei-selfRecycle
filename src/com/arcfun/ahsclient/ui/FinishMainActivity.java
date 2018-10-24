package com.arcfun.ahsclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.data.PackageInfo;
import com.arcfun.ahsclient.data.ProductAndOwnerInfo;
import com.arcfun.ahsclient.utils.CountDownTimerHelper;
import com.arcfun.ahsclient.utils.CountDownTimerHelper.OnFinishListener;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.view.BaseLoginFragment.OnActionCallBack;
import com.arcfun.ahsclient.view.FinshFragment;
import com.arcfun.ahsclient.view.FinshListFragment;

public class FinishMainActivity extends AhsBaseActivity implements
        OnFinishListener, OnActionCallBack {
    private static final String TAG = "Finish|Main";
    private ImageView mStep2, mStep2End, mStep3;
    private TextView mType, mLable;

    /** finish */
    private FinshFragment mFinishFragment;
    private FinshListFragment mFinishListFragment;
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private Button mCountDown;
    private CountDownTimerHelper mHelper;

    public static final String POSITION = "position";

    public static final int FRAGMENT_FINISH = 70;
    public static final int FRAGMENT_FINISH_DEFAULT = FRAGMENT_FINISH;
    public static final int FRAGMENT_EXIT = 100;

    private int mPosition = FRAGMENT_FINISH_DEFAULT;
    public static final int PERIOD = 15 * 1000;
    public static final int INTERVAL = 1 * 1000;
    private OwnerInfo mOwnerInfo;
    private PackageInfo mPackageInfo;
    private int mMachineType = 0;// package

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_finish_home);
        ((AhsApplication) getApplication()).addActivity(this);
        mManager = getSupportFragmentManager();
        initData();
        initView();
    }

    private void initView() {
        mType = (TextView) findViewById(R.id.work_type);
        mLable = (TextView) findViewById(R.id.barcode_text);
        mStep2 = (ImageView) findViewById(R.id.open_step2);
        mStep2End = (ImageView) findViewById(R.id.open_step2_divider);
        mStep3 = (ImageView) findViewById(R.id.open_step3);

        mCountDown = (Button) findViewById(R.id.open_count_down);
        mHelper = new CountDownTimerHelper(mCountDown, PERIOD, INTERVAL);
        mHelper.setOnFinishListener(this);
        setMachineType(mMachineType);
        showFragment(FRAGMENT_FINISH_DEFAULT);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mOwnerInfo = intent.getParcelableExtra("owner_info");
            mPackageInfo = intent.getParcelableExtra("package_info");
            if (mOwnerInfo == null) {
                mOwnerInfo = new OwnerInfo(0, "Guest", 0, "");
            }
            if (mPackageInfo == null) {
                mPackageInfo = SharedPreferencesUtils.getMachineInfo(this);
            }
        }
        mMachineType = SharedPreferencesUtils.getMachineType(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lable = SharedPreferencesUtils
                .getSignature(getApplicationContext());
        mLable.setText(getString(R.string.title_des4, lable));
        mHelper.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHelper.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_barcode:
            showFragment(getNextId(mPosition));
            break;

        default:
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
    private void updateProgress() {
        mStep2.setImageResource(R.drawable.icon_completed);
        mStep2End.setImageResource(R.drawable.line1);
        mStep3.setImageResource(R.drawable.icon_completed);
    }

    private int getNextId(int index) {
        return ++index;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
        mFinishFragment = null;
        mFinishListFragment = null;
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
            Intent intent = new Intent(FinishMainActivity.this,
                    HomeMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
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
        LogUtils.d(TAG, "showFragment" + index);
        // update action bar
        updateProgress();

        mTransaction = mManager.beginTransaction();
        hideAllFragment();
        mPosition = index;
        switch (index) {
        case FRAGMENT_FINISH:
            ProductAndOwnerInfo product = new ProductAndOwnerInfo(
                    mPackageInfo.getTotal(), mOwnerInfo);
            if (mFinishFragment == null) {
                mFinishFragment = new FinshFragment(product, index);
                mTransaction.add(R.id.content, mFinishFragment);
            } else {
                mFinishFragment.setInfo(product);
                mTransaction.show(mFinishFragment);
            }
            if (mFinishListFragment == null) {
                mFinishListFragment = new FinshListFragment(this, mPackageInfo, index);
                mTransaction.add(R.id.content2, mFinishListFragment);
            } else {
                mFinishListFragment.setInfo(mPackageInfo);
                mTransaction.show(mFinishListFragment);
            }
            break;
        }

        mTransaction.commitAllowingStateLoss();
    }

    /** must commit end */
    public void hideAllFragment() {
        if (mFinishFragment != null) {
            mTransaction.hide(mFinishFragment);
        }
        if (mFinishListFragment != null) {
            mTransaction.hide(mFinishListFragment);
        }
    }

}