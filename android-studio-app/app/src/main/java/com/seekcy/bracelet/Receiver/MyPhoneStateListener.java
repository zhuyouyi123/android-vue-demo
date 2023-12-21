package com.seekcy.bracelet.Receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, phoneNumber);

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                // 电话挂断
                Log.i("MyPhoneStateListener","监测到电话挂断");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                // 有来电
                Log.i("MyPhoneStateListener","监测到有来电");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // 通话中
                Log.i("MyPhoneStateListener","监测到通话中");
                break;
        }
    }
}