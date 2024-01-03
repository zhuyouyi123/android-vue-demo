package com.seekcy.otaupgrade.enums;

public enum OtaUpgradeStatusEnum {
    NULL_UPGRADE(-1, "无状态"),
    WAITING_TO_UPGRADE(0, "等待升级"),
    UPGRADING(1, "升级中"),
    UPGRADE_SUCCEEDED(2, "升级成功"),
    UPGRADE_FAILED(3, "升级失败"),
    ;

    OtaUpgradeStatusEnum(int state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    private final int state;

    private final String desc;


    public int getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }
}
