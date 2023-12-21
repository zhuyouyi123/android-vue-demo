package com.seekcy.bracelet.Receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.seekcy.bracelet.Config;

public class PhoneReceiver extends BroadcastReceiver {

    private static final String TAG = PhoneReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // 如果是拨打电话
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            String outNumber = this.getResultData();// 去电号码
            Log.i(TAG, "call OUT 1:" + phoneNumber);
            Log.i(TAG, "call OUT 2:" + outNumber);

        } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
            // 如果是来电
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            // 来电号码
            String mIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(TAG, "call IN 1:" + state);
            Log.i(TAG, "call IN 2:" + mIncomingNumber);

            switch (tManager.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "**********************监测到电话呼入!!!!*****");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "**********************监测到接听电话!!!!************");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "**********************监测到挂断电话!!!!*******************");
                    break;
            }
        }
    }
}
