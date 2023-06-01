package com.ble.blescansdk.ble.entity.seek.thoroughfare;

import java.util.List;

public class Info {
    private final String type;

    private List<Integer> triggerCondition;

    public Info(String type) {
        this.type = type;
    }

    public List<Integer> getTriggerCondition() {
        return triggerCondition;
    }

    public String getType() {
        return type;
    }

    public Info setTriggerCondition(List<Integer> triggerCondition) {
        this.triggerCondition = triggerCondition;
        return this;
    }

    @Override
    public String toString() {
        return "Info{" +
                "mac='" + triggerCondition + '\'' +
                '}';
    }
}
