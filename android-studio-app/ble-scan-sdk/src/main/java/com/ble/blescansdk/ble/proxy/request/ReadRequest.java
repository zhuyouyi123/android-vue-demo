package com.ble.blescansdk.ble.proxy.request;

import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.annotation.Implement;
import com.ble.blescansdk.ble.callback.IReadWrapperCallback;
import com.ble.blescansdk.ble.callback.request.BleReadCallback;
import com.ble.blescansdk.ble.entity.BleDevice;

@Implement(ReadRequest.class)
public class ReadRequest <T extends BleDevice> implements IReadWrapperCallback<T> {

    private BleReadCallback<T> bleReadCallback;

    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getInstance();

    public boolean read(T device, BleReadCallback<T> callback){
        this.bleReadCallback = callback;
        return bleRequest.readCharacteristic(device.getAddress());
    }

    @Override
    public void onReadSuccess(T device, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onReadFailed(T device, int failedCode) {

    }


}
