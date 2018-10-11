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

public class NoticeDialog extends Dialog implements OnClickListener {
    private Button mEnter;
    private TextView mTips;
    private CountDownTimer mTimer;
    public static final int PERIOD = 60 * 1000;
    public static final int INTERVAL = 1 * 1000;

    public NoticeDialog(Context context) {
        this(context, R.style.PopDialog);
    }

    private NoticeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahs_notice_dialog);
        initView();
        init();
    }

    private void initView() {
        mTips = (TextView) findViewById(R.id.notice_tips);
        mEnter = (Button) findViewById(R.id.notice_ok);
        mEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.notice_ok:
            stop();
            dismiss();
            break;
        default:
            break;
        }
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
}