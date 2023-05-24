package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public  class TLM {
    // 通道类型
    private final String type;
    private short voltage;
    /**
     * 温度
     */
    private String temperature;

    private long pduCount;

    private String workTime;

    public TLM(String type) {
        this.type = type;
    }


    public short getVoltage() {
        return voltage;
    }

    public TLM setVoltage(short voltage) {
        this.voltage = voltage;
        return this;
    }

    public String getTemperature() {
        return temperature;
    }

    public TLM setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public long getPduCount() {
        return pduCount;
    }

    public TLM setPduCount(long pduCount) {
        this.pduCount = pduCount;
        return this;
    }

    public String getWorkTime() {
        return workTime;
    }

    public TLM setWorkTime(String workTime) {
        this.workTime = workTime;
        return this;
    }
}