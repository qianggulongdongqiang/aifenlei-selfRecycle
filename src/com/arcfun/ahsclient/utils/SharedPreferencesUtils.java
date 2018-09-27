package com.arcfun.ahsclient.utils;

import com.arcfun.ahsclient.data.DeviceInfo;
import com.arcfun.ahsclient.data.PackageInfo;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static final String TAG = "SharedPreferences";
    private static final String FILE_NAME = "user";
    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_SIGNATURE = "signature";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_QRCODE = "qrcode";
    private static final String KEY_REGISTER = "isRegister";
    // machine type
    private static final String KEY_MACHINE_ID = "machineId";
    private static final String KEY_MACHINE_NAME = "machineName";
    private static final String KEY_MACHINE_UNIT = "machineUnit";
    private static final String KEY_MACHINE_POINT = "machinePoint";
    private static SharedPreferences sp = null;

    public static void setDeviceInfo(Context c, DeviceInfo info) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        LogUtils.d(TAG, "setToken = " + info.getToken());
        SharedPreferences.Editor editor = sp.edit();
        if (info != null) {
            editor.putString(KEY_TOKEN, info.getToken());
            editor.putInt(KEY_DEVICEID, info.getId());
            editor.putString(KEY_SIGNATURE, info.getSignature());
            // machine
            editor.putInt(KEY_MACHINE_ID, info.getType2().getId());
            editor.putString(KEY_MACHINE_NAME, info.getType2().getName());
            editor.putString(KEY_MACHINE_UNIT, info.getType2().getUnit());
            editor.putFloat(KEY_MACHINE_POINT, info.getType2().getPoint());
            editor.commit();
        }
    }

    /**
     * 0 - package -weight; 1 - package - piece; 2 - bottle
     * 
     * @param c
     * @return
     */
    public static int getMachineType(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        int type = sp.getInt(KEY_MACHINE_ID, 0);
        switch (type) {
        case Constancts.TYPE_MACHINE_PACKAGE1:
            return 0;
        case Constancts.TYPE_MACHINE_PACKAGE2:
            return 1;
        case Constancts.TYPE_MACHINE_BOTTLE:
            return 2;

        }
        return 0;
    }

    public static PackageInfo getMachineInfo(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        PackageInfo info = new PackageInfo(sp.getInt(KEY_MACHINE_ID, 0),
                sp.getString(KEY_MACHINE_NAME, ""), sp.getString(
                        KEY_MACHINE_UNIT, "kg"), sp.getFloat(KEY_MACHINE_POINT,
                        0), 0);
        return info;
    }

    public static String getToken(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(KEY_TOKEN, "");
    }

    public static int getDeviceId(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(KEY_DEVICEID, 0);
    }

    public static String getSignature(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(KEY_SIGNATURE, "00001");
    }

    public static void setQrCode(Context c, String url) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        LogUtils.d(TAG, "updateQrCode = " + url);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_QRCODE, url);
        editor.commit();
    }

    public static String getQrCode(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(KEY_QRCODE, "");
    }

    public static void setRegister(Context c, boolean able) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        LogUtils.d(TAG, "setRegister = " + able);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_REGISTER, able);
        editor.commit();
    }

    public static boolean getRegister(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(KEY_REGISTER, false);
    }
}