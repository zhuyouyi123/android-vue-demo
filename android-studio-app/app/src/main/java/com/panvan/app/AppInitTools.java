package com.panvan.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.db.database.UserDatabase;
import com.panvan.app.Receiver.call.CallViewModel;
import com.panvan.app.utils.SdkUtil;

public class AppInitTools extends AppCompatActivity {

    //获取到通知管理器
    public static NotificationManager mNotificationManager = null;
    @SuppressLint("StaticFieldLeak")
    public static NotificationCompat.Builder builder = null;
    private static int COUNT_ID = 10000;
    public WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentBefore();
        setContentView(R.layout.activity_app);

        String url = getIntent().getStringExtra("webUrl");
        url = url == null ? Config.getWebIndexUrl() : url;
        initWebView(url);

        Config.mainContext = this;
        Config.webView = this.webView;
        SdkUtil.init();
        UserDatabase.init(Config.mainContext, "app-user");
        //
        // if (!PermissionsUtil.isNotificationListenerEnabled()) {
        //     AlertDialog enableNotificationListenerDialog = buildNotificationServiceAlertDialog();
        //     enableNotificationListenerDialog.show();
        // }

        // TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();
        // telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    /**
     * 注册监听
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        CallViewModel.getInstance().listenNone();
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
            String CHANNEL_ID = createNotificationChannel(context);
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

    private AlertDialog buildNotificationServiceAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("请求授予通知权限");
        alertDialogBuilder.setPositiveButton("开启", (dialog, which) -> startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
        alertDialogBuilder.setNegativeButton("不开启", (dialog, which) -> Toast.makeText(Config.mainContext, "不授予通知读取权限Monitor将无法运行！", Toast.LENGTH_SHORT).show());
        return alertDialogBuilder.create();
    }


    public void initWebView(String url) {
        webView = findViewById(R.id.mainWebView);
        //访问网页
        webView.loadUrl(url);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true); //-> 是否开启JS支持
//        webSettings.setPluginsEnabled(true); //-> 是否开启插件支持
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //-> 是否允许JS打开新窗口
//
        webSettings.setUseWideViewPort(true); //-> 缩放至屏幕大小
        webSettings.setLoadWithOverviewMode(true); //-> 缩放至屏幕大小
        webSettings.setSupportZoom(false); //-> 是否支持缩放
        webSettings.setBuiltInZoomControls(false); //-> 是否支持缩放变焦，前提是支持缩放
        webSettings.setDisplayZoomControls(false); //-> 是否隐藏缩放控件
//
        webSettings.setAllowFileAccess(true); //-> 是否允许访问文件
        webSettings.setAllowContentAccess(true); //-> 是否允许访问文件
        webSettings.setAllowUniversalAccessFromFileURLs(true); //->允许跨域
        webSettings.setDomStorageEnabled(true); //-> 是否节点缓存
        webSettings.setDatabaseEnabled(true); //-> 是否数据缓存
        webSettings.setAppCacheEnabled(true); //-> 是否应用缓存
        String uri = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(uri); //-> 设置缓存路径

        webSettings.setTextZoom(100); //-> 设置文本缩放的百分比
        webSettings.setMinimumFontSize(8); //-> 设置文本字体的最小值(1~72)
        webSettings.setDefaultFontSize(16); //-> 设置文本字体默认的大小

        //设置本地调用对象及其接口
        webView.addJavascriptInterface(new JavaScriptObject(this), "androidJS");
    }

}
