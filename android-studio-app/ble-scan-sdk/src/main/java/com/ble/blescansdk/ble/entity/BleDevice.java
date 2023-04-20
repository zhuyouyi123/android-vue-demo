package com.ble.blescansdk.ble.entity;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.SKYDeviceTypeEnum;

import java.util.Arrays;

public class BleDevice implements Parcelable {
    public final static String TAG = BleDevice.class.getSimpleName();

    protected BleDevice(Parcel in) {
    }

    public BleDevice() {

    }

    public BleDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

        this.rssi = rssi;
        this.address = device.getAddress();
        this.name = device.getName();
        this.deviceType = device.getType();
        this.scanRecord = scanRecord;
        this.scanTime = System.currentTimeMillis();
    }

    /**
     * 连接状态
     * 2503 未连接状态
     * 2504 正在连接
     * 2505 连接成功
     */
    private int connectState = BleConnectStatusEnum.DISCONNECT.getStatus();

    /**
     * 蓝牙信号值
     */
    private int rssi;

    /**
     * 设备mac地址
     */
    private String address;

    /**
     *
     */
    private String name;

    /**
     * 扫描时间
     */
    private long scanTime;

    /**
     * DEVICE_TYPE_UNKNOWN = 0
     * DEVICE_TYPE_CLASSIC = 1
     * DEVICE_TYPE_LE = 2
     * DEVICE_TYPE_DUAL = 3
     */
    private int deviceType = SKYDeviceTypeEnum.DEVICE_TYPE_NULL.value();

    private byte[] scanRecord;

    private boolean autoConnect = false;

    public int getConnectState() {
        return connectState;
    }

    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    public boolean isConnected() {
        return connectState == BleConnectStatusEnum.CONNECTED.getStatus();
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public long getScanTime() {
        return scanTime;
    }

    public void setScanTime(long scanTime) {
        this.scanTime = scanTime;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel in) {
            return new BleDevice(in);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    @Override
    public String toString() {
        return "BleDevice{" +
                "connectState=" + connectState +
                ", rssi=" + rssi +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", scanTime=" + scanTime +
                ", deviceType=" + deviceType +
                ", scanRecord=" + Arrays.toString(scanRecord) +
                '}';
    }
}
