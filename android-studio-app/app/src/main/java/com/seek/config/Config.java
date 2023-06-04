package com.seek.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Config {

    /**
     * 1.当打包app时，请先设置APK=true
     * 2.使用 npm run build 打包vue文件
     * 3.使用android-studio 打包成apk文件
     */
    public static Boolean APK = false;

    /**
     * ble_scan_sdk_V0.0.1.jar
     * 基本项目目录
     */
    public static String basePackages = "com.seek.config";

    /**
     * 是否显示title
     */
    public static boolean showTitle = false;

    /**
     * 是否显示状态栏
     */
    public static boolean showStatusBar = true;

    public static boolean isNewApi = true;

    @SuppressLint("StaticFieldLeak")
    public static Context mainContext;

    @SuppressLint("StaticFieldLeak")
    public static WebView webView;

    @SuppressLint("SimpleDateFormat")
    public static DateFormat[] df = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd HH:mm"),
            new SimpleDateFormat("yyyy-MM-dd"),
    };

    /**
     * 日期转换
     *
     * @param val
     * @param tryCount
     * @return
     * @throws Exception
     */
    public static Date parseDate(String val, int tryCount) throws Exception {
        if (tryCount >= df.length) {
            return null;
        }
        try {
            return df[tryCount].parse(val);
        } catch (ParseException e) {
            tryCount++;
            return parseDate(val, tryCount);
        }
    }

    public static void loadUrl() {
        webView.loadUrl(getUrl());
    }

    public static String getUrl() {
        if (APK) {
            return "file:///android_asset/ui/index.html";
        }
        return "http://192.168.2.104:8888";
//        return "http://172.16.31.189:8888";
    }


}
