package com.ble.blescansdk.ble.entity.seek.config;

import com.ble.blescansdk.ble.enums.EddystoneUrlPrefixEnum;
import com.ble.blescansdk.ble.enums.EddystoneUrlSuffixEnum;
import com.ble.blescansdk.ble.enums.seekstandard.ThoroughfareTypeEnum;
import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.List;
import java.util.Objects;

public class ChannelConfig {
    /**
     * 通道号
     */
    private int channelNumber;

    /**
     * 协议类型
     */
    private String frameType;

    /**
     * 广播间隔
     */
    private Integer broadcastInterval = 100;

    /**
     * 校准距离
     */
    private Integer calibrationDistance = -41;

    /**
     * 广播功率
     */
    private Integer broadcastPower = 9;

    /**
     * 是否开启触发器
     */
    private Boolean triggerSwitch = false;

    /**
     * 触发条件
     */
    private Integer triggerCondition = 1;

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
    private Integer triggerBroadcastPower;

    /**
     * 始终广播
     */
    private boolean alwaysBroadcast = true;

    private List<String> broadcastData;

    private String deviceName;

    private boolean responseSwitch = false;

    public int getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public String getFrameType() {
        return frameType;
    }

    public void setFrameType(ThoroughfareTypeEnum frameType) {
        this.frameType = frameType.getType();
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
        if (null == calibrationDistance) {
            this.calibrationDistance = 205;
        } else {
            this.calibrationDistance = calibrationDistance & 0xFF;
        }
    }

    public Integer getBroadcastPower() {
        return broadcastPower;
    }

    public void setBroadcastPower(Integer broadcastPower) {
        this.broadcastPower = broadcastPower;
    }

    public Boolean getTriggerSwitch() {
        return triggerSwitch;
    }

    public void setTriggerSwitch(Boolean triggerSwitch) {
        this.triggerSwitch = triggerSwitch;
    }

    public Integer getTriggerCondition() {
        return triggerCondition;
    }

    public void setTriggerCondition(Integer triggerCondition) {
        this.triggerCondition = triggerCondition;
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

    public Integer getTriggerBroadcastPower() {
        return triggerBroadcastPower;
    }

    public void setTriggerBroadcastPower(Integer triggerBroadcastPower) {
        this.triggerBroadcastPower = triggerBroadcastPower;
    }

    public boolean isAlwaysBroadcast() {
        return alwaysBroadcast;
    }

    public void setAlwaysBroadcast(boolean alwaysBroadcast) {
        this.alwaysBroadcast = alwaysBroadcast;
    }

    public List<String> getBroadcastData() {
        return broadcastData;
    }

    public void setBroadcastData(List<String> broadcastData) {
        this.broadcastData = broadcastData;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isResponseSwitch() {
        return responseSwitch;
    }

    public void setResponseSwitch(boolean responseSwitch) {
        this.responseSwitch = responseSwitch;
    }

    public String getSendMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getChannelNumber()).append("_");
        stringBuilder.append(this.frameType);
        if (Objects.equals(this.frameType, ThoroughfareTypeEnum.EMPTY.getType())) {
            return stringBuilder.toString();
        }
        stringBuilder.append("_");
        stringBuilder.append(getBroadcastInterval()).append("_");
        stringBuilder.append(getCalibrationDistance()).append("_");
        stringBuilder.append(getBroadcastPower()).append("_");
        if (this.triggerSwitch) {
            stringBuilder.append(1).append("_");
            stringBuilder.append(getTriggerCondition()).append("_");
            stringBuilder.append(getTriggerBroadcastTime()).append("_");
            stringBuilder.append(this.alwaysBroadcast ? 1 : 0).append("_");
            stringBuilder.append(getTriggerBroadcastInterval()).append("_");
            stringBuilder.append(getTriggerBroadcastPower());
        } else {
            stringBuilder.append(0).append("_");
            stringBuilder.append(0).append("_");
            stringBuilder.append(0).append("_");
            stringBuilder.append(this.alwaysBroadcast ? 1 : 0).append("_");
            stringBuilder.append(0).append("_");
            stringBuilder.append(0);
        }

        if (CollectionUtils.isNotEmpty(this.getBroadcastData())) {
            if (Objects.equals(this.frameType, ThoroughfareTypeEnum.I_BEACON.getType()) && this.getBroadcastData().size() > 2) {
                stringBuilder.append("_");
                stringBuilder.append(this.getBroadcastData().get(0)).append("_");
                stringBuilder.append(this.getBroadcastData().get(1)).append("_");
                stringBuilder.append(this.getBroadcastData().get(2)).append("_");
                // 1开启response 0关闭
                if (this.responseSwitch) {
                    stringBuilder.append(1).append("_").append(this.deviceName);
                } else {
                    stringBuilder.append(0).append("_").append("000000000000");
                }
            } else if (Objects.equals(this.frameType, ThoroughfareTypeEnum.EDDYSTONE_UID.getType()) && this.getBroadcastData().size() > 1) {
                stringBuilder.append("_");
                stringBuilder.append(this.getBroadcastData().get(0)).append("_");
                stringBuilder.append(this.getBroadcastData().get(1));
            } else if (Objects.equals(this.frameType, ThoroughfareTypeEnum.EDDYSTONE_URL.getType()) && this.getBroadcastData().size() > 2) {
                stringBuilder.append("_");
                stringBuilder.append(EddystoneUrlPrefixEnum.getByKey(this.getBroadcastData().get(0))).append("_");
                stringBuilder.append(this.getBroadcastData().get(1).length()).append("_");
                stringBuilder.append(this.getBroadcastData().get(1)).append("_");
                stringBuilder.append(EddystoneUrlSuffixEnum.getByKey(this.getBroadcastData().get(2)));
            } else if (Objects.equals(this.frameType, ThoroughfareTypeEnum.INFO.getType())) {
                stringBuilder.append("_");
                stringBuilder.append(StringUtils.isBlank(this.deviceName) ? "000000000000" : this.deviceName);
            } else if (Objects.equals(this.frameType, ThoroughfareTypeEnum.LINE.getType()) && this.getBroadcastData().size() > 2) {
                stringBuilder.append("_");
                stringBuilder.append(this.getBroadcastData().get(0)).append("_");
                stringBuilder.append(this.getBroadcastData().get(1)).append("_");
                stringBuilder.append(this.getBroadcastData().get(2));
            } else if (Objects.equals(this.frameType, ThoroughfareTypeEnum.CORE_IOT_AOA.getType()) && this.getBroadcastData().size() > 0) {
                stringBuilder.append("_");
                if (CollectionUtils.isEmpty(this.getBroadcastData()) || StringUtils.isBlank(this.getBroadcastData().get(0))) {
                    stringBuilder.append("0");
                } else {
                    stringBuilder.append(this.getBroadcastData().get(0));
                }
            } else if (Objects.equals(this.frameType, ThoroughfareTypeEnum.QUUPPA_AOA.getType()) && this.getBroadcastData().size() > 0) {
                stringBuilder.append("_");
                // Quuppa Tag
                if (CollectionUtils.isEmpty(this.getBroadcastData()) || StringUtils.isBlank(this.getBroadcastData().get(0))) {
                    stringBuilder.append(SeekStandardDeviceHolder.getInstance().getAddress());
                } else {
                    stringBuilder.append(this.getBroadcastData().get(0));
                }
            } else {
                if (ThoroughfareTypeEnum.EDDYSTONE_TLM.getType().equals(this.frameType)) {
                    return stringBuilder.toString();
                }

                if (ThoroughfareTypeEnum.ACC.getType().equals(this.frameType)) {
                    return stringBuilder.toString();
                }
                stringBuilder.append("_");
                stringBuilder.append("000000000000");
            }
        }


        return stringBuilder.toString();
    }

}
