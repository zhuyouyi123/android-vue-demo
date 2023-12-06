package com.db.database.enums;

import static com.db.database.enums.ConfigurationGroupEnum.*;

import java.util.ArrayList;
import java.util.List;

public enum ConfigurationTypeEnum {

    /**
     * HOME_CARD
     */
    STEP(HOME_CARD, "STEP", 1),
    SLEEP(HOME_CARD, "SLEEP", 1),
    BLOOD_OXYGEN_SATURATION(HOME_CARD, "BLOOD_OXYGEN_SATURATION", 1),
    HEART_RATE(HOME_CARD, "HEART_RATE", 1),
    PRESSURE(HOME_CARD, "PRESSURE", 1),
    CALORIE(HOME_CARD, "CALORIE", 1),
    TEMPERATURE(HOME_CARD, "TEMPERATURE", 1),
    BLOOD_PRESSURE(HOME_CARD, "BLOOD_PRESSURE", 1),
    SPORT_RECORDS(HOME_CARD, "SPORT_RECORDS", 1),
    FATIGUE(HOME_CARD, "FATIGUE", 1),

    /**
     * TARGET
     */
    TARGET_STEP(TARGET, "STEP", 8000),
    TARGET_CALORIE(TARGET, "CALORIE", 300),

    ;

    private final ConfigurationGroupEnum group;
    private final String type;
    private final Integer defaultValue;

    ConfigurationTypeEnum(ConfigurationGroupEnum group, String type, Integer defaultValue) {
        this.group = group;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public ConfigurationGroupEnum getGroup() {
        return group;
    }

    public String getType() {
        return type;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public static List<ConfigurationTypeEnum> getByGroup(ConfigurationGroupEnum groupEnum) {
        List<ConfigurationTypeEnum> list = new ArrayList<>();
        for (ConfigurationTypeEnum value : values()) {
            if (value.getGroup() == groupEnum) {
                list.add(value);
            }
        }
        return list;
    }
}
