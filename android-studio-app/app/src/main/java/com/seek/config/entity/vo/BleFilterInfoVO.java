package com.seek.config.entity.vo;

public class BleFilterInfoVO {

    private String filterAddress;

    /**
     * 扫描等级
     */
    private Integer scanLevel;

    /**
     * 扫描时长
     */
    private Long scanPeriod;

    /**
     * 蓝牙扫描间歇时长
     */
    private Long intermittentTime;

    private Boolean continuousScanning;

    /**
     * 间歇性扫描
     */
    private Boolean intermittentScanning;

    /**
     * 扫描存活时间 超过时间 设备会被删除
     */
    private Long deviceSurviveTime ;

    /**
     * 扫描rssi
     */
    private Integer rssi;

    /**
     * 支持连接
     */
    private Boolean supportConnectable;

    /**
     * 标准设备
     */
    private boolean normDevice;

    private Integer communicationEncryption;

    public String getFilterAddress() {
        return filterAddress;
    }

    public void setFilterAddress(String filterAddress) {
        this.filterAddress = filterAddress;
    }

    public Integer getScanLevel() {
        return scanLevel;
    }

    public void setScanLevel(Integer scanLevel) {
        this.scanLevel = scanLevel;
    }

    public Long getScanPeriod() {
        return scanPeriod;
    }

    public void setScanPeriod(Long scanPeriod) {
        this.scanPeriod = scanPeriod;
    }

    public Long getIntermittentTime() {
        return intermittentTime;
    }

    public void setIntermittentTime(Long intermittentTime) {
        this.intermittentTime = intermittentTime;
    }

    public Boolean getContinuousScanning() {
        return continuousScanning;
    }

    public void setContinuousScanning(Boolean continuousScanning) {
        this.continuousScanning = continuousScanning;
    }

    public Boolean getIntermittentScanning() {
        return intermittentScanning;
    }

    public void setIntermittentScanning(Boolean intermittentScanning) {
        this.intermittentScanning = intermittentScanning;
    }

    public Long getDeviceSurviveTime() {
        return deviceSurviveTime;
    }

    public void setDeviceSurviveTime(Long deviceSurviveTime) {
        this.deviceSurviveTime = deviceSurviveTime;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Boolean getSupportConnectable() {
        return supportConnectable;
    }

    public void setSupportConnectable(Boolean supportConnectable) {
        this.supportConnectable = supportConnectable;
    }

    public boolean isNormDevice() {
        return normDevice;
    }

    public void setNormDevice(boolean normDevice) {
        this.normDevice = normDevice;
    }

    public Integer getCommunicationEncryption() {
        return communicationEncryption;
    }

    public void setCommunicationEncryption(Integer communicationEncryption) {
        this.communicationEncryption = communicationEncryption;
    }
}
