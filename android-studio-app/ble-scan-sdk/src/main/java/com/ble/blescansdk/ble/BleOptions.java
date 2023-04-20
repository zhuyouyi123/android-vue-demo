package com.ble.blescansdk.ble;

import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.enums.ManufacturerEnum;
import com.ble.blescansdk.ble.enums.SortTypeEnum;

import java.util.UUID;

public class BleOptions<T extends BleDevice> {

    /**
     * 蓝牙扫描周期时长
     */
    private long scanPeriod = 10 * 1000L;

    /**
     * 连续扫描
     */
    private boolean continuousScanning = false;

    /**
     * 间歇性扫描
     */
    private boolean intermittentScanning = false;

    /**
     * 设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
     */
    private boolean ignoreRepeat = false;

    /**
     * 蓝牙连接超时时长
     */
    private long connectTimeout = 5 * 1000L;

    /**
     * 厂商id
     */
    private int manufacturer = ManufacturerEnum.SEEK_STANDARD.getCode();

    /**
     * 扫描存活时间 超过时间 设备会被删除
     */
    private long deviceSurviveTime = 10 * 1000;

    private int bleScanLevel = BleScanLevelEnum.SCAN_MODE_BALANCED.getLevel();

    /**
     * debug日志开关
     */
    private boolean logSwitch = false;

    private FilterInfo filterInfo;

    private static String logTag = "BleSdkManager";

    /**
     * 蓝牙连接重试次数
     */
    private int connectFailedRetryCount = 1;

    /**
     * 排序方式
     */
    private SortTypeEnum sortType = SortTypeEnum.RSSI_FALL;

    public UUID[] uuid_services_extra = new UUID[]{};
    public UUID uuid_service = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public UUID uuid_notify = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public UUID uuid_read = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public UUID uuid_write = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    public static class FilterInfo {
        private String address;

        private String name;

        private boolean normDevice = false;

        private Integer rssi;

        public String getAddress() {
            return address;
        }

        public FilterInfo setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getName() {
            return name;
        }

        public FilterInfo setName(String name) {
            this.name = name;
            return this;
        }

        public boolean isNormDevice() {
            return normDevice;
        }

        public FilterInfo setNormDevice(boolean normDevice) {
            this.normDevice = normDevice;
            return this;
        }

        public Integer getRssi() {
            return rssi;
        }

        public FilterInfo setRssi(int rssi) {
            this.rssi = rssi;
            return this;
        }
    }

    public long getScanPeriod() {
        return scanPeriod;
    }

    public BleOptions<T> setScanPeriod(long scanPeriod) {
        if (scanPeriod <= 0) {
            return this;
        }
        this.scanPeriod = scanPeriod;
        return this;
    }

    public boolean isContinuousScanning() {
        return continuousScanning;
    }

    public BleOptions<T> setContinuousScanning(boolean continuousScanning) {
        this.continuousScanning = continuousScanning;
        return this;
    }

    public boolean isIntermittentScanning() {
        return intermittentScanning;
    }

    public BleOptions<T> setIntermittentScanning(boolean intermittentScanning) {
        this.intermittentScanning = intermittentScanning;
        return this;
    }

    public boolean isIgnoreRepeat() {
        return ignoreRepeat;
    }

    public BleOptions<T> setIgnoreRepeat(boolean ignoreRepeat) {
        this.ignoreRepeat = ignoreRepeat;
        return this;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public BleOptions<T> setConnectTimeout(long connectTimeout) {
        if (connectTimeout <= 0) {
            return this;
        }
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getManufacturer() {
        return manufacturer;
    }

    public BleOptions<T> setManufacturer(ManufacturerEnum manufacturerEnum) {
        if (null == manufacturerEnum) {
            return this;
        }
        this.manufacturer = manufacturerEnum.getCode();
        return this;
    }

    public long getDeviceSurviveTime() {
        return deviceSurviveTime;
    }

    public BleOptions<T> setDeviceSurviveTime(long deviceSurviveTime) {
        if (deviceSurviveTime <= 0) {
            this.deviceSurviveTime = -1;
            return this;
        }
        this.deviceSurviveTime = deviceSurviveTime;
        return this;
    }

    public boolean isLogSwitch() {
        return logSwitch;
    }

    public BleOptions<T> setLogSwitch(boolean logSwitch) {
        this.logSwitch = logSwitch;
        return this;
    }

    public int getBleScanLevel() {
        return bleScanLevel;
    }

    public BleOptions<T> setBleScanLevel(BleScanLevelEnum levelEnum) {
        if (null == levelEnum) {
            return this;
        }
        this.bleScanLevel = levelEnum.getLevel();
        return this;
    }

    public static String getLogTag() {
        return logTag;
    }

    public static void setLogTag(String logTag) {
        BleOptions.logTag = logTag;
    }

    public int getConnectFailedRetryCount() {
        return connectFailedRetryCount;
    }

    public BleOptions<T> setConnectFailedRetryCount(int connectFailedRetryCount) {
        if (connectFailedRetryCount < 0) {
            return this;
        }
        this.connectFailedRetryCount = connectFailedRetryCount;
        return this;
    }


    public FilterInfo getFilterInfo() {
        return filterInfo;
    }

    public BleOptions<T> setFilterInfo(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
        return this;
    }

    public SortTypeEnum getSortType() {
        return sortType;
    }

    public BleOptions<T> setSortType(SortTypeEnum sortType) {
        if (null == sortType) {
            return this;
        }
        this.sortType = sortType;
        return this;
    }
}
