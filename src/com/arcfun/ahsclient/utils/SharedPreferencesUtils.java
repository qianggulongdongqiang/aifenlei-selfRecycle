package com.arcfun.ahsclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.arcfun.ahsclient.data.DeviceInfo;
import com.arcfun.ahsclient.data.MachineInfo;
import com.arcfun.ahsclient.data.PackageInfo;

public class SharedPreferencesUtils {
    private static final String TAG = "SharedPreferences";
    private static final String FILE_NAME = "user";
    private static final String KEY_DEVICEID = "deviceId";
    private static final String KEY_SIGNATURE = "signature";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_QRCODE = "qrcode";
    private static final String KEY_REGISTER = "isRegister";
    private static final String KEY_STATE = "state";
    private static final String KEY_PROT1 = "port1";
    private static final String KEY_PROT2 = "port2";
    private static final String KEY_DEBUG = "isDebug";
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
        LogUtils.d(TAG, "set machine id = " + info.getType2().getId());
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

    public static void setDeviceInfo(Context c, MachineInfo info) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        if (info != null) {
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
        case Constancts.TYPE_MACHINE_PACKAGE:
        case Constancts.TYPE_MACHINE_PACKAGE1:
        case Constancts.TYPE_MACHINE_PACKAGE3:
        case Constancts.TYPE_MACHINE_BAG:
        case Constancts.TYPE_MACHINE_BAG1:
            return 0;
        case Constancts.TYPE_MACHINE_PACKAGE2:
            return 1;
        case Constancts.TYPE_MACHINE_BOTTLE:
        case Constancts.TYPE_MACHINE_BOTTLE_BL:
        case Constancts.TYPE_MACHINE_BOTTLE_SL:
            return 1;//TODO 2

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

    public static void setState(Context c, int state) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_STATE, state);
        editor.commit();
    }

    public static int getState(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(KEY_STATE, 10);
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

    public static void setPort(Context c, int port, String path) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        if (port == 1) {
            editor.putString(KEY_PROT1, path);
        } else {
            editor.putString(KEY_PROT2, path);
        }
        editor.commit();
    }

    public static String getPort(Context c, int port) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        if (port == 1) {
            return sp.getString(KEY_PROT1, Utils.SERIAL_PROT_1);
        } else {
            return sp.getString(KEY_PROT2, Utils.SERIAL_PROT_2);
        }
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

    public static void setDebug(Context c, boolean isEnable) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_DEBUG, isEnable);
        editor.commit();
    }

    public static boolean getDebug(Context c) {
        if (sp == null) {
            sp = c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(KEY_DEBUG, false);
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