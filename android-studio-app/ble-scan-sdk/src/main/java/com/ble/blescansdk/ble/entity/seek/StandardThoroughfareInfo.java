package com.ble.blescansdk.ble.entity.seek;

import androidx.annotation.NonNull;

public class StandardThoroughfareInfo {
    // 通道类型
    private final String type;

    private String uuid;

    private int major;

    private int minor;

    private int measurePower;

    private String namespaceId;

    private String instanceId;

    private String link;

    public StandardThoroughfareInfo(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }

    public StandardThoroughfareInfo setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public int getMajor() {
        return major;
    }

    public StandardThoroughfareInfo setMajor(int major) {
        this.major = major;
        return this;
    }

    public int getMinor() {
        return minor;
    }

    public StandardThoroughfareInfo setMinor(int minor) {
        this.minor = minor;
        return this;
    }

    public int getMeasurePower() {
        return measurePower;
    }

    public StandardThoroughfareInfo setMeasurePower(int measurePower) {
        this.measurePower = measurePower;
        return this;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public StandardThoroughfareInfo setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public StandardThoroughfareInfo setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public String getLink() {
        return link;
    }

    public StandardThoroughfareInfo setLink(String link) {
        this.link = link;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "StandardThoroughfareInfo{" +
                "type='" + type + '\'' +
                ", uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", measurePower=" + measurePower +
                '}';
    }
}
