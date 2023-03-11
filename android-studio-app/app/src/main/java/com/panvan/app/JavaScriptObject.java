package com.panvan.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.response.BaseResponse;
import com.panvan.app.response.StatusCode;

import org.json.JSONObject;

import java.util.HashMap;

public class JavaScriptObject {

    private Context mContext;
    private WebView webView;

    public static Context mainContext;

    public JavaScriptObject(Context mContext, WebView _webView) {
        this.mContext = mContext;
        this.webView = _webView;
    }

    @JavascriptInterface //sdk17版本以上加上注解
    public String executeMethod(String action, String methodType, String jsonParam) {
        try {
            String _action = methodType.toUpperCase() + ":" + action;
            Object res = CoreEventHander.executeMethodOfController(_action, jsonParam, this.mContext);
            if(res instanceof String){
                return "{'code':'0',data:'"+ res.toString()+"'}";
            }
            Gson gson = new Gson();
            return "{'code':'0',data:'"+ gson.toJson(res)+"'}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{'code':'-1',data:'出现错误！"+ e.getMessage()+"'}";
        }

    }


    /**
     * 获取系统 apk版本
     * @return
     */
    @JavascriptInterface //sdk17版本以上加上注解
    public HashMap<String,String> getVersionInfo() throws Exception {
        Context context=this.mContext;
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
