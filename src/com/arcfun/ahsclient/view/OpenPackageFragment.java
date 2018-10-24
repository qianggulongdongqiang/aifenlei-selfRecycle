package com.arcfun.ahsclient.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.OrderResultInfo;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.data.PackageInfo;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.ui.NoticeDialog;
import com.arcfun.ahsclient.utils.Constancts;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;
import com.reader.base.CMD;

public class OpenPackageFragment extends BaseLoginFragment implements
        OnClickListener, OnLongClickListener {
    private static final String TAG = "Open|Package";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mOpenTips, mOpenMsg, mOpenStart, mOpenEnd;
    private Button mFinishBtn;
    private PackageInfo mInfo;
    private OwnerInfo mOwnerInfo;
    private float mWeight = 0f;
    protected static final int MSG_UPDATE_WEIGHT = 0;
    private String mToken = "";

    public OpenPackageFragment() {
    }

    public void setOwnerInfo(OwnerInfo info) {
        this.mOwnerInfo = info;
    }

    public OpenPackageFragment(OnActionCallBack listener, PackageInfo pInfo,
            OwnerInfo oInfo, int index) {
        this.mListener = listener;
        this.mInfo = pInfo;
        this.mOwnerInfo = oInfo;
        this.mIndex = index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mToken = SharedPreferencesUtils.getToken(getActivity());
        View view = inflater.inflate(R.layout.ahs_open1_fragment, container,
                false);
        mOpenTips = (TextView) view.findViewById(R.id.open_msg);
        mOpenMsg = (TextView) view.findViewById(R.id.open_detail);
        mOpenStart = (TextView) view.findViewById(R.id.open_detail_s);
        mOpenEnd = (TextView) view.findViewById(R.id.open_detail_e);
        mFinishBtn = (Button) view.findViewById(R.id.open_finish);
        mFinishBtn.setOnClickListener(this);
        if (SharedPreferencesUtils.getDebug(getActivity())) {
            mFinishBtn.setOnLongClickListener(this);
        }
        if (mInfo != null) {
            mOpenTips.setText(getString(R.string.open_msg1, mInfo.getName()));
            mOpenStart.setText(getString(R.string.open_result_s2, mInfo.getName()));
            mOpenEnd.setText(mInfo.getuName());
        }
        updateNum(0);
        LogUtils.d(TAG, "onCreateView " + mIndex);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG, "onActivityCreated mIndex = " + mIndex);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, "onHiddenChanged mIndex = " + mIndex);
    }

    private void updateNum(float num) {
        double pig = Utils.formatDouble(num < 0 ? 0 : num);
        mOpenMsg.setText(String.valueOf(pig));
        mInfo.setWeight(num);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.open_finish:
            if (mListener != null) {
                if (mInfo.getWeight() <= 0) {
                    showDialog();
                    return;
                }
                addOrder(buildProductJson());
            }
            break;

        default:
            break;
        }
    }

    private void showDialog() {
        NoticeDialog dialog = new NoticeDialog(getActivity());
        dialog.show();
    }

    @Override
    public boolean onLongClick(View v) {
        updateNum(mWeight += 1);
        if (mOwnerInfo != null) {
            mOwnerInfo.setVendor(mWeight);
        }
        return true;
    }

    public void handleDevInfo(String[] data, float base) {
        if (data != null && data.length == Constancts.LENGTH_DEV) {
            if (CMD.NAME_UNSOLICETD_SYNC.equals(data[1]) ||
                    CMD.NAME_UNSOLICETD_WEIGHT.equals(data[1])) {
                mWeight = Utils.getWeight(data) - base;
                if (mWeight < 0) {
                    // exception here we just set 0;
                    mWeight = 0f;
                }
                if (mOwnerInfo != null) {
                    mOwnerInfo.setVendor(mWeight);
                }
                mHandler.obtainMessage(MSG_UPDATE_WEIGHT).sendToTarget();
            }
        }
    }

    private String buildProductJson() {
        JSONObject object = new JSONObject();
        try {
            JSONObject good = new JSONObject();
            good.put("id", String.valueOf(mInfo.getId()));
            good.put("num", Utils.formatDouble(mInfo.getWeight()));
            good.put("name", mInfo.getName());
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
                OrderResultInfo info = null;
                if (result != null) {
                    info = Utils.parseResultData(result);
                    if (mOwnerInfo != null) {
                        mOwnerInfo.setScore(info.getUserScore());
                    } else {
                        mOwnerInfo = new OwnerInfo(0, "Guest",
                                mInfo.getTotal(), "");
                    }
                    if (mListener != null) {
                        if (mOwnerInfo.getId() > 0) {
                            mListener.onUpdate(FRAGMENT_PACKAGE_ENSURE,
                                    mOwnerInfo);
                        } else {
                            mListener.onUpdate(FRAGMENT_GUEST_FINISH,
                                    mOwnerInfo);
                        }
                    }
                }
            }
        }.execute(url);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_WEIGHT:
                updateNum(mWeight);
                break;

            default:
                break;
            }
        }
    };
}