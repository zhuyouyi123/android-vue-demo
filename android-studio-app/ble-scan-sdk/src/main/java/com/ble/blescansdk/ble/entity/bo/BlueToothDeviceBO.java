package com.ble.blescansdk.ble.entity.bo;

import android.bluetooth.BluetoothDevice;

import java.util.Arrays;

public class BlueToothDeviceBO {

    private BluetoothDevice device;

    private int rssi;

    private byte[] scanRecord;

    public BlueToothDeviceBO(BluetoothDevice device, int rssi, byte[] scanRecord){
        this.device = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    @Override
    public String toString() {
        return "BlueToothDeviceBO{" +
                "device=" + device +
                ", rssi=" + rssi +
                ", scanRecord=" + Arrays.toString(scanRecord) +
                '}';
    }
}
