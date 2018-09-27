package com.arcfun.ahsclient.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "zxz|";
    private static final boolean DEBUG = Log.isLoggable("aifenx",
            Log.DEBUG) || true;

    public static void d(String name, String msg) {
        if (DEBUG) {
            Log.d(TAG + name, msg);
        }
    }

    public static void i(String name, String msg) {
        if (DEBUG) {
            Log.i(TAG + name, msg);
        }
    }

    public static void w(String name, String msg) {
        if (DEBUG) {
            Log.w(TAG + name, msg);
        }
    }

    public static void v(String name, String msg) {
        if (DEBUG) {
            Log.v(TAG + name, msg);
        }
    }

    public static void e(String name, String msg) {
        if (DEBUG) {
            Log.e(TAG + name, msg);
        }
    }
    public static void e(String name, String msg, Throwable e) {
        if (DEBUG) {
            Log.e(TAG + name, msg, e);
        }
    }
}