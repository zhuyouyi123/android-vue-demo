package com.panvan.app.service;

import android.Manifest;
import android.app.Activity;
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
import com.panvan.app.utils.JsBridgeUtil;

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
            JsBridgeUtil.pushEvent(JsBridgeConstants.CAMERA_POPUP_SHOW, true);
        }
    }

    public void requestCameraPermission() {
        Activity activity = (Activity) Config.mainContext;
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PermissionsRequestConstants.CAMERA_PERMISSION_REQUEST_CODE);
    }
}
