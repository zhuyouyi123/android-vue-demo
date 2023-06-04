package com.ble.blescansdk.ble.proxy.request;

import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.annotation.Implement;
import com.ble.blescansdk.ble.callback.INotifyWrapperCallback;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;

@Implement(NotifyRequest.class)
public class NotifyRequest<T extends BleDevice> implements INotifyWrapperCallback<T> {

    private BleNotifyCallback<T> notifyCallback;
    private final BleRequestImpl<T> notifyRequest = BleRequestImpl.getInstance();


    @Override
    public void onChanged(T device, BluetoothGattCharacteristic characteristic) {
        if (null != notifyCallback) {
            notifyCallback.onChanged(device, characteristic);
        }
    }

    @Override
    public void onNotifySuccess(T device) {
        if (null != notifyCallback) {
            notifyCallback.onNotifySuccess(device);
        }
    }

    @Override
    public void onNotifyFailed(T device, int failedCode) {
        if (null != notifyCallback) {
            notifyCallback.onNotifyFailed(device, failedCode);
        }
    }

    @Override
    public void onNotifyCanceled(T device) {
        if (null != notifyCallback) {
            notifyCallback.onNotifyCanceled(device);
        }
    }

    public void notify(T device, boolean enable, BleNotifyCallback<T> callback) {
        notifyCallback = callback;
        try {
            if (null == notifyRequest) {
                if (null != notifyCallback) {
                    notifyCallback.onNotifyFailed(device, ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode());
                }
                return;
            }
            if (null == device) {
                if (null != notifyCallback) {
                    notifyCallback.onNotifyFailed(device, ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode());
                }
                return;
            }
            notifyRequest.setCharacteristicNotification(device.getAddress(), enable);
        } catch (Exception e) {
            if (null != notifyCallback) {
                notifyCallback.onNotifyFailed(device, ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode());
            }
        }
    }
}
