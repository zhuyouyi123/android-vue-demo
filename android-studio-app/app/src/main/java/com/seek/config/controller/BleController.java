package com.seek.config.controller;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.config.callback.BeaconBleCallback;
import com.ble.blescansdk.config.helper.BeaconSingleConfigHelper;
import com.google.gson.Gson;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.dto.BleConnectDTO;
import com.seek.config.entity.dto.BleWriteDTO;
import com.seek.config.entity.dto.ScanInitDTO;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.ScanDataVO;
import com.seek.config.services.BleService;
import com.seek.config.services.impl.BleServiceImpl;
import com.seek.config.utils.I18nUtil;
import com.seek.config.utils.JsBridgeUtil;
import com.seek.config.utils.RegexUtil;

import java.util.Objects;

@AppController(path = "ble")
public class BleController {

    private static final String TAG = BleController.class.getSimpleName();

    private static final BleService bleService = BleServiceImpl.getInstance();

    private static final BeaconBleCallback callback = JsBridgeUtil::pushEvent;

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

        BeaconSingleConfigHelper.getInstance().connect(dto.getAddress(), callback);
        return RespVO.success();
    }

    @AppRequestMapper(path = "/cancel", method = AppRequestMethod.POST)
    public RespVO<Void> cancelConnect(BleConnectDTO dto) {
        if (!RegexUtil.macRegexMatch(dto.getAddress())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.DEVICE_ADDRESS_FORMAT_ERROR));
        }

        BeaconSingleConfigHelper.getInstance().cancelConnect(dto.getAddress());
        return RespVO.success();
    }

    @AppRequestMapper(path = "/connect/status")
    public RespVO<Integer> getConnectionStatus(BleConnectDTO dto) {
        if (!RegexUtil.macRegexMatch(dto.getAddress())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.DEVICE_ADDRESS_FORMAT_ERROR));
        }

        return RespVO.success(bleService.getConnectionStatus(dto.getAddress()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @AppRequestMapper(path = "/write", method = AppRequestMethod.POST)
    public RespVO<Void> write(BleWriteDTO dto) {
        BeaconSingleConfigHelper.getInstance().write(dto.getKey(), dto.getData());
        BleLogUtil.e("开始执行写入：" + new Gson().toJson(dto));
        return RespVO.success();
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    @AppRequestMapper(path = "/connect/detail")
    public RespVO<SeekStandardDeviceHolder> getConnectDetail(BleConnectDTO dto) {
        if (Objects.isNull(dto) || StringUtils.isBlank(dto.getAddress())) {
            return RespVO.success();
        }
        String address = dto.getAddress();
        SeekStandardDeviceHolder instance = SeekStandardDeviceHolder.getInstance();

        if (StringUtils.isNotBlank(instance.getAddress()) && address.equals(instance.getAddress())) {
            return RespVO.success(instance);
        }

        SeekStandardDeviceHolder.release();
        return RespVO.success();
    }

    @AppRequestMapper(path = "/secret")
    public RespVO<String> querySecretKey() {
        return RespVO.success(bleService.querySecretKey());
    }


    @AppRequestMapper(path = "/startNotify", method = AppRequestMethod.POST)
    public RespVO<Void> startNotify(BleConnectDTO dto) {
        if (!RegexUtil.macRegexMatch(dto.getAddress())) {
            return RespVO.failure(I18nUtil.getMessage(I18nUtil.DEVICE_ADDRESS_FORMAT_ERROR));
        }

        BeaconSingleConfigHelper.getInstance().startNotify(dto.getAddress());

        return RespVO.success();
    }

}
