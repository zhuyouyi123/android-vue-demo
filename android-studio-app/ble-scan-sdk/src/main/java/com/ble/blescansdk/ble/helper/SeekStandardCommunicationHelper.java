package com.ble.blescansdk.ble.helper;

import android.bluetooth.BluetoothGattCharacteristic;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.seek.SeekStandardDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

public class SeekStandardCommunicationHelper {

    private static SeekStandardCommunicationHelper instance = null;

    private static String address = "";
    /**
     * 通知
     */
    private static boolean notify = false;

    /**
     * 是否需要秘钥 -1 未知 0 不需要 1需要
     */
    private static int needSecretKey = -1;


    public static SeekStandardCommunicationHelper getInstance() {
        if (null == instance) {
            instance = new SeekStandardCommunicationHelper();
        }
        return instance;
    }


    private static void initSuccess(String mac) {
        notify = true;
        needSecretKey = -1;
        address = mac;
    }

    private static void initError() {
        notify = false;
        needSecretKey = -1;
        address = "";
    }

    /**
     * 开启通知
     *
     * @param address 地址
     */
    public void startNotify(String address) {
        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getBleDevice(address);
        // 如果已连接
        if (bleDevice.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            BleSdkManager.getInstance().startNotify(bleDevice, bleNotifyCallback);
        }
    }

    /**
     * 写入数据
     * 参数 @_1_3_0_!
     */
    public void write(String data) {
        if (StringUtils.isBlank(data)) {
            return;
        }
        ConnectRequest<SeekStandardDevice> request = Rproxy.getRequest(ConnectRequest.class);
        SeekStandardDevice bleDevice = request.getBleDevice(address);
        BleSdkManager.getInstance().write(bleDevice, data, bleWriteCallback);
    }


    /**
     * 是否需要秘钥
     *
     * @return 是否
     */
    public boolean needSecretKey() {
        return false;
    }

    public boolean isNotify() {
        return notify;
    }

    private static final BleNotifyCallback<SeekStandardDevice> bleNotifyCallback = new BleNotifyCallback<SeekStandardDevice>() {
        @Override
        public void onChanged(SeekStandardDevice o, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            BleLogUtil.i("Start Notify Success");
            BleLogUtil.i(ProtocolUtil.byteArrToHexStr(bluetoothGattCharacteristic.getValue()));
        }

        @Override
        public void onNotifySuccess(SeekStandardDevice device) {
            BleLogUtil.i("Start Notify Success");
            initSuccess(device.getAddress());
        }

        @Override
        public void onNotifyFailed(SeekStandardDevice device, int failedCode) {
            BleLogUtil.i("Start Notify Failed");
            initError();
        }

        @Override
        public void onNotifyCanceled(SeekStandardDevice device) {
            BleLogUtil.i("Start Notify Cancelled");
        }
    };


    private static final BleWriteCallback<SeekStandardDevice> bleWriteCallback = new BleWriteCallback<SeekStandardDevice>() {
        @Override
        public void onWriteSuccess(SeekStandardDevice bleDevice, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            BleLogUtil.i("Write Success");
        }

        @Override
        public void onWriteFailed(SeekStandardDevice bleDevice, int i) {
            BleLogUtil.i("Write Failed");
        }
    };

}