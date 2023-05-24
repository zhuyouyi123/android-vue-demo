package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public class Info {
    private String type;

    private String mac;

    public Info(String type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public Info setMac(String mac) {
        this.mac = mac;
        return this;
    }

    @Override
    public String toString() {
        return "Info{" +
                "mac='" + mac + '\'' +
                '}';
    }
}
