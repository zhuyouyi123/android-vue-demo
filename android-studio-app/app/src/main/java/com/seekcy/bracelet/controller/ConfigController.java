package com.seekcy.bracelet.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.seekcy.bracelet.receiver.call.CallViewModel;
import com.seekcy.bracelet.receiver.service.NotificationMonitorService;
import com.seekcy.bracelet.annotation.AppController;
import com.seekcy.bracelet.annotation.AppRequestMapper;
import com.seekcy.bracelet.data.entity.dto.ConfigurationSaveDTO;
import com.seekcy.bracelet.data.entity.vo.ConfigurationVO;
import com.seekcy.bracelet.data.entity.vo.response.RespVO;
import com.seekcy.bracelet.service.ConfigurationService;

import java.util.List;

@AppController(path = "configuration")
@RequiresApi(api = Build.VERSION_CODES.N)
public class ConfigController {

    @AppRequestMapper(path = "/init")
    public RespVO<Void> init() {

        CallViewModel.getInstance().loadConfig();
        NotificationMonitorService.reloadEnable();

        return RespVO.success();
    }

    @AppRequestMapper(path = "/list")
    public RespVO<List<ConfigurationVO>> queryConfig(String group) {
        return RespVO.success(ConfigurationService.getInstance().queryByGroup(group));
    }


    @AppRequestMapper(path = "/save")
    public RespVO<Void> saveConfig(ConfigurationSaveDTO dto) {
        ConfigurationService.getInstance().saveConfig(dto);
        return RespVO.success();
    }

}
