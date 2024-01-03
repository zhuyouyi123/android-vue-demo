package com.panvan.app.Receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.panvan.app.AppActivity;
import com.seekcy.otaupgrade.util.Strings;

public class StartReceiver extends BroadcastReceiver {
    public StartReceiver() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("action1=", action);
        //此处及是重启的之后，打开我们app的方法
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            intent = new Intent(context, AppActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 非常重要，如果缺少的话，程序将在启动时报错
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //自启动APP（Activity）
            context.startActivity(intent);
            //自启动服务（Service）
            //context.startService(intent);
        } else {
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
                    // ClsUtil.setPairingConfirmation(device.getClass(), device, true);
                    //2.终止有序广播
                    Log.i("order...", "isOrderedBroadcast:" + isOrderedBroadcast() + ",isInitialStickyBroadcast:" + isInitialStickyBroadcast());
                    abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                    BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (defaultAdapter.isDiscovering()) {
                        defaultAdapter.cancelDiscovery();
                    }
                    //3.调用setPin方法进行配对...
//                boolean ret = ClsUtil.setPin(device.getClass(), device);
//                Log.i("配对结果", ret + "");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

}
