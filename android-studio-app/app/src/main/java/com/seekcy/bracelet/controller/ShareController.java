package com.seekcy.bracelet.controller;


import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.seekcy.bracelet.annotation.AppController;
import com.seekcy.bracelet.annotation.AppRequestMapper;
import com.seekcy.bracelet.data.entity.vo.response.RespVO;

@AppController(path = "share")
public class ShareController {

    @AppRequestMapper(path = "/get")
    public RespVO<String> shareGet(String key) {
        return RespVO.success(SharePreferenceUtil.getInstance().shareGet(key));
    }
}
