package com.arcfun.ahsclient.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.ahsclient.R;

public class GuestWelcomeDialog extends Dialog implements OnClickListener {
    private Button mEnter, mSwitch;
    private OnChooseListener mListener;
    private TextView mTips;
    private CountDownTimer mTimer;
    public static final int PERIOD = 60 * 1000;
    public static final int INTERVAL = 1 * 1000;

    public GuestWelcomeDialog(Context context) {
        this(context, R.style.PopDialog);
        this.mListener = (OnChooseListener) context;
    }

    private GuestWelcomeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_guest_dialog);
        initView();
        init();
    }

    private void initView() {
        mTips = (TextView) findViewById(R.id.guest_tips);
        mEnter = (Button) findViewById(R.id.guest_login);
        mSwitch = (Button) findViewById(R.id.guest_switch);
        mEnter.setOnClickListener(this);
        mSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.guest_login:
            if (mListener != null) {
                mListener.onNext(1);
            }
            break;
        case R.id.guest_switch:
            if (mListener != null) {
                mListener.onNext(0);
            }
            break;
        default:
            break;
        }
        stop();
        dismiss();
    }

    private void init() {
        mTimer = new CountDownTimer(PERIOD, INTERVAL) {
            
            @Override
            public void onTick(long time) {
                mTips.setText("(" + String.valueOf(time / 1000) + "s)");
            }
            
            @Override
            public void onFinish() {
                dismiss();
            }
        };
        start();
    }

    public void start() {
        if (mTimer != null) {
            mTimer.start();
        }
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    public interface OnChooseListener {
        public void onNext(int type);
    }
}