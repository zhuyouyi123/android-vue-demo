package com.seek.config.controller;

import android.util.Log;

import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.dto.BleConnectDTO;
import com.seek.config.entity.dto.ScanInitDTO;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.ScanDataVO;
import com.seek.config.services.BleService;
import com.seek.config.services.impl.BleServiceImpl;
import com.seek.config.utils.I18nUtil;
import com.seek.config.utils.RegexUtil;

@AppController(path = "ble")
public class BleController {

    private static final String TAG = BleController.class.getSimpleName();

    private static final BleService bleService = BleServiceImpl.getInstance();

    @AppRequestMapper(path = "/init")
    public RespVO<Void> init(ScanInitDTO dto) {
        Log.i(TAG, "init");

        bleService.init(dto);

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


    @AppRequestMapper(path = "/connect", method = AppRequestMethod.POST)
    public RespVO<Void> connect(BleConnectDTO dto) {
        if (!RegexUtil.macRegexMatch(dto.getAddress())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.DEVICE_ADDRESS_FORMAT_ERROR));
        }

        bleService.connect(dto.getAddress());
        return RespVO.success();
    }

    @AppRequestMapper(path = "/connect/status")
    public RespVO<Integer> getConnectionStatus(BleConnectDTO dto) {
        if (!RegexUtil.macRegexMatch(dto.getAddress())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.DEVICE_ADDRESS_FORMAT_ERROR));
        }

        return RespVO.success(bleService.getConnectionStatus(dto.getAddress()));
    }

    @AppRequestMapper(path = "/write", method = AppRequestMethod.POST)
    public RespVO<Void> write(BleConnectDTO dto) {
        if (!RegexUtil.macRegexMatch(dto.getAddress())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.DEVICE_ADDRESS_FORMAT_ERROR));
        }

        bleService.write(dto.getAddress());
        return RespVO.success();
    }

    @AppRequestMapper(path = "/startNotify", method = AppRequestMethod.POST)
    public RespVO<Void> startNotify(BleConnectDTO dto) {
        if (!RegexUtil.macRegexMatch(dto.getAddress())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.DEVICE_ADDRESS_FORMAT_ERROR));
        }

        bleService.startNotify(dto.getAddress());
        return RespVO.success();
    }

}
