package com.ble.blescansdk.ble;

import android.content.Context;

import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.enums.ManufacturerEnum;
import com.ble.blescansdk.ble.enums.SortTypeEnum;
import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.UUID;

public class BleOptions<T extends BleDevice> {

    /**
     * 蓝牙扫描周期时长
     */
    private long scanPeriod = 10 * 1000L;

    /**
     * 蓝牙扫描间歇时长
     */
    private long intermittentTime = 1000L;

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
    private int connectFailedRetryCount = 0;

    private boolean databaseSupport = false;

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

        private int rssi = -100;

        private boolean supportConnectable = false;

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
            return this.normDevice;
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

        public boolean isSupportConnectable() {
            return supportConnectable;
        }

        public FilterInfo setSupportConnectable(boolean supportConnectable) {
            this.supportConnectable = supportConnectable;
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

    public long getIntermittentTime() {
        return intermittentTime;
    }

    public BleOptions<T> setIntermittentTime(long intermittentTime) {
        if (intermittentTime <= 0) {
            return this;
        }
        this.intermittentTime = intermittentTime;
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
        if (null == filterInfo) {
            filterInfo = new FilterInfo();
        }
        return filterInfo;
    }

    public BleOptions<T> setFilterInfo(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
        return this;
    }

    /**
     * 设置sdk是否使用数据库
     *
     * @return
     */
    public boolean isDatabaseSupport() {
        return databaseSupport;
    }

    public BleOptions<T> setDatabaseSupport(boolean databaseSupport) {
        this.databaseSupport = databaseSupport;
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

    public void saveCacheConfig() {
        SharePreferenceUtil.getInstance().shareSet(SharePreferenceUtil.SDK_CONFIG_INFO_CACHE_KEY, new Gson().toJson(this));
    }

    public BleOptions<T> getCacheConfig(Context context) {
        return queryCacheConfig(context);
    }

    public BleOptions<T> getCacheConfig() {
        return queryCacheConfig(BleSdkManager.getContext());
    }

    private BleOptions<T> queryCacheConfig(Context context) {
        String config = SharePreferenceUtil.getInstance(context).shareGet(SharePreferenceUtil.SDK_CONFIG_INFO_CACHE_KEY);
        if (StringUtils.isBlank(config)) {
            return null;
        }
        try {
            return new Gson().fromJson(config, new TypeToken<BleOptions<T>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
