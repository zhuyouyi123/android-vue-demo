package com.ble.blescansdk.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.ble.blescansdk.batch.BeaconBatchConfigActuator;
import com.ble.blescansdk.batch.entity.BeaconConfig;
import com.ble.blescansdk.ble.callback.BluetoothChangedObserver;
import com.ble.blescansdk.ble.callback.request.BleConnectCallback;
import com.ble.blescansdk.ble.callback.request.BleNotifyCallback;
import com.ble.blescansdk.ble.callback.request.BleReadCallback;
import com.ble.blescansdk.ble.callback.request.BleScanCallback;
import com.ble.blescansdk.ble.callback.request.BleWriteCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.proxy.RequestImpl;
import com.ble.blescansdk.ble.proxy.RequestListener;
import com.ble.blescansdk.ble.proxy.RequestProxy;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.proxy.request.BleRequestImpl;
import com.ble.blescansdk.ble.proxy.request.ConnectRequest;
import com.ble.blescansdk.ble.proxy.request.ScanRequest;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.PermissionUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.db.SdkDatabase;
import com.ble.blescansdk.db.enums.BatchConfigTypeEnum;
import com.ble.blescansdk.db.helper.BatchConfigRecordHelper;
import com.google.gson.Gson;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class BleSdkManager<T extends BleDevice> {

    @SuppressLint("StaticFieldLeak")
    public static volatile BleSdkManager instance = new BleSdkManager<>();

    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    private static final char KEY = 's';

    private static boolean encryption = false;

    private RequestListener<T> request;

    private static BleOptions bleOptions;

    private static BluetoothAdapter bleAdapter = null;

    private final Object locker = new Object();

    // 监听蓝牙状态改变
    private BluetoothChangedObserver bleObserver;

    public static <T extends BleDevice> BleSdkManager<T> getInstance() {
        synchronized (BleSdkManager.class) {
            if (Objects.isNull(instance)) {
                return new BleSdkManager<>();
            }
        }
        return instance;
    }

    public void init(Context ctx) {
        context = ctx;
        // 初始化 bleOptions
        bleOptions = Objects.isNull(bleOptions) ? getBleOptions() : bleOptions;
        request = (RequestListener<T>) RequestProxy.newProxy().bindProxy(RequestImpl.newRequestImpl());

        BleRequestImpl<T> bleRequestImpl = BleRequestImpl.getInstance();
        bleRequestImpl.init(ctx);

        SdkDatabase.init();

        BleLogUtil.init();
    }

    public static Context getContext() {
        return context;
    }

    /**
     * start scanning
     */
    public void startScan(BleScanCallback callback) {
        if (!checkContext()) {
            callback.onScanFailed(ErrorStatusEnum.CONTEXT_NULL.getErrorCode());
            return;
        }

        if (!PermissionUtil.checkGpsStatus()) {
            callback.onScanFailed(ErrorStatusEnum.LOCATION_INFO_SWITCH_NOT_OPEN.getErrorCode());
            return;
        }

        initBleObserver();

        BleLogUtil.i("start scan");

        request.startScan(callback, getBleOptions().getScanPeriod());
    }

    /**
     * stop scanning
     */
    public void stopScan() {
        BleLogUtil.i("stop scan");
        if (null != bleObserver) {
            // 解除蓝牙监听
            bleObserver.unregisterReceiver();
        }
        request.stopScan();
    }

    /**
     * connect bluetooth
     *
     * @param device
     * @param callback
     */
    public void connect(T device, BleConnectCallback callback) {
        synchronized (locker) {
            request.connect(device, callback);
        }
    }

    public void cancelConnecting(T device) {
        synchronized (locker) {
            request.cancelConnecting(device);
        }
    }

    /**
     * 读取数据
     *
     * @param device   蓝牙设备对象
     * @param callback 读取结果回调
     */
    public boolean read(T device, BleReadCallback callback) {
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
    public boolean write(T device, byte[] data, BleWriteCallback callback) {
        return request.write(device, data, callback);
    }

    public void write(T device, String data, BleWriteCallback callback) {
        request.write(device, data, callback);
    }

    /**
     * 加密写入
     *
     * @param device   设备
     * @param data     数据
     * @param callback BleWriteCallback
     */
    public void writeEncryption(T device, String data, BleWriteCallback callback) {
        request.write(device, encryption(data), callback);
    }

    private String encryption(String data) {
        char[] chars = data.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= KEY;
        }
        return String.valueOf(chars);
    }

    /**
     * 连接成功后，开始设置通知
     *
     * @param device   蓝牙设备对象
     * @param callback 通知回调
     * @deprecated Use {@link BleNotifyCallback(T, boolean, BleNotifyCallback)} instead.
     */
    public void startNotify(T device, BleNotifyCallback callback) {
        request.notify(device, callback);
    }

    /**
     * disconnect
     *
     * @param device
     */
    public void disconnect(T device) {
        request.disconnect(device);
    }


    public void cancelCallback() {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        request.cancelConnectCallback();
    }

    /**
     * Release Empty all resources
     */
    public void releaseGatts() {
        BleLogUtil.d("BluetoothGatts is released");
        synchronized (locker) {
            Collection<T> connectedDevices = getConnectedDevices();
            if (CollectionUtils.isNotEmpty(connectedDevices)) {
                for (T bleDevice : connectedDevices) {
                    disconnect(bleDevice);
                }
            }
        }
    }

    /**
     * 批量配置信标
     *
     * @param addressList 地址列表
     * @param configs     配置信息列表
     */
    public boolean batchConfigChannel(List<String> addressList, List<BeaconConfig> configs, String secretKey) {
        if (BeaconBatchConfigActuator.getInstance().channelConfigInit(addressList, configs, secretKey)) {
            BatchConfigRecordHelper.deleteByType(BatchConfigTypeEnum.CHANNEL);
            BleLogUtil.i("批量配置通道：删除数据类型为通道的数据");
            SharePreferenceUtil.getInstance().shareSet(SharePreferenceUtil.BATCH_CONFIG_CHANNEL_LIST_KEY, new Gson().toJson(configs));
            BeaconBatchConfigActuator.getInstance().start();
            return true;
        }
        return false;
    }

    public boolean batchConfigSecretKey(List<String> addressList, String secretKey, String oldSecretKey) {
        if (BeaconBatchConfigActuator.getInstance().secretKeyConfigInit(addressList, oldSecretKey, secretKey)) {
            BatchConfigRecordHelper.deleteByType(BatchConfigTypeEnum.SECRET_KEY);
            BeaconBatchConfigActuator.getInstance().start();
            return true;
        }
        return false;
    }

    public boolean batchShutdown(List<String> addressList, String secretKey, boolean clearHistory) {
        if (BeaconBatchConfigActuator.getInstance().shutdownInit(addressList, secretKey)) {
            if (clearHistory) {
                BatchConfigRecordHelper.deleteByType(BatchConfigTypeEnum.SHUTDOWN);
            }
            BeaconBatchConfigActuator.getInstance().start();
            return true;
        }
        return false;
    }

    public List<BeaconBatchConfigActuator.ExecutorResult> getBatchConfigList() {
        return BeaconBatchConfigActuator.getInstance().getExecutorResultList();
    }

    /**
     * 初始化蓝牙状态监听器
     */
    private void initBleObserver() {
        if (Objects.isNull(bleObserver)) {
            bleObserver = new BluetoothChangedObserver();
        }
        bleObserver.registerReceiver();
        bleObserver.setBleStatusCallback(isOn -> {
            BleLogUtil.i("Bluetooth status is open " + isOn);
            if (!isOn && isScanning()) {
                BleSdkManager.getInstance().stopScan();
            }
        });
    }


    public static <T extends BleDevice> BleOptions<T> getBleOptions() {
        if (bleOptions == null) {
            bleOptions = new BleOptions<>();
        }
        return bleOptions;
    }

    public static <T extends BleDevice> void setBleOptions(BleOptions<T> options) {
        bleOptions = options;
    }

    private boolean checkContext() {
        return !Objects.isNull(context);
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        if (null == bleAdapter) {
            bleAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return bleAdapter;
    }

    /**
     * @return 已经连接的设备集合
     */

    public List<T> getConnectedDevices() {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        return request.getConnectedDevices();
    }

    /**
     * 是否正在扫描
     */
    public static boolean isScanning() {
        return ScanRequest.isScanning();
    }


    public static boolean isEncryption() {
        return encryption;
    }

    public static void setEncryption(boolean encryption) {
        BleSdkManager.encryption = encryption;
    }
}
