package com.seekcy.bracelet.data.entity.vo;

import java.util.List;

/**
 * 查询当天最新信息
 * 最新血氧
 * 心率图标
 * 体温
 * 血压
 */
public class CurrDayLastInfoVO {

    private Integer bloodOxygen;

    private Double temperature;

    private Integer highPressure;
    private Integer lowPressure;

    private Integer heartRate;

    private List<Integer> heartRateList;

    public Integer getBloodOxygen() {
        return bloodOxygen;
    }

    public void setBloodOxygen(Integer bloodOxygen) {
        this.bloodOxygen = bloodOxygen;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getHighPressure() {
        return highPressure;
    }

    public void setHighPressure(Integer highPressure) {
        this.highPressure = highPressure;
    }

    public Integer getLowPressure() {
        return lowPressure;
    }

    public void setLowPressure(Integer lowPressure) {
        this.lowPressure = lowPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public List<Integer> getHeartRateList() {
        return heartRateList;
    }

    public void setHeartRateList(List<Integer> heartRateList) {
        this.heartRateList = heartRateList;
    }
}
