package com.panvan.app.controller;

import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.data.enums.AgreementEnum;
import com.panvan.app.utils.SdkUtil;

@AppController(path = "sport")
public class SportController {

    /**
     * 开始运动
     */
    @AppRequestMapper(path = "/start", method = AppRequestMethod.POST)
    public void start() {
        SdkUtil.writeCommand(AgreementEnum.SPORTS_REPORTING.getRequestCommand(""));
    }
}
