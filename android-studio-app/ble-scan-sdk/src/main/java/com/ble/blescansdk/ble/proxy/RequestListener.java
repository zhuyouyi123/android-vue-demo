package com.ble.blescansdk.ble.proxy;

import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.callback.request.BleReadCallback;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.BleDevice;

public interface RequestListener<T extends BleDevice> {

    /**
     * start scanning
     *
     * @param callback   {@link BleScanCallback}
     * @param scanPeriod 扫描间隔
     */
    void startScan(BleScanCallback<T> callback, long scanPeriod);

    /**
     * 停止扫描
     */
    void stopScan();

    /**
     * 连接设备
     *
     * @param device   扫描到的设备
     * @param callback {@link BleScanCallback}
     * @return 连接结果
     */
    boolean connect(T device, BleConnectCallback<T> callback);

    /**
     * 读取数据
     *
     * @param device   待连接设备
     * @param callback {@link BleReadCallback}
     * @return 读取结果
     */
    boolean read(T device, BleReadCallback<T> callback);

    /**
     * 写入数据
     *
     * @param device   蓝牙设备对象
     * @param data     写入数据字节数组
     * @param callback 写入结果回调
     * @return 写入是否成功
     */
    boolean write(T device, byte[]data, BleWriteCallback<T> callback);

    boolean write(T device, String data, BleWriteCallback<T> callback);

    /**
     * 连接成功后，开始设置通知
     *
     * @param device   蓝牙设备对象
     * @param callback 通知回调
     * @deprecated Use {@link BleNotifyCallback (T, boolean, BleNotifyCallback)} instead.
     */
    void notify(T device, BleNotifyCallback<T> callback);

    void disconnect(T device);

}
