package com.seek.config.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;

import com.ble.blescansdk.ble.BleSdkManager;

public class SystemUtil {

    private static final int REQUEST_GPS = 4;

    public static void openGpsLocationSwitch() {
        new AlertDialog.Builder(BleSdkManager.getContext())
                .setTitle(I18nUtil.getMessage(I18nUtil.tips))
                .setMessage(I18nUtil.getMessage(I18nUtil.openLocationInfoSwitch))
                .setPositiveButton(I18nUtil.getMessage(I18nUtil.sure), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Activity activity = (Activity) BleSdkManager.getContext();
                    activity.startActivityForResult(intent, REQUEST_GPS);
                })
                .setNegativeButton(I18nUtil.getMessage(I18nUtil.cancel), null)
                .create()
                .show();
    }
}
