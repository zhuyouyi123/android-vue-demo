package com.panvan.app.controller;

import android.util.Log;

import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.response.RespVO;

@AppController(path = "ble")
public class BleController {

    private static final String TAG = BleController.class.getSimpleName();

    @AppRequestMapper(path = "/init")
    public RespVO<Void> init() {
        Log.i(TAG, "init");
        return RespVO.success();
    }


}
