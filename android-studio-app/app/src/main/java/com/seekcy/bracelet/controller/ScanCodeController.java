package com.seekcy.bracelet.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.bracelet.scancode.ScanQrActivity;
import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.annotation.AppController;
import com.seekcy.bracelet.annotation.AppRequestMapper;
import com.seekcy.bracelet.annotation.AppRequestMethod;
import com.seekcy.bracelet.data.constants.ActiveForResultConstants;
import com.seekcy.bracelet.data.entity.vo.response.RespVO;

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
