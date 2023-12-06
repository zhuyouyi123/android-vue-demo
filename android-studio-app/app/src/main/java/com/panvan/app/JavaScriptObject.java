package com.panvan.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.panvan.app.response.RespVO;

import java.util.HashMap;

public class JavaScriptObject {

    private final Context mContext;

    public JavaScriptObject(Context mContext) {
        this.mContext = mContext;
    }

    // sdk17版本以上加上注解
    @RequiresApi(api = Build.VERSION_CODES.P)
    @JavascriptInterface
    public String executeMethod(String action, String methodType, String jsonParam) {
        try {
            String _action = methodType.toUpperCase() + ":" + action;
            Object res = CoreEventHander.executeMethodOfController(_action, jsonParam, this.mContext);
            Gson gson = new Gson();
            if (res instanceof String) {
                return gson.toJson(RespVO.success(res.toString()));
            }
            return gson.toJson(res);
        } catch (Exception e) {
            e.printStackTrace();
            return new Gson().toJson(RespVO.failure("操作失败，请稍后重试！"));
        }

    }


    /**
     * 获取系统 apk版本
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @JavascriptInterface //sdk17版本以上加上注解
    public HashMap<String, String> getVersionInfo() throws Exception {
        Context context = this.mContext;
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.toString();
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("versionName", packInfo.versionName);
        hash.put("versionLongCode", packInfo.getLongVersionCode() + "");
        hash.put("versionBaseRevisionCode", packInfo.baseRevisionCode + "");
        hash.put("versionCode", packInfo.versionCode + "");
        return hash;
    }
}
