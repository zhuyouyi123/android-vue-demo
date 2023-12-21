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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public class MyBleManager extends BleManager {

    private String address;
    private ConCallback callback;
    private IBleNotifyCallback notifyCallback;
    private boolean needNotify = false;
    private static BluetoothGatt bluetoothGatt;
    private static BluetoothGattCharacteristic writeCharacteristic = null;
    private static final Handler handle = new Handler();
    private static Runnable runnable = null;

    @SuppressLint("StaticFieldLeak")
    private static MyBleManager INSTANCE = null;

    public static MyBleManager getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new MyBleManager(context);
        }
        return INSTANCE;
    }


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

    public void init(String address, ConCallback callback, IBleNotifyCallback notifyCallback, boolean needNotify) {
        this.address = address;
        this.callback = callback;
        this.notifyCallback = notifyCallback;
        this.needNotify = needNotify;
    }

    @SuppressLint("MissingPermission")
    public void dis() {
        disconnect();
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }
        writeCharacteristic = null;
        bluetoothGatt = null;
    }

    public void connectTask(int timeout) {
        runnable = () -> callback.timeout(getConnectionState() == BluetoothProfile.STATE_CONNECTED);
        handle.postDelayed(runnable, (long) timeout + 100);
    }

    public void connectToDevice() {
        if (Objects.nonNull(runnable)) {
            handle.removeCallbacks(runnable);
        }
        dis();
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        final BluetoothDevice device = adapter.getRemoteDevice(address);
        connect(device)
                .timeout(10000)
                .retry(3, 1000)
                .useAutoConnect(false)
                .fail((device1, status) -> callback.failed())
                .enqueue();

    }

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
                setNotificationCallback(notifyCharacteristic).with(new DataReceivedCallback() {
                    @Override
                    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                        notifyCallback.onChanged(data.getValue());
                    }
                });

                callback.success(gatt);
                return true;
            }

            @Override
            protected void onServicesInvalidated() {
                Log.e("Dfu", "onServicesInvalidated");
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
