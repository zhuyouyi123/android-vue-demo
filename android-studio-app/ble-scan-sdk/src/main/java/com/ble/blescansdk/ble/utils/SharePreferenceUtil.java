package com.ble.blescansdk.ble.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ble.blescansdk.ble.BleSdkManager;

import java.util.Objects;

public class SharePreferenceUtil {
    private static SharePreferenceUtil instance = null;
    private static SharedPreferences deploySharePreference = null;

    public static final String PREFERENCE_NAME = "loc_smart_share";

    /**
     * 最后使用的秘钥
     */
    public static final String LAST_USE_SECRET_KEY = "LAST_USE_SECRET_KEY";
    /**
     * sdk配置缓存
     */
    public static final String SDK_CONFIG_INFO_CACHE_KEY = "SDK_CONFIG_INFO_CACHE_KEY";

    public static final String APP_LANGUAGE = "APP_LANGUAGE";


    public static SharePreferenceUtil getInstance(Context context) {
        return init(context);
    }

    public static SharePreferenceUtil getInstance() {
        return init(BleSdkManager.getContext());
    }

    private static SharePreferenceUtil init(Context context) {
        if (instance == null) {
            instance = new SharePreferenceUtil();
            if (Objects.isNull(deploySharePreference)) {
                deploySharePreference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
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
