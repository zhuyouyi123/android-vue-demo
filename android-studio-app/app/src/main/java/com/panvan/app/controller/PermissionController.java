package com.panvan.app.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.data.entity.vo.InitVO;
import com.panvan.app.data.enums.PermissionTypeEnum;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.PermissionService;

import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
@AppController(path = "permission")
public class PermissionController {

    @AppRequestMapper(path = "/camera")
    public RespVO<Void> startScanQr() {
        InitVO initVO = PermissionService.getInstance().checkPermission();

        if (!initVO.getBluetoothEnable()) {
            return RespVO.failure("请先打开蓝牙");
        }

        if (!initVO.getLocateEnable()) {
            PermissionService.getInstance().openLocateService();
            return RespVO.failure("位置服务未打开");
        }
        PermissionService.getInstance().startScanQr();
        return RespVO.success();
    }

    @AppRequestMapper(path = "/camera", method = AppRequestMethod.POST)
    public void requestCameraPermission() {
        PermissionService.getInstance().requestCameraPermission();
    }

    @AppRequestMapper(path = "/exist")
    public RespVO<Boolean> permissionExist(String type) {
        PermissionTypeEnum typeEnum = PermissionTypeEnum.getByType(type);
        return RespVO.success(PermissionService.getInstance().queryPermissionExist(typeEnum));
    }

    @AppRequestMapper(path = "", method = AppRequestMethod.POST)
    public RespVO<Boolean> request(String type) {
        PermissionTypeEnum typeEnum = PermissionTypeEnum.getByType(type);
        PermissionService.getInstance().requestPermission(typeEnum);
        return RespVO.success();
    }
}
