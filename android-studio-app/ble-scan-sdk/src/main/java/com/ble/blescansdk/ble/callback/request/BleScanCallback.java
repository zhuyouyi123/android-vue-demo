package com.ble.blescansdk.ble.callback.request;

import com.ble.blescansdk.ble.entity.BleDevice;

import java.util.List;

/**
 * 蓝牙连接回调
 * Created by Administrator on 2016/12/21.
 */
public abstract class BleScanCallback<T extends BleDevice> {

    /**
     * Start the scan
     */
    public abstract void onStatusChange(boolean state);

    public abstract void onScanFailed(int errorCode);

    /**
     * Scan to device
     *
     * @param device     ble device object
     * @param rssi       rssi
     * @param scanRecord Bluetooth radio package
     */
    public abstract void onLeScan(T device, int rssi, byte[] scanRecord);

    public abstract void onLeScan(List<T> device);

    public abstract void onRssiMax(T device, int rssi);


}
