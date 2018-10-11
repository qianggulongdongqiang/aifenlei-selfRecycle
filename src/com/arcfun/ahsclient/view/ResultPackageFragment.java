package com.arcfun.ahsclient.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arcfun.ahsclient.R;
import com.arcfun.ahsclient.data.OrderResultInfo;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.data.PackageInfo;
import com.arcfun.ahsclient.net.HttpRequest;
import com.arcfun.ahsclient.utils.LogUtils;
import com.arcfun.ahsclient.utils.SharedPreferencesUtils;
import com.arcfun.ahsclient.utils.Utils;

public class ResultPackageFragment extends BaseLoginFragment implements
        OnClickListener {
    private static final String TAG = "Result|Package";
    private OnActionCallBack mListener;
    private int mIndex;
    private TextView mResultTips, mResultType, mResultWeight, mResultSum,
            mResultTotal;
    private Button mFinishBtn;
    private PackageInfo mInfo;
    private OwnerInfo mOwnerInfo;
    private String mToken = "";

    public ResultPackageFragment() {
    }

    public ResultPackageFragment(OnActionCallBack listener, PackageInfo info,
            OwnerInfo _ownerInfo, int index) {
        this.mListener = listener;
        this.mIndex = index;
        this.mInfo = info;
        this.mOwnerInfo = _ownerInfo;
    }

    public PackageInfo getPackageInfo() {
        return mInfo;
    }

    public void setPackageInfo(PackageInfo packge, OwnerInfo owner) {
        this.mInfo = packge;
        this.mOwnerInfo = owner;
        if (packge != null) {
            LogUtils.d(TAG, "setPackageInfo " + mInfo.toString());
            if (mInfo.getId() == 30 || mInfo.getId() == 33
                    || mInfo.getId() == 36) {
                mResultTips.setText(getString(R.string.history_unit,
                        mInfo.getUnit()));
            } else {
                mResultTips.setText(getString(R.string.history_unit2,
                        mInfo.getUnit()));
            }
            mResultType.setText(mInfo.getName());
            mResultWeight.setText(getString(R.string.history_weight,
                    mInfo.getWeight()));
            mResultSum.setText(getString(R.string.history_credit,
                    mInfo.getTotal()));
            mResultTotal.setText(getString(R.string.history_credit,
                    mInfo.getTotal()));
        }
    }

    public OwnerInfo getOwnerInfo() {
        return mOwnerInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ahs_open_result1_fragment,
                container, false);
        mResultTips = (TextView) view.findViewById(R.id.result_unit);
        mResultType = (TextView) view.findViewById(R.id.result_type);
        mResultWeight = (TextView) view.findViewById(R.id.result_weight);
        mResultSum = (TextView) view.findViewById(R.id.result_sum);
        mResultTotal = (TextView) view.findViewById(R.id.total_credit);
        mFinishBtn = (Button) view.findViewById(R.id.open_finish);
        mFinishBtn.setOnClickListener(this);
        mToken = SharedPreferencesUtils.getToken(getActivity());
        if (mInfo != null) {
            LogUtils.d(TAG, "result " + mInfo.toString());
            if (mInfo.getId() == 30 || mInfo.getId() == 33
                    || mInfo.getId() == 36) {
                mResultTips.setText(getString(R.string.history_unit,
                        mInfo.getUnit()));
            } else {
                mResultTips.setText(getString(R.string.history_unit2,
                        mInfo.getUnit()));
            }
            mResultType.setText(mInfo.getName());
            mResultWeight.setText(getString(R.string.history_weight,
                    mInfo.getWeight()));
            mResultSum.setText(getString(R.string.history_credit,
                    mInfo.getTotal()));
            mResultTotal.setText(getString(R.string.history_credit,
                    mInfo.getTotal()));
        }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.open_finish:
            addOrder(buildProductJson());
            break;

        default:
            break;
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
                            mListener.onUpdate(FRAGMENT_FINISH, mOwnerInfo);
                        }
                    }
                }
            }
        }.execute(url);
    }
}