package com.ble.blescansdk.ble.queue;


import androidx.annotation.NonNull;

import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.queue.reconnect.DefaultReConnectHandler;


public final class ConnectQueue extends Queue {

    private static volatile ConnectQueue sInstance;

    private ConnectQueue() {
    }

    @NonNull
    public static ConnectQueue getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (ConnectQueue.class) {
            if (sInstance == null) {
                sInstance = new ConnectQueue();
            }
        }
        return sInstance;
    }

    @Override
    public void execute(RequestTask requestTask) {
        BleDevice device = requestTask.getDevices()[0];
        boolean reconnect = DefaultReConnectHandler.provideReconnectHandler().reconnect(device);
    }

}
