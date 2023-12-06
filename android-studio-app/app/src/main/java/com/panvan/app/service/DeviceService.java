package com.panvan.app.service;

import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;
import com.panvan.app.data.entity.vo.device.DeviceVO;

import java.util.Objects;

public class DeviceService {

    private static DeviceService INSTANCE = null;

    public static DeviceService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DeviceService();
        }
        return INSTANCE;
    }

    /**
     * 查询正在使用的设备
     *
     * @return {@link DeviceVO}
     */
    public DeviceVO queryInUseDevice() {
        DeviceDO deviceDO = UserDatabase.getInstance().getDeviceDAO().queryInUse();

        if (Objects.isNull(deviceDO)) {
            return null;
        }

        DeviceVO deviceVO = new DeviceVO();
        deviceVO.setAddress(deviceDO.getAddress());
        deviceVO.setModel(deviceDO.getModel());
        deviceVO.setBatter(deviceDO.getBatter());
        deviceVO.setFirmwareVersion(deviceDO.getFirmwareVersion());
        deviceVO.setInUse(deviceDO.getInUse());
        return deviceVO;
    }
}
