package com.panvan.app.controller;

import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.data.entity.vo.device.DeviceVO;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.DeviceService;

@AppController(path = "device")
public class DeviceController {

    @AppRequestMapper
    public RespVO<DeviceVO> getInUseDevice() {
        return RespVO.success(DeviceService.getInstance().queryInUseDevice());
    }
}
