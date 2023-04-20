package com.ble.blescansdk.ble.utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.ble.blescansdk.ble.BleSdkManager;

public class PermissionUtil {

    public static final int REQUEST_PERMISSION_LOCATION = 2;
    public static final int REQUEST_PERMISSION_WRITE = 3;
    public static final int REQUEST_GPS = 4;

    //判断某个权限是否打开
    public static boolean checkSelfPermission(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED;
    }

    /**
     * 校验未知权限信息
     *
     * @return 是否存在权限
     */
    public static boolean checkLocationPermission() {
        LocationManager locationManager = (LocationManager) BleSdkManager.getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isGpsOpen() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(BleSdkManager.getContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(BleSdkManager.getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean checkGpsStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isGpsOpen()) {
//            new AlertDialog.Builder(BleSdkManager.getContext())
//                    .setTitle("提示")
//                    .setMessage("为了更精确的扫描到Bluetooth LE设备,请打开GPS定位")
//                    .setPositiveButton("确定", (dialog, which) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        Activity activity = (Activity) BleSdkManager.getContext();
//                        activity.startActivityForResult(intent, REQUEST_GPS);
//                    })
//                    .setNegativeButton("取消", null)
//                    .create()
//                    .show();
            return false;
        } else {
            return true;
        }
    }

}
