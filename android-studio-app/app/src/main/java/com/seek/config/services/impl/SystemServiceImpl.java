package com.seek.config.services.impl;

import android.app.Activity;
import android.content.Intent;

import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.seek.config.Config;
import com.seek.config.ScanQrActivity;
import com.seek.config.entity.constants.ActiveForResultConstants;
import com.seek.config.services.SystemService;


public class SystemServiceImpl implements SystemService {

    private static SystemServiceImpl instance = null;

    public static SystemServiceImpl getInstance() {
        if (instance == null) {
            instance = new SystemServiceImpl();
        }
        return instance;
    }

    @Override
    public void scanQrCode() {
        // 清除存储的key value
        SharePreferenceUtil.getInstance().shareSet(ActiveForResultConstants.SCAN_QR_REQUEST_KEY, "");

        SharePreferenceUtil.getInstance().shareSet(ActiveForResultConstants.SCAN_QR_STATUS_REQUEST_KEY, "true");

        Activity activity = (Activity) Config.mainContext;

        Intent intent = new Intent(activity, ScanQrActivity.class);
        activity.startActivityForResult(intent, ActiveForResultConstants.SCAN_QR_CODE_REQUEST_CODE);
    }
}
