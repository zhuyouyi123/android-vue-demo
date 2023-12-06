package com.panvan.app.Receiver.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;


import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.utils.AsciiUtil;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.panvan.app.Config;
import com.panvan.app.callback.WriteCallback;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.utils.DataConvertUtil;
import com.panvan.app.utils.SdkUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NotificationMonitorService extends NotificationListenerService {

    private static final String TAG = "NotificationMonitor";
    // qq信息
    public static final String QQ = "com.tencent.mobileqq";
    // 微信信息
    public static final String WX = "com.tencent.mm";
    // 短信
    public static final String MMS = "com.android.mms";
    // 荣耀短信
    public static final String HONOR_MMS = "com.hihonor.mms";
    // 信息
    public static final String MESSAGES = "com.google.android.apps.messaging";
    // 来电 -
    public static final String IN_CALL = "com.android.incallui";


    private static final int MMS_CODE = 0x00;
    private static final int WX_CODE = 0x01;
    private static final int QQ_CODE = 0x02;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "开启服务：NotificationMonitorService");

//        startForeground(1,getNotification());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "通知所属包名：" + sbn.getPackageName());

        Notification notification = sbn.getNotification();
        if (notification == null) {
            return;
        }
        String title = "通知消息";
        String content = "";

        // 当 API > 18 时，使用 extras 获取通知的详细信息
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle extras = notification.extras;
            if (extras != null) {
                // 获取通知标题通知所属包名
                title = extras.getString(Notification.EXTRA_TITLE, "");
                Log.d(TAG, "通知的标题为：" + title);
                // 获取通知内容
                content = extras.getString(Notification.EXTRA_TEXT, "");
                Log.d(TAG, "通知的内容为：" + content);
            }
        } else {
            // 当 API = 18 时，利用反射获取内容字段
            List<String> textList = getText(notification);
            if (textList != null && textList.size() > 0) {
                for (String text : textList) {
                    Log.d(TAG, "通知的内容包含：" + text);
                    content = text;
                }
            }
        }

        byte[] two = ProtocolUtil.intToByteArrayTwo(content.getBytes().length);
        byte[] bytes = new byte[two.length];
        for (int i = 0; i < two.length; i++) {
            bytes[two.length - 1 - i] = two[i];
        }

        switch (sbn.getPackageName()) {
            case QQ:
                String hex = "680B" + ProtocolUtil.byteArrToHexStr(DataConvertUtil.mergeBytes(bytes, content.getBytes()));
                SdkUtil.writeCommand(ProtocolUtil.byteArrToHexStr(ProtocolUtil.addSumBytes(ProtocolUtil.hexStrToBytes(hex))) + "16");
                break;
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "监听到包：" + sbn.getPackageName() + " 的通知被移除");
        // Intent localIntent = new Intent(Constants.BROADCAST_NOTIFICATION_REMOVED_ACTION);
        // localIntent.putExtra("packageName",sbn.getPackageName());
        // LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "监听服务被销毁");
//        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    // private Notification getNotification(){
    //     Intent intent = new Intent(this, MainActivity.class);
    //     PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
    //     Notification notification = new Notification.Builder(this)
    //             .setContentTitle("Notification Monitor Service")
    //             .setContentText("请不要杀掉Monitor进程，服务正在运行...")
    //             .setWhen(System.currentTimeMillis())
    //             .setSmallIcon(R.mipmap.ic_launcher)
    //             .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
    //             .setContentIntent(pendingIntent)
    //             .build();
    //     return notification;
    // }

    @Override
    public void onListenerDisconnected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(new ComponentName(this, NotificationMonitorService.class));
        }
    }

    public List<String> getText(Notification notification) {
        if (null == notification) {
            return null;
        }
        RemoteViews views = notification.bigContentView;
        if (views == null) {
            views = notification.contentView;
        }
        if (views == null) {
            return null;
        }
        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        List<String> text = new ArrayList<>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);
            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) continue;
                // View ID
                parcel.readInt();
                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();
                    // Store the actual string
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                }
                parcel.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }


    /**
     * 切换通知监听器服务
     */
    public static void toggleNotificationListenerService() {
        PackageManager pm = Config.mainContext.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(Config.mainContext, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(Config.mainContext, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
