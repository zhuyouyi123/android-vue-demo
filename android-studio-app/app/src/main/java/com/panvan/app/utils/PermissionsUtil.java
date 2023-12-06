package com.panvan.app.utils;

import android.Manifest;
import android.app.Activity;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.panvan.app.Config;
import com.panvan.app.data.constants.PermissionsRequestConstants;

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

}
