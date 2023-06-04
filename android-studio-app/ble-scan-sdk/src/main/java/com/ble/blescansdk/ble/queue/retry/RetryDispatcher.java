package com.ble.blescansdk.ble.queue.retry;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 蓝牙连接重试
 *
 * @param <T>
 */
public class RetryDispatcher<T extends BleDevice> extends BleConnectCallback<T> implements RetryCallback<T> {
    private static final String TAG = "RetryDispatcher";
    private static RetryDispatcher retryDispatcher;
    private final Map<String, Integer> deviceRetryMap = new HashMap<>();

    public static <T extends BleDevice> RetryDispatcher<T> getInstance() {
        if (retryDispatcher == null) {
            retryDispatcher = new RetryDispatcher<>();
        }
        return retryDispatcher;
    }

    public void connectInit(T device) {
        int connectFailedRetryCount = BleSdkManager.getBleOptions().getConnectFailedRetryCount();
        if (connectFailedRetryCount <= 0) return;
        Integer retryCount = connectFailedRetryCount;
        deviceRetryMap.put(device.getAddress(), retryCount);
        BleLogUtil.i("当前剩余重试次数——重置");
    }


    @Override
    public void retry(T device) {
        String address = device.getAddress();
        Integer integer = deviceRetryMap.get(address);
        BleLogUtil.i(address + "state:" + device.isConnectable() + " ble reconnected " + integer + " time");
        if (!device.isConnected()) {
            ConnectRequest<T> connectRequest = Rproxy.getRequest(ConnectRequest.class);
            connectRequest.connect(device);
        }
    }

    @Override
    public void onConnectChange(BleDevice device, int status) {
        if (device.isConnected()) {
            String key = device.getAddress();
            deviceRetryMap.remove(key);
            BleLogUtil.i("当前剩余重试次数,已连接清空");
        }
    }

    @Override
    public void onConnectSuccess(T device) {
        if (device.isConnected()) {
            String key = device.getAddress();
            deviceRetryMap.remove(key);
            BleLogUtil.i("当前剩余重试次数,已连接清空");
        }
    }

    @Override
    public void onConnectFailed(T device, int errorCode) {
        if (errorCode == ErrorStatusEnum.BLUETOOTH_CONNECT_TIMEOUT.getErrorCode() || errorCode == ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode()) {
            String key = device.getAddress();
            int connectFailedRetryCount = BleSdkManager.getBleOptions().getConnectFailedRetryCount();
            if (connectFailedRetryCount <= 0) return;
            if (!deviceRetryMap.containsKey(key)) {
                return;
            }
            Integer retryCount = deviceRetryMap.get(key);

            if (retryCount == null || retryCount <= 0) {
                deviceRetryMap.remove(key);
                return;
            }
            BleLogUtil.i("尝试重新连接" + retryCount);

            deviceRetryMap.put(key, retryCount - 1);


            retry(device);
        }
    }

    /**
     * 根据地址获取重试次数
     *
     * @param address 地址
     * @return 次数
     */
    public int getRetryCountByAddress(String address) {
        if (StringUtils.isBlank(address)) {
            return -1;
        }

        Integer integer = deviceRetryMap.get(address);
        if (null == integer) {
            return -1;
        }

        return integer;

    }

}
