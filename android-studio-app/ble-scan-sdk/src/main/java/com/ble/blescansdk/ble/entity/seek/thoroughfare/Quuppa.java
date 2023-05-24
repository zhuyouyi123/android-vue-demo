package com.ble.blescansdk.ble.entity.seek.thoroughfare;

public class Quuppa {

    private final String type;

    private String tagId;

    public Quuppa(String type) {
        this.type = type;
    }

    public String getTagId() {
        return tagId;
    }

    public Quuppa setTagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

}
