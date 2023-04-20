package com.ble.blescansdk.ble.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ble.blescansdk.ble.BleSdkManager;

import java.util.Objects;

public class SharePreferenceUtil {
    private static SharePreferenceUtil instance = null;
    private static SharedPreferences deploySharePreference = null;

    public static final String PREFERENCE_NAME = "loc_smart_share";


    public static SharePreferenceUtil getInstance() {
        if (instance == null) {
            instance = new SharePreferenceUtil();
            if (Objects.isNull(deploySharePreference)) {
                deploySharePreference = BleSdkManager.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            }
        }
        return instance;
    }


    public String shareGet(String key) {
        return deploySharePreference.getString(key, null);
    }


    public void shareSet(String key, String value) {
        deploySharePreference.edit().putString(key, value).apply();
    }


    public void shareRemove(String key) {
        deploySharePreference.edit().remove(key).apply();
    }
}
