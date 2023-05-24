package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public class UID {
    // 通道类型
    private final String type;
    private String namespaceId;
    private String instanceId;
    private int measurePower;

    public UID(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getNamespaceId() {
        return namespaceId;
    }


    public UID setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public UID setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public int getMeasurePower() {
        return measurePower;
    }

    public UID setMeasurePower(int measurePower) {
        this.measurePower = measurePower;
        return this;
    }
}