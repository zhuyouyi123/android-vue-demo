package com.seekcy.bracelet.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.data.constants.PermissionsRequestConstants;

import java.util.Set;

public class PermissionsUtil {

    public static void requestBasePermissions(String[] permissions) {
        ActivityCompat.requestPermissions((Activity) Config.mainContext, permissions, PermissionsRequestConstants.BASE_REQUEST_CODE);
    }

    public static void requestAccessCoarseLocation() {
        ActivityCompat.requestPermissions((Activity) Config.mainContext, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionsRequestConstants.ACCESS_COARSE_LOCATION_CODE);
    }

    // 检查是否已经授予通知监听权限
    public static boolean isNotificationListenerEnabled() {
        return NotificationManagerCompat.getEnabledListenerPackages(Config.mainContext).contains(Config.mainContext.getPackageName());
    }

    /**
     * 校验位置权限是否打开
     *
     * @return 开关
     */
    public static boolean checkLocationPermission() {
        LocationManager locationManager = (LocationManager) Config.mainContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // if (!providerEnabled) {
        //     new AlertDialog.Builder(Config.mainContext)
        //             .setTitle("获取位置信息失败")
        //             .setMessage("未开启位置信息，是否前往开启")
        //             .setNegativeButton("取消", (dialog, which) -> Toast.makeText(Config.mainContext, "未开启位置信息，无法使用本服务", Toast.LENGTH_SHORT).show())
        //             .setPositiveButton("确定", (dialogInterface, i) -> {
        //                 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //                 ((Activity) Config.mainContext).startActivityForResult(intent, 1);
        //                 dialogInterface.dismiss();
        //             }).show();
        //     return true;
        // }
        return providerEnabled;
    }

}
