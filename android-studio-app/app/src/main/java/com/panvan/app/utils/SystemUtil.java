package com.panvan.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class SystemUtil {

    // 获取应用列表及图标的方法
    public List<AppInfo> getInstalledApps(Context context) {
        // 创建一个PackageManager对象
        PackageManager packageManager = context.getPackageManager();

        // 创建一个Intent，用于匹配所有已安装的应用程序
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // 通过queryIntentActivities方法获取ResolveInfo列表
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

        // 创建一个列表来存储应用程序信息
        List<AppInfo> appList = new ArrayList<>();

        // 遍历ResolveInfo列表，获取应用程序的名称和图标，并添加到列表中
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String appName = resolveInfo.loadLabel(packageManager).toString();
            Drawable appIcon = resolveInfo.loadIcon(packageManager);

            // 创建AppInfo对象，包含应用名称和图标
            AppInfo appInfo = new AppInfo(appName, appIcon);
            appList.add(appInfo);
        }

        // 返回应用程序信息列表
        return appList;
    }

    // 定义一个简单的AppInfo类，包含应用名称和图标
    static class AppInfo {
        private final String appName;
        private final Drawable appIcon;

        public AppInfo(String appName, Drawable appIcon) {
            this.appName = appName;
            this.appIcon = appIcon;
        }

        public String getAppName() {
            return appName;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }
    }

}
