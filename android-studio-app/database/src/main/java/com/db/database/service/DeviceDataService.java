package com.db.database.service;

import com.db.database.AppDatabase;
import com.db.database.UserDatabase;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.daoobject.DeviceDO;

import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class DeviceDataService {
    private static DeviceDataService INSTANCE = null;


    public static DeviceDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DeviceDataService();
        }
        return INSTANCE;
    }

    public void saveDevice(DeviceDO deviceDO) {
        Completable.fromAction(() -> {
                    DeviceDO device = UserDatabase.getInstance().getDeviceDAO().queryByMac(deviceDO.getAddress());
                    if (Objects.isNull(device)) {
                        UserDatabase.getInstance().getDeviceDAO().insert(deviceDO);
                    } else {
                        deviceDO.setId(device.getId());
                        UserDatabase.getInstance().getDeviceDAO().update(deviceDO);
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe();

    }
}
