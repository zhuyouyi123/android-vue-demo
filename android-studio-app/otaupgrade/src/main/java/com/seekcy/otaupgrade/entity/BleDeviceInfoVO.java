package com.seekcy.otaupgrade.entity;

import android.bluetooth.BluetoothDevice;

import com.seekcy.otaupgrade.enums.OtaUpgradeStatusEnum;

/**
 * BleDeviceInfoVO
 *
 * @author zhuyouyi
 * @date 2023年02月03日
 */
public class BleDeviceInfoVO {
    private String mac;

    private Integer rssi;

    private String deviceType;

    private String deviceVersion;

    private Boolean selected;

    /**
     * 升级状态
     * {@link OtaUpgradeStatusEnum}
     */
    private Integer upgradeState;

    /**
     * 升级进度
     */
    private String upgradeProgress;

    private BluetoothDevice bluetoothDevice;

    public BleDeviceInfoVO() {
        this.selected = false;
        this.upgradeState = OtaUpgradeStatusEnum.NULL_UPGRADE.getState();
        this.upgradeProgress = "0";
    }

    /**
     * 连接状态
     */
    private Boolean connectStatus;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public Boolean getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(Boolean connectStatus) {
        this.connectStatus = connectStatus;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Integer isUpgradeState() {
        return upgradeState;
    }

    public void setUpgradeState(Integer upgradeState) {
        this.upgradeState = upgradeState;
    }

    public String getUpgradeProgress() {
        return upgradeProgress;
    }

    public void setUpgradeProgress(String upgradeProgress) {
        this.upgradeProgress = upgradeProgress;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public String toString() {
        return "BleDeviceInfoVO{" +
                "mac='" + mac + '\'' +
                ", rssi=" + rssi +
                ", deviceType='" + deviceType + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", selected=" + selected +
                ", upgradeState=" + upgradeState +
                ", upgradeProgress='" + upgradeProgress + '\'' +
                ", bluetoothDevice=" + bluetoothDevice +
                ", connectStatus=" + connectStatus +
                '}';
    }
}
