package com.panvan.app.service;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.dfuupgrade.MyBleManager;
import com.db.database.AppDatabase;
import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;
import com.db.database.service.DeviceDataService;
import com.panvan.app.Config;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.data.entity.vo.device.DeviceVO;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.utils.SdkUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

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


    public boolean createBind(BluetoothDevice device) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(Config.mainContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        boolean bond = false;
        for (BluetoothDevice bondedDevice : bondedDevices) {
            if (bondedDevice.getAddress().equals(device.getAddress())) {
                bond = true;
                break;
            } else {
                removeBond(bondedDevice);
            }
        }

        if (!bond) {
            bond = device.createBond();
        }

        boolean finalBond = bond;

        queryInUseDeviceDO(device.getAddress(), new DeviceDataService.Callback() {
            @Override
            public void success(DeviceDO deviceDO) {
                deviceDO.setInUse(finalBond);
                DeviceDataService.getInstance().saveDevice(deviceDO);
            }

            @Override
            public void failed() {

            }
        });


        return bond;
    }

    public boolean unbind() {
        try {
            DeviceDO deviceDO = queryInUseDeviceDO();
            if (Objects.nonNull(deviceDO)) {
                DeviceService.getInstance().updateUseStatus(deviceDO);
            }

            AppDatabase.init(Config.mainContext, "FF:FF:FF:FF:FF:FF");

            SharePreferenceUtil.getInstance().shareRemove(SharePreferenceConstants.DEVICE_ADDRESS_KEY);
            SharePreferenceUtil.getInstance().shareRemove(SharePreferenceConstants.DEVICE_HOLDER_KEY);

            if (Objects.nonNull(DeviceHolder.DEVICE)) {
                DeviceHolder.getInstance().getBleManager().dis(() -> {
                    DeviceHolder.DEVICE = null;
                });
            }
            CommunicationService.getInstance().clearTask();
        } finally {
            DeviceHolder.getInstance().setBleManager(null);
        }
        return true;
    }

    private void updateUseStatus(DeviceDO deviceDO) {
        deviceDO.setInUse(false);
        UserDatabase.getInstance().getDeviceDAO().update(deviceDO);
    }

    private boolean removeBond(BluetoothDevice device) {
        Class btDeviceCls = BluetoothDevice.class;
        Method removeBond = null;
        try {
            removeBond = btDeviceCls.getMethod("removeBond");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        removeBond.setAccessible(true);
        try {
            return (boolean) removeBond.invoke(device);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }
}
