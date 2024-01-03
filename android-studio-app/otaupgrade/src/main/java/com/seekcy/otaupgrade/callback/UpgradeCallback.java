package com.seekcy.otaupgrade.callback;

public interface UpgradeCallback {

    void success();

    void failed(String errorMsg);

    void connecting();

    void connected();

    void start();

    void progressChange(String v);
}
