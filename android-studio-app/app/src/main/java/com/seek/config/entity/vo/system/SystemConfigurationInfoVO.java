package com.seek.config.entity.vo.system;

public class SystemConfigurationInfoVO {

    private String language = "简体中文";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        if ("中文".equals(language)) {
            language = "简体中文";
        } else {
            language = "English";
        }
        this.language = language;
    }
}
