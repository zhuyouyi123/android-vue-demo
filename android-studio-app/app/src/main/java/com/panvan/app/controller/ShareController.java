package com.panvan.app.controller;


import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.response.RespVO;

@AppController(path = "share")
public class ShareController {

    @AppRequestMapper(path = "/get")
    public RespVO<String> shareGet(String key) {
        return RespVO.success(SharePreferenceUtil.getInstance().shareGet(key));
    }
}
