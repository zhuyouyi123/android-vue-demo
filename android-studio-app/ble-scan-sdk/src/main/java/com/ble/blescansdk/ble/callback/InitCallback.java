package com.ble.blescansdk.ble.callback;

public interface InitCallback {
    void success();

    void failed(int failedCode);
}
