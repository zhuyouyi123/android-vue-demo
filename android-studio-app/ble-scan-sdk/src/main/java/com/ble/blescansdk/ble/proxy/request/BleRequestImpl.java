package com.ble.blescansdk.ble.proxy.request;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.core.os.HandlerCompat;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.callback.IConnectWrapperCallback;
import com.ble.blescansdk.ble.callback.INotifyWrapperCallback;
import com.ble.blescansdk.ble.callback.IReadWrapperCallback;
import com.ble.blescansdk.ble.callback.IWriteWrapperCallback;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleConnectStatusEnum;
import com.ble.blescansdk.ble.enums.ErrorStatusEnum;
import com.ble.blescansdk.ble.proxy.Rproxy;
import com.ble.blescansdk.ble.queue.retry.RetryDispatcher;
import com.ble.blescansdk.ble.scan.handle.BleHandler;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BleRequestImpl<T extends BleDevice> {

    private static final String TAG = BleRequestImpl.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static BleRequestImpl instance;
    private final Object locker = new Object();
    //The address of the connected device
    private final List<String> connectedAddressList = new ArrayList<>();
    protected final List<BluetoothGattCharacteristic> notifyCharacteristics = new ArrayList<>();
    //Multiple device connections must put the gatt object in the collection
    private final Map<String, BluetoothGatt> gattHashMap = new HashMap<>();


    private final Map<String, BluetoothGattCharacteristic> writeCharacteristicMap = new HashMap<>();
    private final Map<String, BluetoothGattCharacteristic> readCharacteristicMap = new HashMap<>();

    private final Handler handler = BleHandler.of();

    private IConnectWrapperCallback<T> connectWrapperCallback;
    protected INotifyWrapperCallback<T> notifyWrapperCallback;
    private IReadWrapperCallback<T> readWrapperCallback;
    private IWriteWrapperCallback<T> writeWrapperCallback;


    private Context context;
    private BleOptions<T> options;
    private BluetoothAdapter bluetoothAdapter;

    public static <T extends BleDevice> BleRequestImpl<T> getInstance() {
        if (instance == null) {
            instance = new BleRequestImpl<>();
        }
        return instance;
    }

    public void init(Context context) {
        this.connectWrapperCallback = Rproxy.getRequest(ConnectRequest.class);
        this.notifyWrapperCallback = Rproxy.getRequest(NotifyRequest.class);
        this.readWrapperCallback = Rproxy.getRequest(ReadRequest.class);
        this.writeWrapperCallback = Rproxy.getRequest(WriteRequest.class);
        this.context = context;
        bluetoothAdapter = BleSdkManager.getBluetoothAdapter();
        options = BleSdkManager.getBleOptions();

    }


    /**
     * 连接蓝牙
     *
     * @param bleDevice BleDevice
     * @return Connection result
     */
    public boolean connect(final T bleDevice) {
        String address = bleDevice.getAddress();
        if (connectedAddressList.contains(address)) {
            BleLogUtil.e(TAG + " this is device already connected.");
            if (!bleDevice.isConnected()) {
                connectedAddressList.remove(address);
                connectWrapperCallback.onFailed(bleDevice, ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode());
                return false;
            }
            connectWrapperCallback.onFailed(bleDevice, ErrorStatusEnum.BLUETOOTH_ALREADY_CONNECTED.getErrorCode());
            return false;
        }

        if (bluetoothAdapter == null) {
            BleLogUtil.e(TAG + " bluetoothAdapter not available.");
            connectWrapperCallback.onFailed(bleDevice, ErrorStatusEnum.BLUETOOTH_ADAPTER_NOT_AVAILABLE.getErrorCode());
            return false;
        }

        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            BleLogUtil.e(TAG + " the device address is invalid.");
            connectWrapperCallback.onFailed(bleDevice, ErrorStatusEnum.THE_DEVICE_ADDRESS_IS_INVALID.getErrorCode());
            return false;
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        if (device == null) {
            BleLogUtil.e(TAG + " no device.");
            connectWrapperCallback.onFailed(bleDevice, ErrorStatusEnum.DEVICE_NOT_EXISTS.getErrorCode());
            return false;
        }
        HandlerCompat.postDelayed(handler, () -> connectWrapperCallback.onFailed(bleDevice, ErrorStatusEnum.BLUETOOTH_CONNECT_TIMEOUT.getErrorCode()), device.getAddress(), options.getConnectTimeout());
        bleDevice.setConnectState(BleConnectStatusEnum.CONNECTING.getStatus());
        bleDevice.setName(device.getName());
        connectWrapperCallback.onChange(bleDevice);
        // 我们想直接连接到设备，所以我们将autoConnect参数设置为false
        BluetoothGatt bluetoothGatt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL) {
            bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothGatt = device.connectGatt(context, false, gattCallback);
        }

        if (bluetoothGatt != null) {
            gattHashMap.put(address, bluetoothGatt);
            return true;
        }
        return false;
    }

    /**
     * 清除蓝牙蓝牙连接设备的指定蓝牙地址
     *
     * @param address 蓝牙地址
     */
    public void close(String address) {
        BluetoothGatt gatt = getBluetoothGatt(address);
        if (gatt != null) {
            gatt.close();
            gattHashMap.remove(address);
        }
        connectedAddressList.remove(address);
    }


    public BluetoothGatt getBluetoothGatt(String address) {
        return gattHashMap.get(address);
    }

    public void cancelTimeout(String address) {
        handler.removeCallbacksAndMessages(address);
    }

    public T getBleDeviceInternal(String address) {
        ConnectRequest<T> request = Rproxy.getRequest(ConnectRequest.class);
        return request.getBleDevice(address);
    }

    /**
     * 断开蓝牙
     *
     * @param address 蓝牙地址
     */
    public void disconnect(String address) {
        BluetoothGatt gatt = getBluetoothGatt(address);
        if (gatt != null) {
            gatt.disconnect();
        }
        //notifyIndex = 0;
        notifyCharacteristics.clear();
        writeCharacteristicMap.remove(address);
        readCharacteristicMap.remove(address);
    }


    // ############################### read start################################

    /**
     * 读取数据
     *
     * @param address 蓝牙地址
     * @return 读取是否成功(这个是客户端的主观认为)
     */
    public boolean readCharacteristic(String address) {
        BluetoothGattCharacteristic gattCharacteristic = readCharacteristicMap.get(address);
        if (gattCharacteristic != null) {
            if (options.uuid_read.equals(gattCharacteristic.getUuid())) {
                boolean result = getBluetoothGatt(address).readCharacteristic(gattCharacteristic);
                if (result) {
                    readWrapperCallback.onReadSuccess(getBleDeviceInternal(address), gattCharacteristic);
                } else {
                    readWrapperCallback.onReadFailed(getBleDeviceInternal(address), ErrorStatusEnum.NOT_INIT_UUID.getErrorCode());
                }
                return result;
            }
        } else {
            if (null != readWrapperCallback) {
                readWrapperCallback.onReadFailed(getBleDeviceInternal(address), ErrorStatusEnum.NOT_INIT_UUID.getErrorCode());
            }
        }
        return false;
    }

    public boolean readCharacteristicByUuid(String address, UUID serviceUUID, UUID characteristicUUID) {
        BluetoothGatt bluetoothGatt = getBluetoothGatt(address);
        BluetoothGattCharacteristic gattCharacteristic = gattCharacteristic(bluetoothGatt, serviceUUID, characteristicUUID);
        if (gattCharacteristic != null) {
            boolean result = bluetoothGatt.readCharacteristic(gattCharacteristic);
            BleLogUtil.d(TAG, address + " -- read result:" + result);
            return result;
        }
        return false;
    }
    // ############################### read end##################################


    // ############################### write start################################

    /**
     * 写入数据
     *
     * @param address 蓝牙地址
     * @param value   发送的字节数组
     * @return 写入是否成功(这个是客户端的主观认为)
     */
    public boolean writeCharacteristic(String address, byte[] value) {
        BluetoothGattCharacteristic gattCharacteristic = writeCharacteristicMap.get(address);
        if (gattCharacteristic != null) {
            if (options.uuid_write.equals(gattCharacteristic.getUuid())) {
                gattCharacteristic.setValue(value);
                boolean result = getBluetoothGatt(address).writeCharacteristic(gattCharacteristic);
                BleLogUtil.d(TAG, address + " -- write result:" + result);
                return result;
            }
        } else {
            if (null != writeWrapperCallback) {
                writeWrapperCallback.onWriteFailed(getBleDeviceInternal(address), ErrorStatusEnum.NOT_INIT_UUID.getErrorCode());
            }
        }
        return false;
    }

    /**
     * 写入数据
     *
     * @param address 蓝牙地址
     * @param value   发送的字节数组
     * @return 写入是否成功(这个是客户端的主观认为)
     */
    public boolean writeCharacteristic(String address, String value) {
        if (StringUtils.isBlank(value)) {
            if (null != writeWrapperCallback) {
                writeWrapperCallback.onWriteFailed(getBleDeviceInternal(address), ErrorStatusEnum.NOT_INIT_UUID.getErrorCode());
            }
        }
        BluetoothGattCharacteristic gattCharacteristic = writeCharacteristicMap.get(address);
        if (gattCharacteristic != null) {
            if (options.uuid_write.equals(gattCharacteristic.getUuid())) {
                gattCharacteristic.setValue(value);
                boolean result = getBluetoothGatt(address).writeCharacteristic(gattCharacteristic);
                BleLogUtil.d(TAG, address + " -- write result:" + result);
                return result;
            }
        } else {
            if (null != writeWrapperCallback) {
                writeWrapperCallback.onWriteFailed(getBleDeviceInternal(address), ErrorStatusEnum.NOT_INIT_UUID.getErrorCode());
            }
        }
        return false;
    }

    // ############################### write end##################################

    /**
     * 启用或禁用给定特征的通知
     *
     * @param address 蓝牙地址
     * @param enabled 是否设置通知使能
     */
    public void setCharacteristicNotification(String address, boolean enabled) {
        if (notifyCharacteristics.size() > 0) {
            for (BluetoothGattCharacteristic characteristic : notifyCharacteristics) {
                setCharacteristicNotificationInternal(getBluetoothGatt(address), characteristic, enabled);
            }
        }
    }

    private void setCharacteristicNotificationInternal(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (characteristic == null) {
            BleLogUtil.e("characteristic is null");
            if (notifyWrapperCallback != null) {
                notifyWrapperCallback.onNotifyFailed(getBleDeviceInternal(gatt.getDevice().getAddress()), ErrorStatusEnum.NOT_EXIST_UUID.getErrorCode());
            }
            return;
        }
        gatt.setCharacteristicNotification(characteristic, enabled);
        //If the number of descriptors in the eigenvalue of the notification is greater than zero
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        if (!descriptors.isEmpty()) {
            //Filter descriptors based on the uuid of the descriptor
            for (BluetoothGattDescriptor descriptor : descriptors) {
                if (descriptor != null) {
                    //Write the description value
                    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                        //两个都是通知的意思，notify和indication的区别在于，notify只是将你要发的数据发送给手机，没有确认机制，
                        //不会保证数据发送是否到达。而indication的方式在手机收到数据时会主动回一个ack回来。即有确认机制，只有收
                        //到这个ack你才能继续发送下一个数据。这保证了数据的正确到达，也起到了流控的作用。所以在打开通知的时候，需要设置一下。
                        descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    }
                    gatt.writeDescriptor(descriptor);
                    BleLogUtil.d("setCharacteristicNotificationInternal is " + enabled);
                }
            }
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice device = gatt.getDevice();
            if (device == null) {
                return;
            }

            String address = device.getAddress();
            // remove timeout callback
            cancelTimeout(address);
            T bleDevice = getBleDeviceInternal(address);
            //There is a problem here Every time a new object is generated that causes the same device to be disconnected and the connection produces two objects
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    connectedAddressList.add(device.getAddress());
                    if (connectWrapperCallback != null && null != bleDevice) {
                        bleDevice.setConnectState(BleConnectStatusEnum.CONNECTED.getStatus());
                        connectWrapperCallback.onChange(bleDevice);
                    }
                    BluetoothGatt bluetoothGatt = getBluetoothGatt(device.getAddress());
                    if (null != bluetoothGatt) {
                        bluetoothGatt.discoverServices();
                    }
                    if (null != bluetoothGatt) {
                        // Attempts to discover services after successful connection.
                        bluetoothGatt.discoverServices();
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    if (connectWrapperCallback != null) {
                        bleDevice.setConnectState(BleConnectStatusEnum.DISCONNECT.getStatus());
                        connectWrapperCallback.onChange(bleDevice);
                    }
                    close(device.getAddress());
                }
            } else {
                //Occurrence 133 or 257 19 Equal value is not 0: Connection establishment failed due to protocol stack
                close(device.getAddress());
                if (connectWrapperCallback != null) {
                    connectWrapperCallback.onFailed(bleDevice, ErrorStatusEnum.BLUETOOTH_CONNECT_ERROR.getErrorCode());
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Empty the notification attribute list
                notifyCharacteristics.clear();
                //notifyIndex = 0;
                displayGattServices(gatt);
            } else {
                BleLogUtil.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        /**
         * 当连接成功的时候会回调这个方法，这个方法可以处理发送密码或者数据分析
         * 当setnotify（true）被设置时，如果MCU（设备端）上的数据改变，则该方法被回调。
         * @param gatt 蓝牙gatt对象
         * @param characteristic 蓝牙通知特征对象
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            synchronized (locker) {
                if (gatt == null || gatt.getDevice() == null) return;
                T bleDevice = getBleDeviceInternal(gatt.getDevice().getAddress());
                if (notifyWrapperCallback != null) {
                    notifyWrapperCallback.onChanged(bleDevice, characteristic);
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            if (gatt == null || gatt.getDevice() == null) return;
            UUID uuid = descriptor.getCharacteristic().getUuid();
            BleLogUtil.d(TAG, "write descriptor uuid:" + uuid);
            synchronized (locker) {
                T bleDevice = getBleDeviceInternal(gatt.getDevice().getAddress());
                if (status == BluetoothGatt.GATT_SUCCESS) {
//                    if (null != descWrapperCallback){
//                        descWrapperCallback.onDescWriteSuccess(bleDevice, descriptor);
//                    }

                    //fix bug
                    BleLogUtil.d(TAG, "set characteristic notification is completed");
                    if (notifyWrapperCallback != null) {
                        if (Arrays.equals(descriptor.getValue(), BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                                || Arrays.equals(descriptor.getValue(), BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
                            notifyWrapperCallback.onNotifySuccess(bleDevice);
                        } else if (Arrays.equals(descriptor.getValue(), BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)) {
                            notifyWrapperCallback.onNotifyCanceled(bleDevice);
                        }
                    }
                } else {
//                    if (null != descWrapperCallback){
//                        descWrapperCallback.onDescWriteFailed(bleDevice, status);
//                    }
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            if (gatt == null || gatt.getDevice() == null) return;
            UUID uuid = descriptor.getCharacteristic().getUuid();
            BleLogUtil.d(TAG, "read descriptor uuid:" + uuid);
            T bleDevice = getBleDeviceInternal(gatt.getDevice().getAddress());
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                if (null != descWrapperCallback){
//                    descWrapperCallback.onDescReadSuccess(bleDevice, descriptor);
//                }
//            }else {
//                if (null != descWrapperCallback){
//                    descWrapperCallback.onDescReadFailed(bleDevice, status);
//                }
//            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BleLogUtil.d(TAG, "read remoteRssi, rssi: " + rssi);
            if (gatt == null || gatt.getDevice() == null) return;
//            if (null != readRssiWrapperCallback){
//                readRssiWrapperCallback.onReadRssiSuccess(getBleDeviceInternal(gatt.getDevice().getAddress()), rssi);
//            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (gatt == null || gatt.getDevice() == null) return;
            BleLogUtil.d(TAG, "onCharacteristicRead:" + status);
            T bleDevice = getBleDeviceInternal(gatt.getDevice().getAddress());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (null != readWrapperCallback) {
                    readWrapperCallback.onReadSuccess(bleDevice, characteristic);
                }
            } else {
                if (null != readWrapperCallback) {
                    readWrapperCallback.onReadFailed(bleDevice, status);
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            if (gatt == null || gatt.getDevice() == null) return;
            BleLogUtil.d(TAG, gatt.getDevice().getAddress() + "-----write success----- status: " + status);
            synchronized (locker) {
                T bleDevice = getBleDeviceInternal(gatt.getDevice().getAddress());
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (null != writeWrapperCallback) {
                        writeWrapperCallback.onWriteSuccess(bleDevice, characteristic);
                    }
                } else {
                    if (null != writeWrapperCallback) {
                        writeWrapperCallback.onWriteFailed(bleDevice, status);
                    }
                }
            }
        }

    };

    private void displayGattServices(BluetoothGatt gatt) {
        //是否设置了service_uuid,且service_uuid是否存在
        boolean service_uuid_exist = false;
        BluetoothDevice device = gatt.getDevice();
        List<BluetoothGattService> gattServices = gatt.getServices();
        if (gattServices == null || device == null) {
            BleLogUtil.e(TAG, "displayGattServices gattServices or device is null");
            if (device != null) {
                close(device.getAddress());
            }
            return;
        }
        if (gattServices.isEmpty()) {
            BleLogUtil.e(TAG, "displayGattServices gattServices size is 0");
            disconnect(device.getAddress());
            return;
        }
        if (connectWrapperCallback != null) {
            T bleDevice = getBleDeviceInternal(device.getAddress());
            connectWrapperCallback.onServicesDiscovered(bleDevice, gatt);
        }
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            BleLogUtil.d(TAG, "discovered gattServices: " + uuid);
            if (uuid.equals(options.uuid_service.toString()) || isContainUUID(uuid)) {
                service_uuid_exist = true;
                BleLogUtil.i(TAG, "service_uuid is set up successfully:" + uuid);
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String char_uuid = gattCharacteristic.getUuid().toString();
                    BleLogUtil.d(TAG, "characteristic_uuid: " + char_uuid);
                    int charaProp = gattCharacteristic.getProperties();
                    StringBuilder properties_builder = new StringBuilder();
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
                        properties_builder.append("write,");
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
                        properties_builder.append("write_no_response,");
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                        properties_builder.append("read,");
                    }
                    //Optimize designated notifications

                    //Auto obtain Notification feature
                    if ((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        notifyCharacteristics.add(gattCharacteristic);
                        properties_builder.append("notify,");
                    }
                    if ((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                        notifyCharacteristics.add(gattCharacteristic);
                        properties_builder.append("indicate,");
                    }
                    int length = properties_builder.length();
                    if (length > 0) {
                        properties_builder.deleteCharAt(length - 1);
                        BleLogUtil.d(TAG, properties_builder.insert(0, "characteristic properties is ").toString());
                    }

                    if (char_uuid.equals(options.uuid_write.toString())) {
                        BleLogUtil.i(TAG, "write characteristic set up successfully:" + char_uuid);
                        writeCharacteristicMap.put(device.getAddress(), gattCharacteristic);
                        //Notification feature
                    }
                    if (char_uuid.equals(options.uuid_read.toString())) {
                        BleLogUtil.i(TAG, "read characteristic set up successfully:" + char_uuid);
                        readCharacteristicMap.put(device.getAddress(), gattCharacteristic);
                    }
                }
            }
        }
        if (!service_uuid_exist) {
            BleLogUtil.e(TAG, "init error, and uuid_service not the uuid of your device");
            BleLogUtil.e(TAG, "It is recommended to initialize in your application\n" +
                    "BleSdkManager.options()\n" +
                    ".setUuidService(替换成自己的service_uuid)必选\n" +
                    ".setUuidWriteCha(替换成自己的write_uuid)写入必选\n" +
                    ".setUuidReadCha(替换成自己的read_uuid)读取必选");
        }
        if (null != connectWrapperCallback) {
            connectWrapperCallback.onSuccess(getBleDeviceInternal(device.getAddress()));
        }

    }

    public BluetoothGattCharacteristic gattCharacteristic(BluetoothGatt gatt, UUID serviceUUID, UUID characteristicUUID) {
        if (gatt == null) {
            BleLogUtil.e(TAG, "BluetoothGatt is null");
            return null;
        }
        BluetoothGattService gattService = gatt.getService(serviceUUID);
        if (gattService == null) {
            BleLogUtil.e(TAG, "serviceUUID is null");
            return null;
        }
        BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(characteristicUUID);
        if (characteristic == null) {
            BleLogUtil.e(TAG, "characteristicUUID is null");
            return null;
        }
        return characteristic;
    }

    //是否包含该uuid
    private boolean isContainUUID(String uuid) {
        for (UUID u : options.uuid_services_extra) {
            if (u != null && uuid.equals(u.toString())) {
                return true;
            }
        }
        return false;
    }

    public void release() {
        connectWrapperCallback = null;
        notifyWrapperCallback = null;
        handler.removeCallbacksAndMessages(null);
        BleLogUtil.d(TAG + " is released");
    }

}
