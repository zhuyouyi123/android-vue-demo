package com.seekcy.bracelet;

import android.content.Context;
import android.os.Build;
import android.webkit.JavascriptInterface;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.seekcy.bracelet.data.entity.vo.response.RespVO;

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

}
