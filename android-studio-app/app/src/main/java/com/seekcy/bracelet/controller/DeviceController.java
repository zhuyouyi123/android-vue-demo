package com.seekcy.bracelet.controller;

import com.seekcy.bracelet.annotation.AppController;
import com.seekcy.bracelet.annotation.AppRequestMapper;
import com.seekcy.bracelet.annotation.AppRequestMethod;
import com.seekcy.bracelet.data.entity.vo.device.DeviceVO;
import com.seekcy.bracelet.response.RespVO;
import com.seekcy.bracelet.service.DeviceService;

@AppController(path = "device")
public class DeviceController {

    @AppRequestMapper(path = "")
    public RespVO<DeviceVO> getInUseDevice() {
        return RespVO.success(DeviceService.getInstance().queryInUseDevice());
    }

    @AppRequestMapper(path = "/unbind", method = AppRequestMethod.POST)
    public RespVO<Boolean> unbind() {
        return RespVO.success(DeviceService.getInstance().unbind());
    }
}
