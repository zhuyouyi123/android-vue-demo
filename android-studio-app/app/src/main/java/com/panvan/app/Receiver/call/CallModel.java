package com.panvan.app.Receiver.call;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * @author xt on 2020/4/15 8:44
 */
public abstract class CallModel {
    private IntentFilter mFilter;
    private CallReceiver mCallReceiver;
    private final TelephonyManager mTelephonyManager;


    public CallModel(TelephonyManager telephonyManager) {
        this.mTelephonyManager = telephonyManager;

        new Handler(Looper.getMainLooper()).post(() -> {
            mFilter = new IntentFilter();
            mFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            mPhoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    super.onCallStateChanged(state, phoneNumber);

                    phoneNumber = phoneNumber.replace("-", "").replace(" ", "");        // 剔除号码中的分隔符
                    switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:
                            // 空闲/挂断处理逻辑
                            onStateIdle(state, phoneNumber);
                            break;

                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            // 接听处理逻辑
                            onStateOffhook(state, phoneNumber);
                            break;

                        case TelephonyManager.CALL_STATE_RINGING:
                            // 来电处理逻辑
                            onStateRinging(state, phoneNumber);
                            break;
                        default:
                            break;
                    }
                }
            };

            mCallReceiver = new CallReceiver();
            // 注册来电的电话状态监听服务
            telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        });
    }

    /**
     * 取消来电的电话状态监听服务
     */
    public void listenNone() {
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public IntentFilter getFilter() {
        return mFilter;
    }

    public CallReceiver getCallReceiver() {
        return mCallReceiver;
    }

    /**
     * 电话铃声响了
     */
    protected abstract void onStateRinging(int state, String phoneNumber);

    /**
     * 来电接通或者去电拨号 但是没法区分出
     */
    protected abstract void onStateOffhook(int state, String phoneNumber);

    /**
     * 空闲或挂断电话
     */
    protected abstract void onStateIdle(int state, String phoneNumber);

    private PhoneStateListener mPhoneStateListener = null;

    /**
     * 来电、去电广播监听
     */
    public class CallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.NEW_OUTGOING_CALL".equals(action)) {    // 接收到去电广播，执行去电处理逻辑
                /*IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
                context.registerReceiver(this, intentFilter);
                String phoneNumber = getResultData();*/
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                phoneNumber = phoneNumber.replace("-", "").replace(" ", "");        // 剔除号码中的分隔符
            } else if ("android.intent.action.PHONE_STATE".equals(action)) {    // 接收到来电广播，执行来电监听处理逻辑
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
    }
}
