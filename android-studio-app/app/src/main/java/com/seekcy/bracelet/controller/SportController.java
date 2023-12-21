package com.seekcy.bracelet.controller;

import com.seekcy.bracelet.annotation.AppController;
import com.seekcy.bracelet.annotation.AppRequestMapper;
import com.seekcy.bracelet.annotation.AppRequestMethod;
import com.seekcy.bracelet.data.enums.AgreementEnum;
import com.seekcy.bracelet.utils.SdkUtil;

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
