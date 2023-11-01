package com.panvan.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.ble.blescansdk.ble.BleSdkManager;

public class AppInitTools extends AppCompatActivity {


    //获取到通知管理器
    public static NotificationManager mNotificationManager = null;
    public static NotificationCompat.Builder builder = null;
    private static int COUNT_ID = 10000;
    private static String CHANNEL_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentBefore();
        setContentView(R.layout.activity_app);

        BleSdkManager.getInstance().init(this);
    }

    /**
     * 获取channel_id
     */
    public static String createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFY_CHANNEL = "MY_CHANNEL_CALL_LEND_STOCK";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL, "Notifications", importance);
            channel.setDescription("This is a notification channel");

            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.y682);
            channel.setSound(uri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            return NOTIFY_CHANNEL;
        }
        return "";
    }

    /**
     * 发送notify 本地消息推送
     *
     * @param title
     * @param text
     * @return
     */
    public static boolean showNotify(int iconId, String title, String text, Context context) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            CHANNEL_ID = createNotificationChannel(context);
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }
        try {
            COUNT_ID++;
            builder
//              .setSound(uri)
                    .setSmallIcon(iconId)
                    .setContentTitle(title)
                    .setContentText(text) //收到通知时的效果，这里是默认声音
//                .setContentIntent(PendingIntent.getActivity(context, COUNT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
            ;

            mNotificationManager.notify(COUNT_ID, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean showNotify(String title, String text, Context context) {
        return showNotify(R.drawable.logo_48x48, title, text, context);
    }

    public int getStatusHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);

        }
        return result;
    }

    /**
     * 初始化
     */
    public void initContentBefore() {
        showTitle(Config.showTitle);
        showStatusBar(Config.showStatusBar);
    }


    /**
     * 初始化
     *
     * @param show
     */
    private void showStatusBar(boolean show) {
        if (!show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
    }

    private void showTitle(boolean show) {
        if (!show) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }


}
