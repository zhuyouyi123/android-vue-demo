package com.ble.blescansdk.ble.proxy;

import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.callback.request.BleReadCallback;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.proxy.request.NotifyRequest;
import com.ble.blescansdk.ble.proxy.request.ReadRequest;
import com.ble.blescansdk.ble.proxy.request.ScanRequest;
import com.ble.blescansdk.ble.proxy.request.WriteRequest;

/**
 * 请求实现类
 *
 * @param <T>
 */

public class RequestImpl<T extends BleDevice> implements RequestListener<T> {


    public static <T extends BleDevice> RequestImpl<T> newRequestImpl() {
        return new RequestImpl<>();
    }


    @Override
    public void startScan(BleScanCallback<T> callback, long scanPeriod) {
        ScanRequest<T> request = Rproxy.getRequest(ScanRequest.class);
        request.startScan(callback, scanPeriod);
    }

    @Override
    public void stopScan() {
        ScanRequest<T> request = Rproxy.getRequest(ScanRequest.class);
        request.stopScan();
    }

    @Override
    public boolean connect(T device, BleConnectCallback<T> callback) {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        return request.connect(device, callback);
    }

    /**
     * 读取数据
     *
     * @param device   待连接设备
     * @param callback {@link BleReadCallback}
     * @return 读取结果
     */
    @Override
    public boolean read(T device, BleReadCallback<T> callback) {
        ReadRequest<T> request = Rproxy.getRequest(ReadRequest.class);
        return request.read(device, callback);
    }

    /**
     * 写入数据
     *
     * @param device   蓝牙设备对象
     * @param data     写入数据字节数组
     * @param callback 写入结果回调
     * @return 写入是否成功
     */
    @Override
    public boolean write(T device, byte[] data, BleWriteCallback<T> callback) {
        WriteRequest<T> request = Rproxy.getRequest(WriteRequest.class);
        return request.write(device, data, callback);
    }

    @Override
    public boolean write(T device, String data, BleWriteCallback<T> callback) {
        WriteRequest<T> request = Rproxy.getRequest(WriteRequest.class);
        return request.write(device, data, callback);
    }

    /**
     * 连接成功后，开始设置通知
     *
     * @param device   蓝牙设备对象
     * @param callback 通知回调
     * @deprecated Use {@link BleNotifyCallback (T, boolean, BleNotifyCallback)} instead.
     */
    @Override
    public void notify(T device, BleNotifyCallback<T> callback) {
        NotifyRequest<T> request = Rproxy.getRequest(NotifyRequest.class);
        request.notify(device, true, callback);
    }

    @Override
    public void disconnect(T device) {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        request.disconnect(device);
    }

}
