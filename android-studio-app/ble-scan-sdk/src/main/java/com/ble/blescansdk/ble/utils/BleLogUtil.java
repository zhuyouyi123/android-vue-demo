package com.ble.blescansdk.ble.utils;

import android.util.Log;

import com.ble.blescansdk.ble.BleSdkManager;

public class BleLogUtil {

    private static String TAG = "BleSdkManager";
    private static boolean isDebug = false;

    public static void init() {
        BleLogUtil.i("BleLogUtil init success");
        isDebug = BleSdkManager.getBleOptions().isLogSwitch();
        TAG = BleSdkManager.getBleOptions().getLogTag();
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

}
