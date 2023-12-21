package com.seekcy.bracelet.callback;

import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.seek.BraceletDevice;
import com.seekcy.bracelet.utils.LogUtil;

import java.util.Objects;

public class WriteCallback extends BleWriteCallback<BraceletDevice> {

    private static WriteCallback callback = null;

    public static WriteCallback getInstance() {
        if (Objects.isNull(callback)) {
            callback = new WriteCallback();
        }
        return callback;
    }

    @Override
    public void onWriteSuccess(BraceletDevice device, BluetoothGattCharacteristic characteristic) {
        LogUtil.info("写入成功");
    }

    @Override
    public void onWriteFailed(BraceletDevice device, int failedCode) {
        LogUtil.info("写入失败");
    }
}
