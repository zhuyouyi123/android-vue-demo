package com.db.database.enums;

public enum ConfigurationGroupEnum {
    /**
     * 首页卡片
     */
    HOME_CARD("HOME_CARD"),
    /**
     * 目标
     */
    TARGET("TARGET"),
    ;

    private final String name;

    ConfigurationGroupEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ConfigurationGroupEnum getByGroup(String group) {
        for (ConfigurationGroupEnum value : values()) {
            if (value.getName().equals(group)) {
                return value;
            }
        }

        return HOME_CARD;
    }
}
