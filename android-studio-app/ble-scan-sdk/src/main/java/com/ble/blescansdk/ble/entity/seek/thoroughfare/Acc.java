package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public class Acc {
    // 通道类型
    private final String type;
    private double xAxis;
    private double yAxis;
    private double zAxis;

    public Acc(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public double getxAxis() {
        return xAxis;
    }

    public Acc setxAxis(double xAxis) {
        this.xAxis = xAxis;
        return this;
    }

    public double getyAxis() {
        return yAxis;
    }

    public Acc setyAxis(double yAxis) {
        this.yAxis = yAxis;
        return this;
    }

    public double getzAxis() {
        return zAxis;
    }

    public Acc setzAxis(double zAxis) {
        this.zAxis = zAxis;
        return this;
    }
}