package com.panvan.app.controller;

import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.dfuupgrade.MyBleManager;
import com.panvan.app.Config;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.data.entity.vo.device.DeviceVO;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.DeviceService;

@AppController(path = "device")
public class DeviceController {

    @AppRequestMapper(path = "")
    public RespVO<DeviceVO> getInUseDevice() {
        return RespVO.success(DeviceService.getInstance().queryInUseDevice());
    }

    @AppRequestMapper(path = "/unbind", method = AppRequestMethod.POST)
    public RespVO<Boolean> unbind() {
        // MyBleManager.getInstance(Config.mainContext).dis();
        return RespVO.success(DeviceService.getInstance().unbind());
    }
}
