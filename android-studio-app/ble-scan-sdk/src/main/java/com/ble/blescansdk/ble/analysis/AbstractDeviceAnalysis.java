package com.ble.blescansdk.ble.analysis;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.entity.BleDevice;

public abstract class AbstractDeviceAnalysis<T extends BleDevice> {

    public abstract T analysis(T t, byte[] scanBytes, BluetoothDevice device, boolean isConnectable, int rssi);

    protected abstract T handle(byte[] scanBytes, T t, boolean isConnectable);

    public T preHandle(T t, BluetoothDevice device, boolean isConnectable, int rssi) {
        t.setAddress(device.getAddress());
        t.setDeviceType(device.getType());
        t.setName(device.getName());
        t.setRssi(rssi);
        t.setScanTime(System.currentTimeMillis());
        t.setConnectable(isConnectable);
        return t;
    }
}
