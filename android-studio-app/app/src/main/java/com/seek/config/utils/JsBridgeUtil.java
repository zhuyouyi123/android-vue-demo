package com.seek.config.utils;

import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;

import com.google.gson.Gson;
import com.seek.config.Config;

import java.util.HashMap;

public class JsBridgeUtil {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private static final Gson gson = new Gson();

    public static final String SCAN_RESULT = "SCAN_RESULT";

    public static final String SCAN_DEVICE_LIST_RESULT = "SCAN_DEVICE_LIST_RESULT";

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";



    public static void pushEvent(String eventName, Object message) {
        try {
            sHandler.post(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("eventName", eventName);
                    map.put("data", message);
                    String json = gson.toJson(map);


                    Config.webView.evaluateJavascript(String.format("javascript:commonAndroidEvent('%s')", json), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            System.out.println(s);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class Result {
        private String status;
        private Object data;
        private String type;

        public Result(String status, Object data) {
            this.status = status;
            this.data = data;
        }

        public Result setType(String type) {
            this.type = type;
            return this;
        }
    }

    public static Result success(Object data, String type) {
        Result result = new Result(SUCCESS, data);
        return result.setType(type);
    }

    public static Result success(Object data) {
        return new Result(SUCCESS, data);
    }

    public static Result fail(Object data) {
        return new Result(ERROR, data);
    }

    public static Result success() {
        return new Result(SUCCESS, true);
    }

    public static Result fail() {
        return new Result(ERROR, false);
    }

}
