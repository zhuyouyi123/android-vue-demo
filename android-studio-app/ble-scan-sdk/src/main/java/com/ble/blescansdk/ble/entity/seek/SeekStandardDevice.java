package com.ble.blescansdk.ble.entity.seek;

import androidx.annotation.NonNull;

import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.entity.constants.SeekStandardDeviceConstants;

public class SeekStandardDevice extends BleDevice {
    // 是否是locSmart信标
    private int locSmartBeacon = SeekStandardDeviceConstants.DEFAULT_IS_LOC_SMART_BEACON;

    /**
     * 电量
     */
    private int battery = SeekStandardDeviceConstants.DEFAULT_BATTERY;

    private long broadcastInterval = SeekStandardDeviceConstants.DEFAULT_BROADCAST_INTERVAL;

    private StandardThoroughfareInfo standardThoroughfareInfo;

    public boolean isLocSmart() {
        return locSmartBeacon == 1;
    }

    public int getLocSmartBeacon() {
        return locSmartBeacon;
    }

    public void setLocSmartBeacon(int locSmartBeacon) {
        this.locSmartBeacon = locSmartBeacon;
    }

    public StandardThoroughfareInfo getStandardThoroughfareInfo() {
        if (null == standardThoroughfareInfo) {
            standardThoroughfareInfo = new StandardThoroughfareInfo();
        }
        return standardThoroughfareInfo;
    }

    public void setStandardThoroughfareInfo(StandardThoroughfareInfo standardThoroughfareInfo) {
        this.standardThoroughfareInfo = standardThoroughfareInfo;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public long getBroadcastInterval() {
        return broadcastInterval;
    }

    public void setBroadcastInterval(long broadcastInterval) {
        this.broadcastInterval = broadcastInterval;
    }

    @NonNull
    @Override
    public String toString() {
        return "SeekStandardDevice{" +
                "address=" + getAddress() +
                "name=" + getName() +
                "scanTime=" + getScanTime() +
                "rssi=" + getRssi() +
                "connectable=" + isConnectable() +
                "locSmartBeacon=" + locSmartBeacon +
                ", battery=" + battery +
                ", broadcastInterval=" + broadcastInterval +
                ", standardThoroughfareInfo=" + standardThoroughfareInfo +
                '}';
    }
}
