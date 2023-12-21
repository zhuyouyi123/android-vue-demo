package com.seekcy.bracelet.data.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private final String appName;
    private final String packageName;
    private final Drawable appIcon;

    public AppInfo(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getPackageName() {
        return packageName;
    }
}
