package com.ble.blescansdk.ble.proxy.request;

import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.annotation.Implement;
import com.ble.blescansdk.ble.callback.IWriteWrapperCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.BleDevice;

@Implement(WriteRequest.class)
public class WriteRequest<T extends BleDevice> implements IWriteWrapperCallback<T> {

    private BleWriteCallback<T> bleWrapperCallback;
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getInstance();

    @Override
    public void onWriteSuccess(T device, BluetoothGattCharacteristic characteristic) {
        if (bleWrapperCallback != null) {
            bleWrapperCallback.onWriteSuccess(device, characteristic);
        }
    }

    @Override
    public void onWriteFailed(T device, int failedCode) {
        if (bleWrapperCallback != null) {
            bleWrapperCallback.onWriteFailed(device, failedCode);
        }
    }

    public boolean write(T device, byte[] data, BleWriteCallback<T> callback) {
        this.bleWrapperCallback = callback;
        return bleRequest.writeCharacteristic(device.getAddress(), data);
    }

    public boolean write(T device, String data, BleWriteCallback<T> callback) {
        this.bleWrapperCallback = callback;
        if (null == device) {
            return false;
        }
        return bleRequest.writeCharacteristic(device.getAddress(), data);
    }
}
