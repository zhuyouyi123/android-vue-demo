package com.seek.config.controller;

import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.dto.share.ShareDTO;
import com.seek.config.entity.dto.share.ShareSetDTO;
import com.seek.config.entity.response.RespVO;

@AppController(path = "share/")
public class SharePreferenceController {

    @AppRequestMapper(path = "set", method = AppRequestMethod.POST)
    public void set(ShareSetDTO dto) {
        String key = dto.getKey();
        String value = dto.getValue();
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return;
        }

        SharePreferenceUtil.getInstance().shareSet(key, value);
    }

    @AppRequestMapper(path = "get")
    public RespVO<String> get(ShareDTO dto) {
        String key = dto.getKey();
        if (StringUtils.isBlank(key)) {
            return RespVO.failure("Missing");
        }

        return RespVO.success(SharePreferenceUtil.getInstance().shareGet(key));
    }

    @AppRequestMapper(path = "remove", method = AppRequestMethod.POST)
    public void remove(ShareDTO dto) {
        String key = dto.getKey();
        if (StringUtils.isBlank(key)) {
            return;
        }

        SharePreferenceUtil.getInstance().shareRemove(key);
    }
}
