package com.db.database.service;

import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;

import java.util.Objects;

public class DeviceDataService {
    private static DeviceDataService INSTANCE = null;


    public static DeviceDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DeviceDataService();
        }
        return INSTANCE;
    }

    public void saveDevice(DeviceDO deviceDO) {
        DeviceDO device = UserDatabase.getInstance().getDeviceDAO().queryByMac(deviceDO.getAddress());
        if (Objects.isNull(device)) {
            UserDatabase.getInstance().getDeviceDAO().insert(deviceDO);
        } else {
            deviceDO.setId(device.getId());
            UserDatabase.getInstance().getDeviceDAO().update(deviceDO);
        }
    }
}
