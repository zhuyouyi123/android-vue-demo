package com.seek.config.entity.dto.channel;

import com.ble.blescansdk.ble.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class ChannelConfigDTO {
    /**
     * 通道号
     */
    private int channelNumber;

    /**
     * 帧类型
     */
    private String frameType;

    /**
     * 始终广播
     */
    private Boolean alwaysBroadcast;

    /**
     * 广播内容
     */
    private String broadcastData;

    /**
     * 广播间隔
     */
    private Integer broadcastInterval;

    /**
     * 校准距离
     */
    private Integer calibrationDistance;

    /**
     * 广播功率
     */
    private Double broadcastPower;

    /**
     * 触发器开关
     */
    private Boolean triggerSwitch;

    /**
     * 触发器广播时间
     */
    private Integer triggerBroadcastTime;

    /**
     * 广播间隔
     */
    private Integer triggerBroadcastInterval;

    /**
     * 广播功率
     */
    private Double triggerBroadcastPower;

    private Integer triggerCondition;

    private Boolean responseSwitch;

    private String deviceName;


    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public String getFrameType() {
        return frameType;
    }

    public void setFrameType(String frameType) {
        this.frameType = frameType;
    }

    public Boolean getAlwaysBroadcast() {
        return alwaysBroadcast;
    }

    public void setAlwaysBroadcast(Boolean alwaysBroadcast) {
        this.alwaysBroadcast = alwaysBroadcast;
    }

    public List<String> getBroadcastData() {
        if (StringUtils.isBlank(broadcastData)) {
            return Collections.emptyList();
        }

        List<String> list;
        try {
            list = new Gson().fromJson(broadcastData, new TypeToken<List<String>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            return Collections.emptyList();
        }
        return list;
    }

    public String getBroadcastDataString() {
        return broadcastData;
    }

    public void setBroadcastData(String broadcastData) {
        this.broadcastData = broadcastData;
    }

    public Integer getBroadcastInterval() {
        return broadcastInterval;
    }

    public void setBroadcastInterval(Integer broadcastInterval) {
        this.broadcastInterval = broadcastInterval;
    }

    public Integer getCalibrationDistance() {
        return calibrationDistance;
    }

    public void setCalibrationDistance(Integer calibrationDistance) {
        this.calibrationDistance = calibrationDistance;
    }

    public Double getBroadcastPower() {
        return broadcastPower;
    }

    public void setBroadcastPower(Double broadcastPower) {
        this.broadcastPower = broadcastPower;
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

    public Integer getTriggerBroadcastInterval() {
        return triggerBroadcastInterval;
    }

    public void setTriggerBroadcastInterval(Integer triggerBroadcastInterval) {
        this.triggerBroadcastInterval = triggerBroadcastInterval;
    }

    public Double getTriggerBroadcastPower() {
        return triggerBroadcastPower;
    }

    public void setTriggerBroadcastPower(Double triggerBroadcastPower) {
        this.triggerBroadcastPower = triggerBroadcastPower;
    }

    public Integer getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(Integer triggerCondition) {
        this.triggerCondition = triggerCondition;
    }

    public Boolean getResponseSwitch() {
        if (null == responseSwitch) {
            return false;
        }
        return responseSwitch;
    }

    public void setResponseSwitch(Boolean responseSwitch) {
        this.responseSwitch = responseSwitch;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void selfCheck() {
        if (null == this.alwaysBroadcast) {
            setAlwaysBroadcast(true);
        }

        if (null == this.triggerSwitch) {
            setTriggerSwitch(false);
        }
    }
}
