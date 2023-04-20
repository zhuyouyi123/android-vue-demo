package com.ble.blescansdk.ble.callback;

import android.bluetooth.BluetoothGatt;

public interface IConnectWrapperCallback<T> {

    void onChange(T device);

    /**
     * When the callback when the error, such as app can only connect four devices
     * at the same time forcing the user to connect more than four devices will call back the method
     *
     * @param device ble device object
     */
    void onFailed(T device, int errorCode);

    /**
     * Set the notification feature to be successful and can send data
     *
     * @param device ble device object
     */
    void onSuccess(T device);

    /**
     * Set the notification here when the service finds a callback       setNotify
     *
     * @param device
     */
    void onServicesDiscovered(T device, BluetoothGatt gatt);
}
