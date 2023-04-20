package com.ble.blescansdk.ble.entity.seek;

import android.bluetooth.BluetoothDevice;

import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.entity.constants.SeekDeviceConstants;
import com.ble.blescansdk.ble.enums.SKYBeaconPowerEnum;

public class SeekBleDevice extends BleDevice {
    public SeekBleDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        super(device, rssi, scanRecord);
    }

    public SeekBleDevice() {

    }

    // 是否打开防篡改模式，0表示关闭，1表示开启
    private int isLocked = SeekDeviceConstants.DEFAULT_SKY_BEACON_IS_LOCKED_FALSE;
    // 是否开启防蹭用模式，0表示关闭，1表示开启
    private int isEncrypted = SeekDeviceConstants.DEFAULT_SKY_BEACON_IS_ENCRYPTED_FALSE;
    // uuid
    private String uuid = SeekDeviceConstants.DEFAULT_SKY_BEACON_PROXIMITY_UUID;
    // major
    private int major = SeekDeviceConstants.DEFAULT_SKY_BEACON_MAJOR_FALSE;
    // minor
    private int minor = SeekDeviceConstants.DEFAULT_SKY_BEACON_MINOR_FALSE;
    // 发送间隔
    private int sendInterval = SeekDeviceConstants.DEFAULT_SKY_BEACON_INTERVAL_MILLISECOND_FALSE;
    // 发送功率
    private SKYBeaconPowerEnum txPower = SKYBeaconPowerEnum.POWER_LEVEL_FALSE;
    // 是否是寻息信标
    private int seekBeacon = SeekDeviceConstants.DEFAULT_SKY_BEACON_IS_SEEKCY_IBEACON;
    // 硬件版本号
    private int hardwareVersion = SeekDeviceConstants.DEFAULT_SKY_BEACON_HARDWARE_VERSION;
    // 主软件版本号
    private int firmwareVersionMajor = SeekDeviceConstants.DEFAULT_SKY_BEACON_FIRMWARE_VERSION_MAJOR;
    // 次软件版本号
    private int firmwareVersionMinor = SeekDeviceConstants.DEFAULT_SKY_BEACON_FIRMWARE_VERSION_MINOR;

    private String deviceModel = SeekDeviceConstants.DEFAULT_SKY_BEACON_MODEL;
    // 光感值Lex
    private int lightValue = SeekDeviceConstants.DEFAULT_SKY_BEACON_LIGHT_SENSOR_VALUE;

    // 电量：％
    private int battery = SeekDeviceConstants.DEFAULT_SKY_BEACON_BATTERY_FALSE;

    // 测量功率
    private int measuredPower = SeekDeviceConstants.DEFAULT_SKY_BEACON_MEASURED_POWER_FALSE;

    private double distance = SeekDeviceConstants.DEFAULT_SKY_BEACON_DISTANCE_FALSE; // 距离

    public int getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public int getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(int isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
    }

    public SKYBeaconPowerEnum getTxPower() {
        return txPower;
    }

    public void setTxPower(SKYBeaconPowerEnum txPower) {
        this.txPower = txPower;
    }

    public int getSeekBeacon() {
        return seekBeacon;
    }

    public boolean isSeekBeacon() {
        return seekBeacon == 1;
    }

    public void setSeekBeacon(int seekBeacon) {
        this.seekBeacon = seekBeacon;
    }

    public int getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(int hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public int getFirmwareVersionMajor() {
        return firmwareVersionMajor;
    }

    public void setFirmwareVersionMajor(int firmwareVersionMajor) {
        this.firmwareVersionMajor = firmwareVersionMajor;
    }

    public int getFirmwareVersionMinor() {
        return firmwareVersionMinor;
    }

    public void setFirmwareVersionMinor(int firmwareVersionMinor) {
        this.firmwareVersionMinor = firmwareVersionMinor;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getLightValue() {
        return lightValue;
    }

    public void setLightValue(int lightValue) {
        this.lightValue = lightValue;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getMeasuredPower() {
        return measuredPower;
    }

    public void setMeasuredPower(int measuredPower) {
        this.measuredPower = measuredPower;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return super.toString() + "SeekBleDevice{" +
                "isLocked=" + isLocked +
                ", isEncrypted=" + isEncrypted +
                ", uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", sendInterval=" + sendInterval +
                ", txPower=" + txPower +
                ", seekBeacon=" + seekBeacon +
                ", hardwareVersion=" + hardwareVersion +
                ", firmwareVersionMajor=" + firmwareVersionMajor +
                ", firmwareVersionMinor=" + firmwareVersionMinor +
                ", deviceModel='" + deviceModel + '\'' +
                ", lightValue=" + lightValue +
                ", battery=" + battery +
                ", measuredPower=" + measuredPower +
                ", distance=" + distance +
                '}';
    }
}
