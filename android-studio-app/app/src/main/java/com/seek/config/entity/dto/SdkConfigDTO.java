package com.seek.config.entity.dto;

public class SdkConfigDTO {

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

    private Integer continuousScanning;

    /**
     * 扫描存活时间 超过时间 设备会被删除
     */
    private Long deviceSurviveTime ;

    private Integer communicationEncryption;

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

    public Integer getContinuousScanning() {
        return continuousScanning;
    }

    public void setContinuousScanning(Integer continuousScanning) {
        this.continuousScanning = continuousScanning;
    }

    public Long getDeviceSurviveTime() {
        return deviceSurviveTime;
    }

    public void setDeviceSurviveTime(Long deviceSurviveTime) {
        this.deviceSurviveTime = deviceSurviveTime;
    }

    public Integer getCommunicationEncryption() {
        return communicationEncryption;
    }

    public void setCommunicationEncryption(Integer communicationEncryption) {
        this.communicationEncryption = communicationEncryption;
    }
}
