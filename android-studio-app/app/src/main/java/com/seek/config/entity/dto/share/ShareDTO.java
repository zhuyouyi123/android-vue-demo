package com.seek.config.entity.dto.share;

public class ShareDTO {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ShareEditDTO{" +
                "key='" + key + '\'' +
                '}';
    }
}
