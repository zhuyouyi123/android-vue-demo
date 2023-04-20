package com.ble.blescansdk.ble.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BluetoothReceiver extends BroadcastReceiver {

    public BluetoothReceiver() {

    }

    //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        //得到action
        String action = intent.getAction();
        //创建一个蓝牙device对象
        BluetoothDevice device = null;
        // 从Intent中获取设备对象
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (null == device) {
            return;
        }
        Log.i("onReceive", device.getAddress());
        if (null == device.getName()) {
            return;
        }

        if (!device.getAddress().equals("19:18:B2:00:20:BB")) {
            return;
        }

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {  //发现设备
            Log.e("发现设备:", "[" + device.getName() + "]" + ":" + device.getAddress());
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                try {
                    //通过工具类ClsUtils,调用createBond方法
                    BluetoothBondUtil.createBond(device.getClass(), device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else
            // 再次得到的action，会等于PAIRING_REQUEST
            if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                try {
                    Log.i("order...", "isOrderedBroadcast:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    BluetoothBondUtil.autoBond(device.getClass(), device, "1234");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
    }
}