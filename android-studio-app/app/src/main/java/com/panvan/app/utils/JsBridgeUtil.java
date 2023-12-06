package com.panvan.app.utils;

import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;

import com.google.gson.Gson;
import com.panvan.app.Config;

import java.util.HashMap;

public class JsBridgeUtil {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private static final Gson gson = new Gson();

    public static void pushEvent(String eventName, Object message) {
        try {
            sHandler.post(() -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("eventName", eventName);
                map.put("data", message);
                String json = gson.toJson(map);

                Config.webView.evaluateJavascript(String.format("javascript:commonAndroidEvent('%s')", json), System.out::println);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handlePush(boolean needPush, String eventName, Object object) {
        if (needPush) {
            pushEvent(eventName, object);
        }
    }
}
