package com.ble.blescansdk.batch.callback;

/**
 * 批量升级callback
 */
public interface BeaconConfigCallback {

    void success(String address);

    void fail(String address, String message);
}

