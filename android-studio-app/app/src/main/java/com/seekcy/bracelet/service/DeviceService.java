package com.seekcy.bracelet.service;

import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.db.database.AppDatabase;
import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;
import com.db.database.service.DeviceDataService;
import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.data.constants.SharePreferenceConstants;
import com.seekcy.bracelet.data.entity.vo.device.DeviceVO;

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

    public DeviceDO queryInUseDeviceDO() {
        return UserDatabase.getInstance().getDeviceDAO().queryInUse();
    }

    public void queryInUseDeviceDO(String address, DeviceDataService.Callback callback) {
        DeviceDataService.getInstance().query(address, callback);
    }

    public boolean unbind() {
        DeviceDO deviceDO = queryInUseDeviceDO();
        if (Objects.nonNull(deviceDO)) {
            DeviceService.getInstance().updateUseStatus(deviceDO);
            SharePreferenceUtil.getInstance().shareRemove(SharePreferenceConstants.DEVICE_ADDRESS_KEY);
            SharePreferenceUtil.getInstance().shareRemove(SharePreferenceConstants.DEVICE_HOLDER_KEY);
            AppDatabase.init(Config.mainContext, "FF:FF:FF:FF:FF:FF");
        }
        return true;
    }

    private void updateUseStatus(DeviceDO deviceDO) {
        deviceDO.setInUse(false);
        UserDatabase.getInstance().getDeviceDAO().update(deviceDO);
    }
}
