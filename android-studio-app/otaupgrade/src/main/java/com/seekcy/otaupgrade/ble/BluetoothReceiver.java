package com.seekcy.otaupgrade.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.seekcy.otaupgrade.util.Strings;


public class BluetoothReceiver extends BroadcastReceiver {

    public BluetoothReceiver() {

    }

    //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction(); //得到action
        Log.e("action1=", action);
        BluetoothDevice device;  //创建一个蓝牙device对象
        // 从Intent中获取设备对象
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (null == device) {
            return;
        }
        Log.i("onReceive", device.getAddress());

        if (Strings.isEmpty(device.getName())) {
            return;
        }
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {  //发现设备
            Log.e("发现设备:", "[" + device.getName() + "]" + ":" + device.getAddress());
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                Log.e("ywq", "attemp to bond:" + "[" + device.getName() + "]");
                try {
                    //通过工具类ClsUtils,调用createBond方法
                    //如果没有将广播终止，则会出现一个一闪而过的配对框。
//                    abortBroadcast();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

            }
        } else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) //再次得到的action，会等于PAIRING_REQUEST
        {
            Log.e("action2=", action);
            try {
                //1.确认配对
//                ClsUtil.setPairingConfirmation(device.getClass(), device, true);
                //2.终止有序广播
                Log.i("order...", "isOrderedBroadcast:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调用setPin方法进行配对...
//                boolean ret = ClsUtil.setPin(device.getClass(), device);
//                Log.i("配对结果", ret + "");
//                BluetoothAdapter bluetoothAdapter = ClsUtil.getBluetoothAdapter();
//                if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
//                    bluetoothAdapter.cancelDiscovery();
//                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}