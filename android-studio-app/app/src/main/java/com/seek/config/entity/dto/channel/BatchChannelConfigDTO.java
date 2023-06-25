package com.seek.config.entity.dto.channel;

import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.config.entity.BeaconConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class BatchChannelConfigDTO {

    private String addressJson;

    private String beaconListJson;

    private String secretKey;

    private String oldSecretKey;

    private Boolean retry;

    private Boolean clearHistory;

    private String type;
    private String extendedInfo;

    public List<BeaconConfig> getChannelInfo() {
        if (StringUtils.isBlank(beaconListJson)) {
            return new ArrayList<>();
        }

        try {
            return new Gson().fromJson(beaconListJson, new TypeToken<List<BeaconConfig>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            return new ArrayList<>();
        }

    }

    public List<String> getAddressList() {
        if (StringUtils.isBlank(addressJson)) {
            return new ArrayList<>();
        }

        try {
            return new Gson().fromJson(addressJson, new TypeToken<List<String>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            return new ArrayList<>();
        }
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getOldSecretKey() {
        return oldSecretKey;
    }

    public Boolean getRetry() {
        if (null == retry) {
            return false;
        }
        return retry;
    }

    public Boolean getClearHistory() {
        if (null == clearHistory) {
            return false;
        }
        return clearHistory;
    }

    public void setClearHistory(Boolean clearHistory) {
        this.clearHistory = clearHistory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtendedInfo() {
        return extendedInfo;
    }

    public void setExtendedInfo(String extendedInfo) {
        this.extendedInfo = extendedInfo;
    }
}
