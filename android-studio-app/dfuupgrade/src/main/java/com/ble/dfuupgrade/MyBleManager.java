package com.ble.dfuupgrade;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ble.dfuupgrade.callback.ConCallback;
import com.ble.dfuupgrade.callback.IBleNotifyCallback;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.ConnectRequest;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class MyBleManager extends BleManager {

    private static final String TAG = MyBleManager.class.getSimpleName();

    private String address;
    private static ConCallback callback;
    private IBleNotifyCallback notifyCallback;
    private boolean needNotify = false;
    private static BluetoothGatt bluetoothGatt;
    private static BluetoothGattCharacteristic writeCharacteristic = null;
    private static BluetoothLeScannerCompat scanner = null;

    /**
     * 当前是否已有连接
     */
    private static boolean alreadyHaveConnect = false;

    @SuppressLint("StaticFieldLeak")
    // private static MyBleManager INSTANCE = null;

    // public static MyBleManager getInstance(Context context) {
    //     if (null == INSTANCE) {
    //         INSTANCE = new MyBleManager(context);
    //     }
    //     return INSTANCE;
    // }


    /**
     * The manager constructor.
     * <p>
     * To connect a device, call {@link #connect(BluetoothDevice)}.
     *
     * @param context the context.
     */
    public MyBleManager(@NonNull Context context) {
        super(context);
    }

    public void init(String address, ConCallback conCallback, IBleNotifyCallback notifyCallback, boolean needNotify) {
        this.address = address;
        callback = conCallback;
        this.notifyCallback = notifyCallback;
        this.needNotify = needNotify;
    }

    public void connectToDevice() {
        if (alreadyHaveConnect) {
            return;
        }
        alreadyHaveConnect = true;
        Log.i(TAG, "connectToDevice");
        BluetoothDevice remoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        ConnectRequest connectRequest = connect(remoteDevice)
                .timeout(15000)
                .useAutoConnect(false)
                .fail((device1, status) -> callback.failed());
        connectRequest.enqueue();

    }

    @SuppressLint("MissingPermission")
    public void dis(DisCallback callback) {
        try {
           dis();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            callback.success();
        }
    }

    private void dis(){
        if (alreadyHaveConnect) {
            close();
            refreshDeviceCache();
        }
        writeCharacteristic = null;
        bluetoothGatt = null;
        alreadyHaveConnect = false;
        stopScan();
    }

    public static interface DisCallback {
        void success();
    }

    private final Handler handler = new Handler();

    public void startScan() {
        if (Objects.isNull(scanner)) {
            dis();
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // 设置扫描模式
                    .build();

            scanner = BluetoothLeScannerCompat.getScanner();
            scanner.startScan(null, settings, scanCallback);
            handler.postDelayed(() -> {
                stopScan();
                callback.end();
            }, 20000); // 设置扫描时间
        }
    }

    private void stopScan() {
        if (Objects.nonNull(scanner)) {
            BluetoothLeScannerCompat.getScanner().stopScan(scanCallback);
            scanner = null;
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            super.onScanResult(callbackType, result);
            // 处理扫描到的蓝牙设备
            BluetoothDevice device = result.getDevice();
            if (device.getAddress().equals(address)) {
                connectToDevice();
            }

        }
    };

    public UUID uuid_service = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public UUID uuid_notify = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public UUID uuid_write = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    @NonNull
    @Override
    public BleManagerGattCallback getGattCallback() {
        return new BleManagerGattCallback() {
            @SuppressLint("MissingPermission")
            @Override
            protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
                Log.e("Dfu", "isRequiredServiceSupported");
                stopScan();
                if (Objects.isNull(bluetoothGatt)) {
                    bluetoothGatt = gatt;
                }
                if (!needNotify) {
                    return true;
                }
                List<BluetoothGattService> services = gatt.getServices();
                BluetoothGattService gattService = null;
                for (BluetoothGattService service : services) {
                    String uuid = service.getUuid().toString();
                    if (uuid_service.toString().equals(uuid)) {
                        gattService = service;
                        break;
                    }
                }

                if (Objects.isNull(gattService)) {
                    callback.failed();
                    return false;
                }


                BluetoothGattCharacteristic notifyCharacteristic = gattService.getCharacteristic(uuid_notify);
                if (Objects.isNull(writeCharacteristic)) {
                    writeCharacteristic = gattService.getCharacteristic(uuid_write);
                }

                gatt.setCharacteristicNotification(notifyCharacteristic, true);

                List<BluetoothGattDescriptor> descriptors = notifyCharacteristic.getDescriptors();

                boolean writeDescriptor = true;
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    if (!gatt.writeDescriptor(descriptor)) {
                        writeDescriptor = false;
                    }
                }

                if (writeDescriptor) {
                    notifyCallback.onNotifySuccess();
                }

                setNotificationCallback(notifyCharacteristic).with((device, data) -> notifyCallback.onChanged(data.getValue()));

                callback.success(gatt);
                return true;
            }

            @Override
            protected void onServicesInvalidated() {
                Log.e("Dfu", "onServicesInvalidated");
                callback.failed();
            }
        };
    }


    @SuppressLint("MissingPermission")
    public void write(byte[] bytes) {
        if (getConnectionState() != BluetoothProfile.STATE_CONNECTED) {
            return;
        }
        if (Objects.nonNull(writeCharacteristic)) {
            writeCharacteristic.setValue(bytes);
            if (Objects.nonNull(bluetoothGatt)) {
                bluetoothGatt.writeCharacteristic(writeCharacteristic);
            }
        }
    }


}
