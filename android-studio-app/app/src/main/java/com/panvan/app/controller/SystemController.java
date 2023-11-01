package com.panvan.app.controller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.panvan.app.AppActivity;
import com.panvan.app.AppInitTools;
import com.panvan.app.Config;
import com.panvan.app.DeviceIDTools;
import com.panvan.app.SecondActivity;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;

import java.util.HashMap;
import java.util.Map;

@AppController(path = "system")
public class SystemController {

    @AppRequestMapper(path = "/setTitle")
    public void setTitle(Boolean isShow) throws Exception {
        throw new Exception("暂不支持，请使用vue相关的标题栏组件");
    }

    @AppRequestMapper(path = "/setStatusBar")
    public void setStatusBar(Boolean isShow) throws Exception {
        throw new Exception("暂不支持，请使用vue相关的状态栏组件");
    }

    @AppRequestMapper(path = "/setStatusBarHeight")
    public double setStatusBarHeight() throws Exception {
        int result = 0;
        int resourceId = AppActivity.appActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            System.out.println(resourceId);
            result = AppActivity.appActivity.getResources().getDimensionPixelSize(resourceId);

        }
        return Config.Dp2Px(result) ;
    }

    @AppRequestMapper(path="/pushMessage",method = AppRequestMethod.POST)
    public Boolean pushMessage(String title,String text) {
        Toast.makeText(Config.mainContext, "text", Toast.LENGTH_SHORT).show();
        return true ;
    }


    @AppRequestMapper(path="/goPage",method = AppRequestMethod.POST)
    public Boolean goPage(){
        Intent intent=new Intent();
        intent.setClass(AppActivity.appActivity, SecondActivity.class);
        AppActivity.appActivity.startActivity(intent);
        return true;
    }

    /**
     * 获取设备唯一编号
     * @return
     */
    @AppRequestMapper(path="/getDeviceID",method = AppRequestMethod.GET)
    public String getDeviceUUID(){
        return DeviceIDTools.getDeviceUUID(AppActivity.appActivity);
    }

    /**
     * 获取设备唯一编号
     * @return
     */
    @AppRequestMapper(path="/openWebView",method = AppRequestMethod.GET)
    public String openWebView(String url){
        Intent intent= AppActivity.appActivity.openWebView(url);
        return intent.hashCode()+"";
    }

    /**
     * 获取设备唯一编号
     * @return
     */
    @AppRequestMapper(path="/closeWebView",method = AppRequestMethod.GET)
    public Boolean closeWebView(Integer formId){
        return  AppActivity.appActivity.closeWebView(formId);
    }

    /**
     * 获取系统 apk版本
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @AppRequestMapper(path="/versionInfo",method = AppRequestMethod.GET)
    public HashMap<String,String> getVersionInfo() throws Exception {
        Context context=AppActivity.appActivity;
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.toString();
        HashMap<String,String> hash=new HashMap<String,String>();
        hash.put("versionName",packInfo.versionName);
        hash.put("versionLongCode",packInfo.getLongVersionCode()+"");
        hash.put("versionBaseRevisionCode",packInfo.baseRevisionCode+"");
        hash.put("versionCode",packInfo.versionCode+"");
        return hash;
    }
}
