package com.ble.blescansdk.ble.proxy.request;

import android.bluetooth.BluetoothGatt;
import android.text.TextUtils;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.annotation.Implement;
import com.ble.blescansdk.ble.callback.IConnectWrapperCallback;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.proxy.BleConnectsDispatcher;
import com.ble.blescansdk.ble.queue.reconnect.DefaultReConnectHandler;
import com.ble.blescansdk.ble.queue.retry.RetryDispatcher;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.ThreadUtils;
import com.ble.blescansdk.db.SdkDatabase;
import com.ble.blescansdk.db.dataobject.SecretKeyDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Implement(ConnectRequest.class)
public class ConnectRequest<T extends BleDevice> implements IConnectWrapperCallback<T> {

    private static final String DEFAULT_SECRET_KEY = "loc666";

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
        return connect(device, connectCallback, true);
    }

    public synchronized boolean connect(T device, BleConnectCallback<T> callback, boolean isRetryConnect) {
        connectCallback = callback;
        if (device == null) {
            doConnectException(null, ErrorStatusEnum.DEVICE_NOT_EXISTS.getErrorCode());
            return false;
        }

        if (device.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            doConnectException(device, ErrorStatusEnum.BLUETOOTH_ALREADY_CONNECTED.getErrorCode());
            return false;
        }

        if (!BleSdkManager.getBluetoothAdapter().isEnabled()) {
            doConnectException(device, ErrorStatusEnum.BLUETOOTH_NOT_OPEN.getErrorCode());
            return false;
        }

        addBleToPool(device);

        if (!isRetryConnect) {
            RetryDispatcher.getInstance().connectInit(device);
            SecretKeyDO secretKeyDO = SdkDatabase.getInstance().getSecretKeyDAO().queryFirst(device.getAddress());
            if (null != secretKeyDO) {
                SeekStandardDeviceHolder.getInstance().setSecretKey(secretKeyDO.getSecretKey());
            } else {
                SeekStandardDeviceHolder.getInstance().setSecretKey(SharePreferenceUtil.getInstance().shareGet(SharePreferenceUtil.LAST_USE_SECRET_KEY));
            }
        }

        BleLogUtil.i("设备连接========");

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
            if (connectCallback != null && ErrorStatusEnum.BLUETOOTH_ALREADY_CONNECTED.getErrorCode() == errorCode) {
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
        String address = device.getAddress();
        int status = device.getConnectState();
        if (status == BleConnectStatusEnum.CONNECTING.getStatus()) {
            connectedDevices.put(address, device);
            devices.put(address, device);
        } else if (device.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            device.setConnectState(BleConnectStatusEnum.CONNECTED.getStatus());
            connectedDevices.put(address, device);
            devices.put(address, device);
            status = BleConnectStatusEnum.CONNECTED.getStatus();
            BleLogUtil.d("onChange connect success");
        } else if (device.getConnectState() == BleConnectStatusEnum.DISCONNECT.getStatus()) {
            connectedDevices.remove(address);
            devices.remove(address);
            BleLogUtil.d("onChange connect error");
        }

        int finalStatus = status;
        runOnUiThread(() -> {
            if (connectCallback != null) {
                SeekStandardDeviceHolder.getInstance().setConnectState(device.isConnected());
                SeekStandardDeviceHolder.getInstance().setConnectable(device.isConnectable());

                if (device.isConnected()) {
                    SeekStandardDeviceHolder.getInstance().setAddress(device.getAddress());
//                    RetryDispatcher.getInstance().onConnectSuccess(device);
                    connectCallback.onConnectSuccess(device);
                } else if (BleConnectStatusEnum.DISCONNECT.getStatus() == device.getConnectState()) {
//                    SeekStandardDeviceHolder.getInstance().setAddress("");
                    int retryCountByAddress = RetryDispatcher.getInstance().getRetryCountByAddress(device.getAddress());

                    BleLogUtil.d("BeaconConfigHelper", "当前重试次数" + retryCountByAddress);

                    if (retryCountByAddress == 0) {
                        connectCallback.onConnectFailed(device, ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode());
                        return;
                    }
                }
//                RetryDispatcher.getInstance().onConnectChange(device, finalStatus);
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
