package com.ble.blescansdk.ble.entity.seek;

import androidx.annotation.NonNull;

import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.entity.constants.SeekStandardDeviceConstants;

import java.util.List;

public class SeekStandardDevice extends BleDevice {
    // 是否是locSmart信标
    private int locSmartBeacon = SeekStandardDeviceConstants.DEFAULT_IS_LOC_SMART_BEACON;

    /**
     * 电量
     */
    private int battery = SeekStandardDeviceConstants.DEFAULT_BATTERY;

    private List<StandardThoroughfareInfo> thoroughfares;

    public boolean isLocSmart() {
        return locSmartBeacon == 1;
    }

    public int getLocSmartBeacon() {
        return locSmartBeacon;
    }

    public void setLocSmartBeacon(int locSmartBeacon) {
        this.locSmartBeacon = locSmartBeacon;
    }

    public List<StandardThoroughfareInfo> getThoroughfares() {
        return thoroughfares;
    }

    public void setThoroughfares(List<StandardThoroughfareInfo> thoroughfares) {
        this.thoroughfares = thoroughfares;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    @NonNull
    @Override
    public String toString() {
        return "SeekStandardDevice{" +
                "address='" + getAddress() + '\'' +
                ", rssi=" + getRssi() +
                ", name='" + getName() + '\'' +
                ", scanTime=" + getScanTime() +
                ", deviceType=" + getDeviceType() +
                ", locSmartBeacon=" + locSmartBeacon +
                ", thoroughfares=" + thoroughfares +
                '}';
    }
}
