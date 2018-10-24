package com.arcfun.ahsclient.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.arcfun.ahsclient.data.DeviceInfo;
import com.arcfun.ahsclient.data.GoodInfo;
import com.arcfun.ahsclient.data.MachineType;
import com.arcfun.ahsclient.data.OrderResultInfo;
import com.arcfun.ahsclient.data.OwnerInfo;
import com.arcfun.ahsclient.data.QrCode;
import com.arcfun.ahsclient.data.ResultInfo;
import com.arcfun.ahsclient.data.ScoreInfo;
import com.arcfun.ahsclient.data.SlowSpeedScroller;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class Utils {
    public static final boolean DEBUG = Log.isLoggable("aifenx",
            Log.DEBUG);

    public static final int MAX_WELCOME_LIST = 5;
    private static final String TAG = "Utils";
    private static final String DIR = "goods";
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_WEIGHT = 1;
    public static final int TYPE_EPC = 2;
    public static final int LENGTH_EPC = 24;
    public static final int LENGTH_RSSI = 5;
    public static final int LENGTH_RAW_RFID = 33;
    public static final int LENGTH_RAW_WEIGHT = 13;

    public static final int TRANS_WEIGHT = 0;
    public static final int TRANS_PIECES = 1;

    /** Baud rate */
    public static final int BAUD_RATE_DEF = 9600;
    public static final int BAUD_RATE_UHF = 115200;

    /** Serial Port */
    public static final String SERIAL_PROT_1 = "/dev/ttyS2";//dev/ttyS1
    public static final String SERIAL_PROT_2 = "/dev/ttyUSB0";//dev/ttyS3
    public static final String SERIAL_PROT_3 = "/dev/ttyUSB1";//dev/ttyUSB1
    public static final String SERIAL_PROT_4 = "/dev/ttyUSB2";//dev/ttyUSB2

    public static int MAX_GRID_VIEW_NUM = 9;
    public static int NUMBER_COLUMNS = 3;

    /** 0=fail */
    public static int RESULT_FAIL = 0;
    /** 1=success; */
    public static int RESULT_OK = 1;
    /** 2=not login */
    public static int RESULT_UNKNOWN = 2;

    public static Toast mToast = null;

    public static final String PREFS_NAME = "JPUSH_EXAMPLE";
    public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
    public static final String PREFS_START_TIME = "PREFS_START_TIME";
    public static final String PREFS_END_TIME = "PREFS_END_TIME";
    public static final String KEY_APP_KEY = "JPUSH_APPKEY";

    private static final String PHONE_NUMBER_REGEX = "^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$";

    public static String decode(String bytes) {
        String hexString = "0123456789ABCDEF";
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    public static String formatPhoneNumber(String args) {
        Matcher matcher = Pattern.compile(PHONE_NUMBER_REGEX).matcher(args);
        if (matcher.find()) {
            return matcher.group().replaceAll("(?<=[\\d]{3})\\d(?=[\\d]{4})",
                    "*");
        }
        return args;
    }

    public static String formatArrayList(List<String> array) {
        if (array == null || array.size() == 0)
            return "";

        StringBuilder buffer = new StringBuilder();
        int offset = array.size() - 1;
        for (int i = 0; i < offset; i++) {
            buffer.append(array.get(i)).append(",");
        }
        buffer.append(array.get(offset));
        return buffer.toString();
    }

    public static int formatInt(float orginal) {
        BigDecimal b = new BigDecimal(orginal);
        return b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }

    public static float formatFloat(float orginal) {
        BigDecimal b = new BigDecimal(orginal);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static Double formatDouble(float orginal) {
        BigDecimal b = new BigDecimal(orginal);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void showMsg(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            //mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static void showMsg(Context context, CharSequence msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            //mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static void showMsg(Context context, int id) {
        if (mToast == null) {
            mToast = Toast.makeText(context, id, Toast.LENGTH_SHORT);
            //mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(id);
        }
        mToast.show();
    }

    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        long time = System.currentTimeMillis();
        return sdf.format(time);
    }

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * 只能以 “+” 或者 数字开头；后面的内容只能包含 “-” 和 数字。
     * */
    private final static String MOBILE_NUMBER_CHARS = "^[+0-9][-0-9]{1,}$";

    public static boolean isValidMobileNumber(String s) {
        if (TextUtils.isEmpty(s))
            return true;
        Pattern p = Pattern.compile(MOBILE_NUMBER_CHARS);
        Matcher m = p.matcher(s);
        return m.matches();
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    // 取得AppKey
    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (NameNotFoundException e) {

        }
        return appKey;
    }

    // 取得版本号
    public static String GetVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static boolean isReadableASCII(CharSequence string) {
        if (TextUtils.isEmpty(string))
            return false;
        try {
            Pattern p = Pattern.compile("[\\x20-\\x7E]+");
            return p.matcher(string).matches();
        } catch (Throwable e) {
            return true;
        }
    }

    public static String getDeviceId(Context context) {
        return JPushInterface.getUdid(context);
    }

    public static String getRegistrationID(Context context) {
        return JPushInterface.getRegistrationID(context);
    }

    public static String getImei(Context context) {
        String imei= "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (imei.isEmpty()) {
            return getSerialNo();
        }
        return imei;
    }

    public static String getSerialNo() {
        return android.os.Build.SERIAL;
    }

    public static List<GoodInfo> parseJsonData(String json) {
        List<GoodInfo> goods = new ArrayList<GoodInfo>();
        GoodInfo info = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == RESULT_OK) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray result = new JSONArray(jsonArray.getString(i));
                    for (int j = 0; j < result.length(); j++) {
                        JSONObject object = result.getJSONObject(j);
                        info = new GoodInfo(object.getInt("id"),
                                object.getString("name"),
                                object.getString("unit_name"),
                                object.getString("unit"),
                                Float.parseFloat(object
                                        .getString("purchasing_price")),
                                Float.parseFloat(object
                                        .getString("purchasing_point")),
                                object.getString("img_1"),
                                object.getString("img_2"));
                        goods.add(info);
                    }
                }
            }
        } catch (JSONException e) {
            LogUtils.e(TAG, "parseJsonData:" + e.toString());
        }
        return goods;
    }

    public static List<OwnerInfo> parseOwnerData(String json) {
        List<OwnerInfo> results = new ArrayList<OwnerInfo>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == RESULT_OK) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    results.add(new OwnerInfo(object.getInt("id"), object
                            .getString("user_nickname"),
                            object.getInt("score"), object.getString("mobile")));
                }
            }
        } catch (JSONException e) {
            LogUtils.e(TAG, "parseResultData:" + e.toString());
        }
        return results;
    }

    public static List<String> parseSlideList(String json) {
        List<String> results = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == RESULT_OK) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    results.add(object.getString("image"));
                }
            }
        } catch (JSONException e) {
            LogUtils.e(TAG, "parseSlideList:" + e.toString());
        }
        return results;
    }

    public static OrderResultInfo parseResultData(String json) {
        OrderResultInfo result = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code != Utils.RESULT_OK) {
                return new OrderResultInfo(code, 0, 0, 0);
            }
            JSONObject data = jsonObject.getJSONObject("data");
            long time = data.getLong("add_time");
            int point = data.getInt("points_number");
            int score = data.getInt("user_score");
            return new OrderResultInfo(code, time, point, score);
        } catch (JSONException e) {
            LogUtils.e(TAG, "parseResultData:" + e.toString());
        }
        return result;
    }

    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes());
            return getHashString(digest);

        } catch (NoSuchAlgorithmException e) {
            LogUtils.e(TAG, "getMD5:" + e.toString());
        }
        return null;
    }

    public static String buildRfidJson(List<String> rfids) {
        return buildRfidJson(Utils.formatArrayList(rfids));
    }

    public static String buildRfidJson(String rfid) {
        JSONObject object = new JSONObject();
        try {
            object.put("rfids", rfid);
        } catch (JSONException e) {
            LogUtils.e(TAG, "buildRfidJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildRfidJson:" + object.toString());
        return object.toString();
    }

    public static String buildSlideListJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", 4);
        } catch (JSONException e) {
            LogUtils.e(TAG, "buildSlideListJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildSlideListJson:" + object.toString());
        return object.toString();
    }

    public static String buildScoreCodeJson(String mobile) {
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", mobile);
        } catch (Exception e) {
            LogUtils.e(TAG, "buildScoreCodeJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildScoreCodeJson:" + object.toString());
        return object.toString();
    }

    public static ScoreInfo parseScoreCode(String json) {
        ScoreInfo scoreInfo = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == RESULT_OK) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                scoreInfo = new ScoreInfo(code, jsonObject.getString("msg"),
                        dataObject.getInt("id"),
                        dataObject.getString("name"),
                        dataObject.getString("mobile"),
                        dataObject.getInt("score"));
            } else {
                scoreInfo = new ScoreInfo(code, jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "parseScoreCode:" + e.toString());
        }
        return scoreInfo;
    }

    public static String buildLoginJson(String mobile, boolean isMobile) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", mobile);
            object.put("type", isMobile ? 1 : 2);
        } catch (Exception e) {
            LogUtils.e(TAG, "buildLoginJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildLoginJson:" + object.toString());
        return object.toString();
    }

    public static OwnerInfo parseLoginCode(String json) {
        OwnerInfo owner = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == RESULT_OK) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                owner = new OwnerInfo(dataObject.getInt("id"),
                        dataObject.getString("user_nickname"),
                        dataObject.getInt("score"),
                        dataObject.getString("mobile"));
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "parseLoginCode:" + e.toString());
        }
        return owner;
    }

    public static String buildStringJson(String mobile) {
        JSONObject object = new JSONObject();
        try {
            object.put("mobile", mobile);
        } catch (Exception e) {
            LogUtils.e(TAG, "buildStringJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildStringJson:" + object.toString());
        return object.toString();
    }

    public static String buildLoginJson(String name) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", name);
            object.put("password", "111111");
        } catch (Exception e) {
            LogUtils.e(TAG, "buildLoginJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildLoginJson:" + object.toString());
        return object.toString();
    }

    public static DeviceInfo parseLoginResult(String json) {
        DeviceInfo device = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == RESULT_OK) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                JSONObject type1 = dataObject.getJSONObject("machine_type_1");
                JSONObject type2 = dataObject.getJSONObject("machine_type_2");
                device = new DeviceInfo(code, jsonObject.getString("msg"),
                        dataObject.getInt("id"),
                        dataObject.getString("user_nickname"),
                        dataObject.getString("signature"),
                        dataObject.getString("token"),
                        new MachineType(type1.getInt("id"),
                                type1.getString("name"),
                                type1.getString("unit_name"),
                                type1.getString("unit"),
                                type1.getString("purchasing_point")),
                        new MachineType(type2.getInt("id"),
                                type2.getString("name"),
                                type2.getString("unit_name"),
                                type2.getString("unit"),
                                type2.getString("purchasing_point"))
                        );
            } else {
                device = new DeviceInfo(code, jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "parseLoginResult:" + e.toString());
        }
        return device;
    }

    public static String buildQrJson(String token) {
        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("type", 0);
        } catch (Exception e) {
            LogUtils.e(TAG, "buildQrJson:" + e.toString());
        }
        LogUtils.d(TAG, "buildQrJson:" + object.toString());
        return object.toString();
    }

    public static QrCode parseQrResult(String json) {
        QrCode qrCode = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            if (code == RESULT_OK) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                qrCode = new QrCode(code, jsonObject.getString("msg"),
                        dataObject.getString("ticket"),
                        dataObject.getString("url"),
                        dataObject.getString("getImg"));
            } else {
                qrCode = new QrCode(code, jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "parseQrResult:" + e.toString());
        }
        return qrCode;
    }

    private static String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();
        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }
        return builder.toString();
    }

    public static String buildPushCode(String token, String code) {
        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("code", code);
        } catch (Exception e) {
            LogUtils.e(TAG, "buildPushCode:" + e.toString());
        }
        LogUtils.d(TAG, "buildPushCode:" + object.toString());
        return object.toString();
    }

    public static ResultInfo parsePushCode(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");
            if (code == RESULT_OK) {
                return new ResultInfo(code, msg);
            } else if (!TextUtils.isEmpty(msg)) {
                return new ResultInfo(code, msg);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "parsePushCode:" + e.toString());
        }
        return null;
    }

    /** 10-normal;20-0ffline;30-exception;40-full */
    public static String buildStateCode(String token, int state) {
        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("state", state);
        } catch (Exception e) {
            LogUtils.e(TAG, "buildStateCode:" + e.toString());
        }
        LogUtils.d(TAG, "buildStateCode:" + object.toString());
        return object.toString();
    }

    public static void dumpJson2Db(Context context, byte[] data, String name) {
        File cache = context.getExternalFilesDir(DIR);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        File file = new File(cache, name);
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LogUtils.e(TAG, "createNewFile:" + e.toString());
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.close();
        } catch (Exception e) {
            LogUtils.e(TAG, "dumpJson2Db:" + e.toString());
        }
    }

    public static byte[] getdumpFromDb(Context context, String name) {
        byte[] buffer = new byte[2048];
        File cache = context.getExternalFilesDir(DIR);
        File file = new File(cache, name);
        if (!file.exists()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            fis.close();
            return out.toByteArray();
        } catch (IOException e) {
            LogUtils.e(TAG, "getdumpFromDb:" + e.toString());
        }
        return null;
    }

    public static String timeFormat(final long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (time < 1528724131) {
            return sdf.format(new Date());
        }
        return sdf.format(new Date(time * 1000));
    }

    public static boolean isValidNumber(final String number) {
        return isPhone(number) || isMobile(number);
    }

    private static boolean isPhone(final String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    public static boolean isMobile(final String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        String number = normalizeNumber(str);
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3-9][0-9]{9}$");
        m = p.matcher(number);
        b = m.matches();
        return b;
    }

    private static final SparseIntArray KEYPAD_MAP = new SparseIntArray();
    static {
        KEYPAD_MAP.put('a', '2');
        KEYPAD_MAP.put('b', '2');
        KEYPAD_MAP.put('c', '2');
        KEYPAD_MAP.put('A', '2');
        KEYPAD_MAP.put('B', '2');
        KEYPAD_MAP.put('C', '2');

        KEYPAD_MAP.put('d', '3');
        KEYPAD_MAP.put('e', '3');
        KEYPAD_MAP.put('f', '3');
        KEYPAD_MAP.put('D', '3');
        KEYPAD_MAP.put('E', '3');
        KEYPAD_MAP.put('F', '3');

        KEYPAD_MAP.put('g', '4');
        KEYPAD_MAP.put('h', '4');
        KEYPAD_MAP.put('i', '4');
        KEYPAD_MAP.put('G', '4');
        KEYPAD_MAP.put('H', '4');
        KEYPAD_MAP.put('I', '4');

        KEYPAD_MAP.put('j', '5');
        KEYPAD_MAP.put('k', '5');
        KEYPAD_MAP.put('l', '5');
        KEYPAD_MAP.put('J', '5');
        KEYPAD_MAP.put('K', '5');
        KEYPAD_MAP.put('L', '5');

        KEYPAD_MAP.put('m', '6');
        KEYPAD_MAP.put('n', '6');
        KEYPAD_MAP.put('o', '6');
        KEYPAD_MAP.put('M', '6');
        KEYPAD_MAP.put('N', '6');
        KEYPAD_MAP.put('O', '6');

        KEYPAD_MAP.put('p', '7');
        KEYPAD_MAP.put('q', '7');
        KEYPAD_MAP.put('r', '7');
        KEYPAD_MAP.put('s', '7');
        KEYPAD_MAP.put('P', '7');
        KEYPAD_MAP.put('Q', '7');
        KEYPAD_MAP.put('R', '7');
        KEYPAD_MAP.put('S', '7');

        KEYPAD_MAP.put('t', '8');
        KEYPAD_MAP.put('u', '8');
        KEYPAD_MAP.put('v', '8');
        KEYPAD_MAP.put('T', '8');
        KEYPAD_MAP.put('U', '8');
        KEYPAD_MAP.put('V', '8');

        KEYPAD_MAP.put('w', '9');
        KEYPAD_MAP.put('x', '9');
        KEYPAD_MAP.put('y', '9');
        KEYPAD_MAP.put('z', '9');
        KEYPAD_MAP.put('W', '9');
        KEYPAD_MAP.put('X', '9');
        KEYPAD_MAP.put('Y', '9');
        KEYPAD_MAP.put('Z', '9');
    }

    public static String normalizeNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int len = phoneNumber.length();
        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            // Character.digit() supports ASCII and Unicode digits (fullwidth,
            // Arabic-Indic, etc.)
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (sb.length() == 0 && c == '+') {
                sb.append(c);
            } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                sb.append((char) KEYPAD_MAP.get(c, c));
            }
        }
        return sb.toString();
    }

    public static Bitmap obtainQRcode(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static boolean createQRcode(String content, int width,
            int height, Bitmap logoBm) {
        try {
            if (content == null || "".equals(content)) {
                return false;
            }

            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 1); // default is 4

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = new QRCodeWriter().encode(content,
                        BarcodeFormat.QR_CODE, width, height, hints);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            if (logoBm != null) {
                bitmap = addLogo(bitmap, logoBm);
            }
            if (bitmap == null) {
                return false;
            }

            File cache = new File(Environment.getExternalStorageDirectory(),
                    "cache");
            if (!cache.exists()) {
                cache.mkdirs();
            }
            File file = new File(cache, Constancts.QR_FILE_NAME);
            return bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                            new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        // logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight,
                Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2,
                    (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }

    public static String getQrFile() {
        File cache = new File(Environment.getExternalStorageDirectory(),
                "cache");
        File file = new File(cache, Constancts.QR_FILE_NAME);
        if (!file.exists()) {
            return null;
        }

        return file.toString();
    }

    public static int getIndex(String[] array) {
        int index = 0;
        index = Integer.parseInt(array[2] + array[3], 16);
        System.out.println("getIndex=" + index);
        return index;
    }

    public static float getWeight(String[] array) {
        boolean isPositive = true;
        isPositive = (array[3].equals("00") || (array[3].equals("01")));
        float weigth = 0;
        weigth = Integer.parseInt(array[4] + array[5], 16)/100f;
        System.out.println("getWeight=" + weigth);
        return weigth * (isPositive ? 1 : -1);
    }

    @Deprecated
    public void slowViewPager(ViewPager pager) {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            SlowSpeedScroller scroller = new SlowSpeedScroller(
                    pager.getContext(), new AccelerateInterpolator());
            field.set(pager, scroller);
            scroller.setmDuration(500);
        } catch (Exception e) {
            LogUtils.e(TAG, "", e);
        }
    }
}