package com.ble.blescansdk.ble.entity.storage;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.Objects;

public abstract class AbstractStorage {

    public abstract void expire();

    public abstract void release();

    public boolean filterMacAndRssi(BleOptions.FilterInfo filterInfo, BluetoothDevice device, int rssi) {
        if (Objects.isNull(device)) {
            return false;
        }

        if (null == filterInfo) {
            return true;
        }

        String filterInfoMac = filterInfo.getAddress();
        if (StringUtils.isNotBlank(filterInfoMac)) {
            String address = device.getAddress().replaceAll(":", "");
            if (!address.contains(filterInfoMac.replaceAll(":", ""))) {
                return false;
            }
        }

        String filterInfoName = filterInfo.getName();
        if (StringUtils.isNotBlank(filterInfoName)) {
            String name = device.getName();
            if (StringUtils.isBlank(name) || !name.contains(filterInfoName)) {
                return false;
            }
        }

        Integer filterInfoRssi = filterInfo.getRssi();
        if (Objects.nonNull(filterInfoRssi)) {
            return rssi >= filterInfoRssi;
        }

        return true;
    }

}
