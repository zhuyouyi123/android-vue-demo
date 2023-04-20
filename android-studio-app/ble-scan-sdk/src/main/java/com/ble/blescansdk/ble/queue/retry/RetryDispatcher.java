package com.ble.blescansdk.ble.queue.retry;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.utils.BleLogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 蓝牙连接重试
 *
 * @param <T>
 */
public class RetryDispatcher<T extends BleDevice> extends BleConnectCallback<T> implements RetryCallback<T> {
    private static final String TAG = "RetryDispatcher";
    private static RetryDispatcher retryDispatcher;
    private final static Map<String, Integer> deviceRetryMap = new HashMap<>();

    public static <T extends BleDevice> RetryDispatcher<T> getInstance() {
        if (retryDispatcher == null) {
            retryDispatcher = new RetryDispatcher<>();
        }
        return retryDispatcher;
    }

    @Override
    public void retry(T device) {
        String address = device.getAddress();
        BleLogUtil.i(address + " ble reconnected " + deviceRetryMap.get(address) + " time");
        if (!device.isAutoConnect()) {
            ConnectRequest<T> connectRequest = Rproxy.getRequest(ConnectRequest.class);
            connectRequest.connect(device);
        }
    }

    @Override
    public void onConnectChange(BleDevice device, int status) {
        if (device.getConnectState() == BleConnectStatusEnum.CONNECTED.getStatus()) {
            String key = device.getAddress();
            deviceRetryMap.remove(key);
        }
    }

    @Override
    public void onConnectFailed(T device, int errorCode) {
        if (errorCode == ErrorStatusEnum.BLUETOOTH_CONNECT_TIMEOUT.getErrorCode() || errorCode == ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode()) {
            String key = device.getAddress();
            int connectFailedRetryCount = BleSdkManager.getBleOptions().getConnectFailedRetryCount();
            if (connectFailedRetryCount <= 0) return;
            Integer retryCount = connectFailedRetryCount;
            if (deviceRetryMap.containsKey(key)) {
                retryCount = deviceRetryMap.get(key);
            }

            if (retryCount == null || retryCount <= 0) {
                deviceRetryMap.remove(key);
                return;
            }
            deviceRetryMap.put(key, retryCount - 1);
            retry(device);
        }
    }

}
