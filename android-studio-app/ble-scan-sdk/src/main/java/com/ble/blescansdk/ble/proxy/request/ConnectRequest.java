package com.ble.blescansdk.ble.proxy.request;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.annotation.Implement;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.IConnectWrapperCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.proxy.BleConnectsDispatcher;
import com.ble.blescansdk.ble.queue.reconnect.DefaultReConnectHandler;
import com.ble.blescansdk.ble.queue.retry.RetryDispatcher;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.BluetoothReceiver;
import com.ble.blescansdk.ble.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Implement(ConnectRequest.class)
public class ConnectRequest<T extends BleDevice> implements IConnectWrapperCallback<T> {

    public ConnectRequest() {
        DefaultReConnectHandler<T> connectHandler = DefaultReConnectHandler.provideReconnectHandler();
        addConnectHandlerCallbacks(connectHandler);
        RetryDispatcher<T> retryDispatcher = RetryDispatcher.getInstance();
        addConnectHandlerCallbacks(retryDispatcher);
    }

    private BleConnectCallback<T> connectCallback;
    private final Map<String, T> devices = new HashMap<>();
    private final BleRequestImpl<T> bleRequest = BleRequestImpl.getInstance();
    private final Map<String, T> connectedDevices = new HashMap<>();
    private final List<BleConnectCallback<T>> connectInnerCallbacks = new ArrayList<>();

    private final BleConnectsDispatcher<T> dispatcher = new BleConnectsDispatcher<>();

    public boolean connect(T device) {
        return connect(device, connectCallback);
    }

    public synchronized boolean connect(T device, BleConnectCallback<T> callback) {
        connectCallback = callback;
        if (device == null) {
            doConnectException(null, ErrorStatusEnum.DEVICE_NOT_EXISTS.getErrorCode());
            return false;
        }

        // TODO 连接状态
        if (device.getConnectState() == BleConnectStatusEnum.CONNECTING.getStatus()) {
            doConnectException(device, ErrorStatusEnum.BLUETOOTH_ALREADY_CONNECTED.getErrorCode());
            return false;
        }

        if (!BleSdkManager.getBluetoothAdapter().isEnabled()) {
            doConnectException(device, ErrorStatusEnum.BLUETOOTH_NOT_OPEN.getErrorCode());
            return false;
        }

        addBleToPool(device);

        return bleRequest.connect(device);
    }

    /**
     * 取消正在连接的设备
     *
     * @param device
     */
    public void cancelConnecting(T device) {
        boolean connecting = device.getConnectState() == BleConnectStatusEnum.CONNECTING.getStatus();
        boolean ready_connect = dispatcher.isContains(device);
        if (connecting || ready_connect) {
            if (connectCallback != null) {
                connectCallback.onConnectCancel(device);
            }
            if (connecting) {
                disconnect(device);
                bleRequest.cancelTimeout(device.getAddress());
                device.setConnectState(BleConnectStatusEnum.DISCONNECT.getStatus());
                onChange(device);
            }
            if (ready_connect) {
                dispatcher.cancelOne(device);
            }
        }
    }

    private void addBleToPool(T device) {
        if (devices.containsKey(device.getAddress())) {
            return;
        }
        devices.put(device.getAddress(), device);
    }

    public T getBleDevice(String address) {
        if (TextUtils.isEmpty(address)) {
            return null;
        }
        return devices.get(address);
    }

    /**
     * 无回调的断开
     *
     * @param device 设备对象
     */
    public void disconnect(BleDevice device) {
        disconnect(device, connectCallback);
    }

    /**
     * 带回调的断开
     *
     * @param device 设备对象
     */
    public void disconnect(BleDevice device, BleConnectCallback<T> callback) {
        if (device != null) {
            connectCallback = callback;
            bleRequest.disconnect(device.getAddress());
        }
    }

    /**
     * @return 已经连接的蓝牙设备集合
     */
    public ArrayList<T> getConnectedDevices() {
        return new ArrayList<>(connectedDevices.values());
    }

    void addConnectHandlerCallbacks(BleConnectCallback<T> callback) {
        connectInnerCallbacks.add(callback);
    }

    private void doConnectException(final T device, final int errorCode) {
        runOnUiThread(() -> {
            if (connectCallback != null) {
                connectCallback.onConnectFailed(device, errorCode);
            }
        });
        for (BleConnectCallback<T> callback : connectInnerCallbacks) {
            callback.onConnectFailed(device, errorCode);
        }
    }

    private void runOnUiThread(Runnable runnable) {
        ThreadUtils.ui(runnable);
    }

    public T getConnectedDevice(String address) {
        if (connectedDevices.isEmpty()) {
            return null;
        }

        return connectedDevices.get(address);
    }

    @Override
    public void onChange(T device) {
        if (device == null) return;
        int status = device.getConnectState();
        if (status == BleConnectStatusEnum.CONNECTING.getStatus()) {
            connectedDevices.put(device.getAddress(), device);
            devices.put(device.getAddress(), device);
        } else if (device.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            device.setConnectState(BleConnectStatusEnum.CONNECTED.getStatus());
            connectedDevices.put(device.getAddress(), device);
            devices.put(device.getAddress(), device);
            status = BleConnectStatusEnum.CONNECTED.getStatus();
            BleLogUtil.d("onChange connect success");
        } else if (device.getConnectState() == BleConnectStatusEnum.DISCONNECT.getStatus()) {
            connectedDevices.remove(device.getAddress());
            devices.remove(device.getAddress());
            BleLogUtil.d("onChange connect error");
        }

        int finalStatus = status;
        runOnUiThread(() -> {
            if (connectCallback != null) {
                connectCallback.onConnectChange(device, finalStatus);
            }
        });
    }

    @Override
    public void onFailed(T device, int errorCode) {
        if (device == null) return;
        device.setConnectState(BleConnectStatusEnum.DISCONNECT.getStatus());
        onChange(device);
        doConnectException(device, errorCode);
    }

    @Override
    public void onSuccess(T device) {
        if (device == null) return;


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connectCallback != null) {
                    connectCallback.onReady(device);
                }
            }
        });

    }

    @Override
    public void onServicesDiscovered(T device, BluetoothGatt gatt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connectCallback != null) {
                    connectCallback.onServicesDiscovered(device, gatt);
                }
            }
        });
    }
}
