package com.ble.blescansdk.ble.queue.reconnect;

import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.queue.ConnectQueue;
import com.ble.blescansdk.ble.queue.RequestTask;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * author: jerry
 * date: 20-11-30
 * email: superliu0911@gmail.com
 * des:
 */
public class DefaultReConnectHandler<T extends BleDevice> extends BleConnectCallback<T> {
    private static final String TAG = "DefaultReConnectHandler";
    public static final long DEFAULT_CONNECT_DELAY = 2000L;
    private static DefaultReConnectHandler defaultReConnectHandler;
    private final ArrayList<T> autoDevices = new ArrayList<>();

    private DefaultReConnectHandler() {
    }

    public static <T extends BleDevice> DefaultReConnectHandler<T> provideReconnectHandler() {
        if (defaultReConnectHandler == null) {
            defaultReConnectHandler = new DefaultReConnectHandler<>();
        }
        return defaultReConnectHandler;
    }

    public boolean reconnect(T device) {
        for (T autoDevice : autoDevices) {
            if (TextUtils.equals(autoDevice.getAddress(), device.getAddress())) {
                ConnectRequest<T> connectRequest = Rproxy.getRequest(ConnectRequest.class);
                return connectRequest.connect(device);
            }
        }
        return false;
    }

    /**
     * Add a disconnected device to the autopool
     *
     * @param device Device object
     */
    private void addAutoPool(T device) {
        if (device == null) return;
        if (device.isAutoConnect()) {
            if (!autoDevices.contains(device)) {
                autoDevices.add(device);
            }
            RequestTask requestTask = new RequestTask.Builder()
                    .devices(device)
                    .delay(DEFAULT_CONNECT_DELAY)
                    .build();
            ConnectQueue.getInstance().put(requestTask);
        }
    }

    /**
     * If it is automatically connected device is removed from the automatic connection pool
     *
     * @param device Device object
     */
    private void removeAutoPool(T device) {
        if (device == null) return;
        Iterator<T> iterator = autoDevices.iterator();
        while (iterator.hasNext()) {
            BleDevice item = iterator.next();
            if (device.getAddress().equals(item.getAddress())) {
                iterator.remove();
            }
        }
    }

    public void resetAutoConnect(T device, boolean autoConnect) {
        if (device == null) return;
        device.setAutoConnect(autoConnect);
        if (!autoConnect) {
            removeAutoPool(device);
            if (device.getConnectState() == BleConnectStatusEnum.CONNECTING.getStatus()) {
                ConnectRequest<T> connectRequest = Rproxy.getRequest(ConnectRequest.class);
                connectRequest.disconnect(device);
            }
        } else {//重连
            addAutoPool(device);
        }
    }

    //取消所有需要自动重连的设备
    public void cancelAutoConnect() {
        autoDevices.clear();
    }

    /**
     * 打开蓝牙后,重新连接异常断开时的设备
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public void openBluetooth() {
        for (T device : autoDevices) {
            addAutoPool(device);
        }
    }


    @Override
    public void onConnectChange(T device, int status) {
        if (device.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            /*After the success of the connection can be considered automatically reconnect.
            If it is automatically connected device is removed from the automatic connection pool*/
            removeAutoPool(device);
        } else if (device.getConnectState() == BleConnectStatusEnum.DISCONNECT.getStatus()) {
            addAutoPool(device);
        }
    }

    /**
     * When the callback when the error, such as app can only connect four devices
     * at the same time forcing the user to connect more than four devices will call back the method
     *
     * @param device    ble device object
     * @param errorCode errorCode
     */
    @Override
    public void onConnectFailed(T device, int errorCode) {

    }
}
