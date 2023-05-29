package com.ble.blescansdk.batch.entity;

import androidx.annotation.NonNull;

import com.ble.blescansdk.ble.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 信标配置实体
 */
public class BeaconConfig {

    /**
     * 帧类型
     */
    private String frameType;

    /**
     * 通道号
     */
    private Integer channelNumber;

    /**
     * 始终广播
     */
    private Boolean alwaysBroadcast;

    /**
     * 广播功率
     */
    private Double broadcastPower;

    private Integer calibrationDistance;

    /**
     * 广播间隔
     */
    private Integer broadcastInterval;

    /**
     * 触发器开关
     */
    private Boolean triggerSwitch;

    /**
     * 触发器时间
     */
    private Integer triggerBroadcastTime;

    /**
     * 触发器功率
     */
    private Double triggerBroadcastPower;

    /**
     * 触发器广播间隔
     */
    private Integer triggerBroadcastInterval;

    /**
     * 触发条件
     * 1 双击 2 三击 3 加速度
     */
    private Integer triggerCondition;

    private String deviceName;

    private String broadcastDataJson;

    public String getFrameType() {
        return frameType;
    }

    public void setFrameType(String frameType) {
        this.frameType = frameType;
    }

    public Integer getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(Integer channelNumber) {
        this.channelNumber = channelNumber;
    }

    public Boolean getAlwaysBroadcast() {
        if (null == alwaysBroadcast) {
            return true;
        }
        return alwaysBroadcast;
    }

    public void setAlwaysBroadcast(Boolean alwaysBroadcast) {
        this.alwaysBroadcast = alwaysBroadcast;
    }

    public Double getBroadcastPower() {
        return broadcastPower;
    }

    public void setBroadcastPower(Double broadcastPower) {
        this.broadcastPower = broadcastPower;
    }

    public Integer getCalibrationDistance() {
        return calibrationDistance;
    }

    public void setCalibrationDistance(Integer calibrationDistance) {
        this.calibrationDistance = calibrationDistance;
    }

    public Integer getBroadcastInterval() {
        return broadcastInterval;
    }

    public void setBroadcastInterval(Integer broadcastInterval) {
        this.broadcastInterval = broadcastInterval;
    }

    public Boolean getTriggerSwitch() {
        return triggerSwitch;
    }

    public void setTriggerSwitch(Boolean triggerSwitch) {
        this.triggerSwitch = triggerSwitch;
    }

    public Integer getTriggerBroadcastTime() {
        return triggerBroadcastTime;
    }

    public void setTriggerBroadcastTime(Integer triggerBroadcastTime) {
        this.triggerBroadcastTime = triggerBroadcastTime;
    }

    public Double getTriggerBroadcastPower() {
        return triggerBroadcastPower;
    }

    public void setTriggerBroadcastPower(Double triggerBroadcastPower) {
        this.triggerBroadcastPower = triggerBroadcastPower;
    }

    public Integer getTriggerBroadcastInterval() {
        return triggerBroadcastInterval;
    }

    public void setTriggerBroadcastInterval(Integer triggerBroadcastInterval) {
        this.triggerBroadcastInterval = triggerBroadcastInterval;
    }

    public Integer getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(Integer triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<String> getBroadcastDataList() {
        if (StringUtils.isBlank(broadcastDataJson)) {
            return new ArrayList<>();
        }
        try {
            return new Gson().fromJson(broadcastDataJson, new TypeToken<List<String>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            return new ArrayList<>();
        }
    }

    public void setBroadcastDataJson(String broadcastDataJson) {
        this.broadcastDataJson = broadcastDataJson;
    }

    @NonNull
    @Override
    public String toString() {
        return "BeaconConfig{" +
                "frameType='" + frameType + '\'' +
                ", channelNumber=" + channelNumber +
                ", alwaysBroadcast=" + alwaysBroadcast +
                ", broadcastPower=" + broadcastPower +
                ", broadcastInterval=" + broadcastInterval +
                ", triggerSwitch=" + triggerSwitch +
                ", triggerBroadcastTime=" + triggerBroadcastTime +
                ", triggerBroadcastPower=" + triggerBroadcastPower +
                ", triggerBroadcastInterval=" + triggerBroadcastInterval +
                ", triggerCondition=" + triggerCondition +
                '}';
    }
}
