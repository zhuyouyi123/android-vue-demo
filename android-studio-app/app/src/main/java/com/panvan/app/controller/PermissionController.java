package com.panvan.app.controller;

import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.PermissionService;

@AppController(path = "permission")
public class PermissionController {

    @AppRequestMapper(path = "/camera")
    public RespVO<Boolean> startScanQr() {
        PermissionService.getInstance().startScanQr();
        return RespVO.success();
    }

    @AppRequestMapper(path = "/camera", method = AppRequestMethod.POST)
    public void requestCameraPermission() {
        PermissionService.getInstance().requestCameraPermission();
    }
}
