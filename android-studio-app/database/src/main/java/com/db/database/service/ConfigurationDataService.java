package com.db.database.service;

import com.db.database.UserDatabase;
import com.db.database.daoobject.ConfigurationDO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigurationDataService {

    private static ConfigurationDataService INSTANCE = null;

    public static ConfigurationDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ConfigurationDataService();
        }
        return INSTANCE;
    }

    public List<ConfigurationDO> queryByGroup(String group) {
        List<ConfigurationDO> list = UserDatabase.getInstance().getConfigurationDAO().queryByGroup(group);
        if (null == list) {
            list = new ArrayList<>();
        }
        return list;
    }

    public ConfigurationDO queryByGroupAndType(String group, String type) {
        return UserDatabase.getInstance().getConfigurationDAO().queryByGroupAndType(group, type);
    }

    public void updateConfig(ConfigurationDO configurationDO) {
        UserDatabase.getInstance().getConfigurationDAO().update(configurationDO);
    }

    public void insertConfig(ConfigurationDO configurationDO) {
        UserDatabase.getInstance().getConfigurationDAO().insert(configurationDO);
    }
}
