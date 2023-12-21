package com.ble.dfuupgrade.callback;


public interface IBleNotifyCallback {
    void onNotifySuccess();

    void onChanged(byte[] bytes);
}
