package com.seek.config.controller;

import android.content.Intent;

import com.seek.config.AppActivity;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.services.SystemService;
import com.seek.config.services.impl.SystemServiceImpl;

@AppController(path = "system")
public class SystemController {

    private SystemService systemService= SystemServiceImpl.getInstance();

    /**
     * 获取设备唯一编号
     *
     * @return
     */
    @AppRequestMapper(path = "/openWebView", method = AppRequestMethod.GET)
    public String openWebView(String url) {
        Intent intent = AppActivity.appActivity.openWebView(url);
        return intent.hashCode() + "";
    }

    /**
     * 获取设备唯一编号
     *
     * @return
     */
    @AppRequestMapper(path = "/closeWebView", method = AppRequestMethod.GET)
    public Boolean closeWebView(Integer formId) {
        return AppActivity.appActivity.closeWebView(formId);
    }

    @AppRequestMapper(path = "/scan-qr-code",method = AppRequestMethod.POST)
    public void scanQRCode() {
       systemService.scanQrCode();
    }
}
