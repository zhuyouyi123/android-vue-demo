package com.ble.blescansdk.ble.callback.request;

import android.bluetooth.BluetoothGatt;

import com.ble.blescansdk.ble.entity.BleDevice;

/**
 * 蓝牙扫描回调
 * Created by Administrator on 2016/12/21.
 *
 * @param <T>
 */
public abstract class BleConnectCallback<T extends BleDevice> {

    /**
     * The callback is disconnected or connected when the connection is changed
     *
     * @param device ble device object
     */
    public abstract void onConnectChange(T device,int status);

    public void onConnectCancel(T device) {
    }

    /**
     * Set the notification feature to be successful and can send data
     *
     * @param device ble device object
     */
    public void onReady(T device) {
    }

    /**
     * Set the notification here when the service finds a callback       setNotify
     *
     * @param device
     */
    public void onServicesDiscovered(T device, BluetoothGatt gatt) {
    }

    /**
     * When the callback when the error, such as app can only connect four devices
     * at the same time forcing the user to connect more than four devices will call back the method
     *
     * @param device    ble device object
     * @param errorCode errorCode
     */
    public abstract void onConnectFailed(T device, int errorCode) ;

}
