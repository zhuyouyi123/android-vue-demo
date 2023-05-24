package com.seek.config.controller;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.dto.SdkConfigDTO;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.BleFilterInfoVO;

@AppController(path = "sdk")
public class SdkController {

    @AppRequestMapper(path = "/filter-info")
    public RespVO<BleFilterInfoVO> queryFilterInfo() {
        BleOptions<BleDevice> bleOptions = BleSdkManager.getBleOptions();
        BleFilterInfoVO filterInfoVO = new BleFilterInfoVO();
        filterInfoVO.setFilterAddress(bleOptions.getFilterInfo().getAddress());
        filterInfoVO.setScanLevel(bleOptions.getBleScanLevel());
        filterInfoVO.setScanPeriod(bleOptions.getScanPeriod());
        filterInfoVO.setIntermittentTime(bleOptions.getIntermittentTime());
        filterInfoVO.setContinuousScanning(bleOptions.isContinuousScanning());
        filterInfoVO.setIntermittentScanning(bleOptions.isIntermittentScanning());
        filterInfoVO.setDeviceSurviveTime(bleOptions.getDeviceSurviveTime());
        filterInfoVO.setRssi(bleOptions.getFilterInfo().getRssi());
        filterInfoVO.setSupportConnectable(bleOptions.getFilterInfo().isSupportConnectable());
        filterInfoVO.setNormDevice(bleOptions.getFilterInfo().isNormDevice());

        return RespVO.success(filterInfoVO);
    }

    @AppRequestMapper(path = "/filter-info", method = AppRequestMethod.POST)
    public RespVO<BleFilterInfoVO> saveFilterInfo(SdkConfigDTO dto) {
        BleOptions<BleDevice> bleOptions = BleSdkManager.getBleOptions();
        bleOptions.setBleScanLevel(BleScanLevelEnum.getByLevel(dto.getScanLevel()));
        bleOptions.setDeviceSurviveTime(dto.getDeviceSurviveTime());
        bleOptions.setContinuousScanning(1 == dto.getContinuousScanning());
        bleOptions.setIntermittentTime(dto.getIntermittentTime());
        bleOptions.saveCacheConfig();
        return RespVO.success();
    }
}
