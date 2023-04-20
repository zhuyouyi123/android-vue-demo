package com.ble.blescansdk.ble.analysis;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.entity.BleDevice;

public abstract class AbstractDeviceAnalysis<T extends BleDevice> {

    public abstract T analysis(byte[] scanBytes, BluetoothDevice device,int rssi);

    protected abstract T handle(byte[] scanBytes,T t);

    public T preHandle(T t, BluetoothDevice device, int rssi) {
        t.setAddress(device.getAddress());
        t.setDeviceType(device.getType());
        t.setName(device.getName());
        t.setRssi(rssi);
        t.setScanTime(System.currentTimeMillis());
        return t;
    }
}
