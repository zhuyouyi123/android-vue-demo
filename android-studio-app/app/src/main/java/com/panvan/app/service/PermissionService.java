package com.panvan.app.service;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bracelet.scancode.ScanQrActivity;
import com.panvan.app.Config;
import com.panvan.app.data.constants.ActiveForResultConstants;
import com.panvan.app.data.constants.JsBridgeConstants;
import com.panvan.app.data.constants.PermissionsRequestConstants;
import com.panvan.app.data.enums.PermissionTypeEnum;
import com.panvan.app.utils.JsBridgeUtil;
import com.panvan.app.utils.PermissionsUtil;

import java.util.Objects;

public class PermissionService {

    private static PermissionService INSTANCE = null;

    public static PermissionService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new PermissionService();
        }
        return INSTANCE;
    }

    public void scanQr() {
        try {
            Activity activity = (Activity) Config.mainContext;

            Intent intent = new Intent(activity, ScanQrActivity.class);

            activity.startActivityForResult(intent, ActiveForResultConstants.SCAN_QR_CODE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("scanQr", "scanQr: 1111");
        }
    }

    public void startScanQr() {
        // 检查相机权限
        if (ContextCompat.checkSelfPermission(Config.mainContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 已经授予相机权限
            scanQr();
        } else {
            // 尚未授予相机权限，需要请求权限
            JsBridgeUtil.pushEvent(JsBridgeConstants.POPUP_SHOW, "为了扫描设备二维码，APP需要使用摄像头，需要获取拍摄照片、录制视频的权限。");
        }
    }

    public void requestCameraPermission() {
        Activity activity = (Activity) Config.mainContext;
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PermissionsRequestConstants.CAMERA_PERMISSION_REQUEST_CODE);
    }

    /**
     * 查看是否存在权限
     *
     * @param typeEnum
     * @return
     */
    public boolean queryPermissionExist(PermissionTypeEnum typeEnum) {
        if (Objects.isNull(typeEnum)) {
            return false;
        }

        switch (typeEnum) {
            case READ_CONTACTS:
                return ContextCompat.checkSelfPermission(Config.mainContext, typeEnum.getName()) == PackageManager.PERMISSION_GRANTED;
            case READ_PHONE_STATE:
                boolean existReadState = ContextCompat.checkSelfPermission(Config.mainContext, PermissionTypeEnum.READ_PHONE_STATE.getName()) == PackageManager.PERMISSION_GRANTED;
                boolean existReadCallLog = ContextCompat.checkSelfPermission(Config.mainContext, PermissionTypeEnum.READ_CALL_LOG.getName()) == PackageManager.PERMISSION_GRANTED;
                return existReadState && existReadCallLog;
            case NOTIFICATION:
                return PermissionsUtil.isNotificationListenerEnabled();
        }

        return false;
    }

    public void requestPermission(PermissionTypeEnum typeEnum) {
        if (Objects.isNull(typeEnum)) {
            return;
        }
        if (typeEnum == PermissionTypeEnum.NOTIFICATION) {
            Config.mainContext.startActivity(new Intent(typeEnum.getName()));
        } else {
            String[] strings = {typeEnum.getName()};

            if (typeEnum == PermissionTypeEnum.READ_PHONE_STATE) {
                strings = new String[]{PermissionTypeEnum.READ_PHONE_STATE.getName(), PermissionTypeEnum.READ_CALL_LOG.getName()};
            }

            Activity activity = (Activity) Config.mainContext;
            ActivityCompat.requestPermissions(activity, strings, typeEnum.getRequestCode());
        }
    }
}
