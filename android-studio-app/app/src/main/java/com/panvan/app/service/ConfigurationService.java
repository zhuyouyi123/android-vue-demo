package com.panvan.app.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.db.database.daoobject.ConfigurationDO;
import com.db.database.enums.ConfigurationGroupEnum;
import com.db.database.enums.ConfigurationTypeEnum;
import com.db.database.service.ConfigurationDataService;
import com.panvan.app.Receiver.service.NotificationMonitorService;
import com.panvan.app.data.entity.dto.ConfigurationSaveDTO;
import com.panvan.app.data.entity.vo.ConfigurationVO;
import com.panvan.app.receiver.call.CallViewModel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigurationService {

    private static ConfigurationService INSTANCE = null;

    public static ConfigurationService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ConfigurationService();
        }
        return INSTANCE;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<ConfigurationVO> queryByGroup(String group) {
        ConfigurationGroupEnum groupEnum = ConfigurationGroupEnum.getByGroup(group);

        Map<String, List<ConfigurationDO>> listMap = ConfigurationDataService.getInstance().queryByGroup(groupEnum.getName())
                .stream()
                .collect(Collectors.groupingBy(ConfigurationDO::getType));

        return ConfigurationTypeEnum.getByGroup(groupEnum).stream().map(e -> {
            ConfigurationVO vo = new ConfigurationVO();
            vo.setType(e.getType());
            vo.setEnable(e.getDefaultValue() == 1);
            vo.setValue(e.getDefaultValue());

            if (listMap.containsKey(e.getType())) {
                List<ConfigurationDO> configurations = listMap.get(e.getType());
                if (CollectionUtils.isNotEmpty(configurations)) {
                    vo.setEnable(configurations.get(0).getValue() == 1);
                    vo.setValue(configurations.get(0).getValue());
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }

    public void saveConfig(ConfigurationSaveDTO dto) {
        ConfigurationDO configurationDO = ConfigurationDataService.getInstance().queryByGroupAndType(dto.getGroup(), dto.getType());

        if (Objects.isNull(configurationDO)) {
            configurationDO = new ConfigurationDO();
            configurationDO.setConfigGroup(dto.getGroup());
            configurationDO.setType(dto.getType());
            configurationDO.setValue(dto.getValue());
            ConfigurationDataService.getInstance().insertConfig(configurationDO);
        } else {
            configurationDO.setValue(dto.getValue());
            ConfigurationDataService.getInstance().updateConfig(configurationDO);
        }

        ConfigurationTypeEnum type = ConfigurationTypeEnum.getType(dto.getType());

        if (null != type) {
            switch (type) {
                case NOTIFICATION_IN_CALL:
                case NOTIFICATION_IN_CALL_CONTACTS:
                    CallViewModel.getInstance().loadConfig(type, dto.getValue() == 1);
                    break;
                case NOTIFICATION_NOTIFY:
                case NOTIFICATION_SMS:
                    NotificationMonitorService.reloadEnable(type, dto.getValue() == 1);
                    break;
            }
        }
    }
}
