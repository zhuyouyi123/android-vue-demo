package com.panvan.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.bracelet.scancode.ScanQrActivity;
import com.panvan.app.Config;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.data.constants.ActiveForResultConstants;
import com.panvan.app.response.RespVO;

@AppController(path = "scan-code")
public class ScanCodeController {

    @AppRequestMapper(path = "/start", method = AppRequestMethod.POST)
    public RespVO<Void> scanQr() {
        try {
            Activity activity = (Activity) Config.mainContext;

            Intent intent = new Intent(activity, ScanQrActivity.class);

            activity.startActivityForResult(intent, ActiveForResultConstants.SCAN_QR_CODE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("scanQr", "scanQr: 1111");
        }

        return RespVO.success();
    }
}
