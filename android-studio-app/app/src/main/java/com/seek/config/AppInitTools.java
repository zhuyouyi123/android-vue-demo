package com.seek.config;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.ble.blescansdk.ble.BleOptions;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.entity.BleDevice;
import com.ble.blescansdk.ble.enums.BleScanLevelEnum;
import com.ble.blescansdk.ble.enums.SortTypeEnum;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class AppInitTools extends AppCompatActivity {

    public WebView webView;

    // 获取到通知管理器
    public static NotificationManager mNotificationManager = null;
    @SuppressLint("StaticFieldLeak")
    public static NotificationCompat.Builder builder = null;
    private static int COUNT_ID = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentBefore();
        setContentView(R.layout.activity_app);
        webView = findViewById(R.id.mainWebView);
        webView.onResume();

        webView.setWebChromeClient(new WebChromeClient());

        initPermission();

        initSdkConfig();

        initToast();

        setLanguage();

        Config.isNewApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

    }

    private void setLanguage() {
        String language = SharePreferenceUtil.getInstance().shareGet(SharePreferenceUtil.APP_LANGUAGE);
        if (!TextUtils.isEmpty(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }

    private void initSdkConfig() {
        BleOptions<BleDevice> cacheConfig = BleSdkManager.getBleOptions().getCacheConfig(this);
        if (null == cacheConfig) {
            BleSdkManager.getBleOptions()
                    // 日志开关
                    .setLogSwitch(true)
                    // 间歇性扫描开关
                    .setIntermittentScanning(true)
                    // 间歇时间
                    .setIntermittentTime(1000)
                    // 扫描间隔
                    .setScanPeriod(20000)
                    // 持续扫描开关
                    .setContinuousScanning(false)
                    // 连接失败重试次数
                    .setConnectFailedRetryCount(5)
                    // 连接超时时间
                    .setConnectTimeout(3_000)
                    // 数据库支持
                    .setDatabaseSupport(true)
                    // 蓝牙扫描等级
                    .setBleScanLevel(BleScanLevelEnum.SCAN_MODE_BALANCED)
                    // 设备存活时间
                    .setDeviceSurviveTime(10_000)
                    // 忽略重复设备
                    .setIgnoreRepeat(false)
                    // 扫描过滤条件
                    .setFilterInfo(new BleOptions.FilterInfo())
                    // 排序规则
                    .setSortType(SortTypeEnum.RSSI_FALL)
                    .saveCacheConfig();
        } else {
            BleSdkManager.setBleOptions(cacheConfig);
        }
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


    @SuppressLint("CheckResult")
    private void initToast() {
        Toasty.Config.getInstance()
                .setTextSize(14)
                .setGravity(Gravity.TOP, 0, 0)
                .apply();
    }

    /**
     * 发送notify 本地消息推送
     *
     * @param title
     * @param text
     */
    public static void showNotify(int iconId, String title, String text, Context context) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String CHANNEL_ID = createNotificationChannel(context);
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }
        try {
            COUNT_ID++;
            builder
                    .setSmallIcon(iconId)
                    .setContentTitle(title)
                    .setContentText(text) //收到通知时的效果，这里是默认声音
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
            ;

            mNotificationManager.notify(COUNT_ID, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getStatusHeight() {
        int result = 0;
        @SuppressLint("DiscouragedApi")
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

    private void initPermission() {
        String[] requestPermissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions = new String[]{
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
        } else {
            requestPermissions = new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA
            };

        }
        ActivityCompat.requestPermissions(this,
                requestPermissions, 125);

    }

}
