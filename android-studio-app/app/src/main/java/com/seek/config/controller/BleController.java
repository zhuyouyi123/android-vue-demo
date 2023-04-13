package com.seek.config.controller;

import android.util.Log;

import com.ble.blescansdk.ble.BleSdkManager;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.ScanDataVO;
import com.seek.config.services.BleService;
import com.seek.config.services.impl.BleServiceImpl;

@AppController(path = "ble")
public class BleController {

    private static final String TAG = BleController.class.getSimpleName();

    private static final BleService bleService = BleServiceImpl.getInstance();

    @AppRequestMapper(path = "/init")
    public RespVO<Void> init() {
        Log.i(TAG, "init");

        bleService.init();

        return RespVO.success();
    }

    @AppRequestMapper(path = "/startScan", method = AppRequestMethod.POST)
    public void startScan() {
        bleService.startScan();
    }

    @AppRequestMapper(path = "/stopScan", method = AppRequestMethod.POST)
    public void stopScan() {
        bleService.stopScan();
    }

    @AppRequestMapper(path = "/list")
    public RespVO<ScanDataVO> list() {
        return RespVO.success(bleService.scanDevices());
    }


}
