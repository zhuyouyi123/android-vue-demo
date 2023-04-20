package com.seek.config.entity.dto.share;

/**
 * 保存
 */
public class ShareSetDTO {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ShareSetDTO{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
