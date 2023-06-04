package com.seek.config.entity.dto.channel;

import com.ble.blescansdk.batch.entity.BeaconConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import dev.utils.common.StringUtils;

public class BatchChannelConfigDTO {

    private String addressJson;

    private String beaconListJson;

    private String secretKey;

    private String oldSecretKey;

    private Boolean retry;

    private Boolean clearHistory;

    public List<BeaconConfig> getChannelInfo() {
        if (StringUtils.isEmpty(beaconListJson)) {
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
        if (StringUtils.isEmpty(addressJson)) {
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
}
