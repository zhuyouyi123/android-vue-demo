package com.panvan.app.controller;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.panvan.app.Receiver.service.NotificationMonitorService;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.data.entity.dto.ConfigurationSaveDTO;
import com.panvan.app.data.entity.vo.ConfigurationVO;
import com.panvan.app.receiver.call.CallViewModel;
import com.panvan.app.response.RespVO;
import com.panvan.app.service.ConfigurationService;

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
