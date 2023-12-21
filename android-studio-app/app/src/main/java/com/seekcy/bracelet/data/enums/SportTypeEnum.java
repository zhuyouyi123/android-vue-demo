package com.seekcy.bracelet.data.enums;

public enum SportTypeEnum {

    WALKING(0x01, "徒步"),
    RUNNING(0x02, "跑步"),
    HIKING(0x03, "爬山"),
    BALL_SPORTS(0x04, "球类运动"),
    STRENGTH_TRAINING(0x05, "力量训练"),
    AEROBIC_EXERCISE(0x06, "有氧运动"),

    // B10新增类型
    TREADMILL(0x07, "跑步机"),
    JUMP_ROPE(0x08, "跳绳"),
    INDOOR_CYCLING(0x09, "室内自行车"),
    MOUNTAIN_BIKING(0x0a, "山地自行车"),
    HIGH_INTENSITY_TRAINING(0x0b, "高强度训练"),
    FREE_TRAINING(0x0c, "自由训练"),
    SWIMMING(0x0d, "游泳"),
    YOGA(0x0e, "瑜伽");

    private final int code;
    private final String description;

    SportTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
