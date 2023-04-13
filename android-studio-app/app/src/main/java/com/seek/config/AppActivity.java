package com.seek.config;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.BleSdkManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;


public class AppActivity extends AppInitTools {

    @SuppressLint("StaticFieldLeak")
    public static AppActivity appActivity;
    public HashMap<Integer, Intent> intentMap = new HashMap<>();

    public void reloadMainPage(String url) {
        if (url == null) {
            url = Config.getWebIndexUrl();
        }
        webView.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initContentBefore();
//        setContentView(R.layout.activity_app);
        //获得控件
//        openWelcome();
        initRefresh();
        String url = getIntent().getStringExtra("webUrl");
        url = url == null ? Config.getWebIndexUrl() : url;
        initWebView(url);
        AppActivity.appActivity = this;
        Config.mainContext = AppActivity.this;

        Config.webView = webView;

        this.setTitleColor(this.getResources().getColor(R.color.system_color));

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    /**
     * 打开窗口，并返回窗口句柄
     *
     * @param url
     * @return
     */
    public Intent openWebView(String url) {
        Intent intent = new Intent();
        intent.setClass(this, AppActivity.class);
        intent.putExtra("webUrl", url);
        int hashCode = intent.hashCode();
//        startActivity(intent);
        startActivityForResult(intent, hashCode);
        intentMap.put(hashCode, intent);
        return intent;
    }

    public boolean closeWebView(Integer formId) {
        finishActivity(formId);
        return true;
    }

    public Intent getWebViewIntent(String hashCode) {
        return intentMap.get(hashCode);
    }

    /**
     * 拍照
     */
    public ValueCallback<Uri[]> uploadMessage;
    private static boolean isCapture;
    private Uri imageUri;

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(String url) {
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

        /**
         * 打开浏览器下载
         */
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
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

//        webSettings.setMediaPlaybackRequiresUserGesture(false); //-> 是否要手势触发媒体
//        webSettings.setStandardFontFamily("sans-serif"); //-> 设置字体库格式
//        webSettings.setFixedFontFamily("monospace"); //-> 设置字体库格式
//        webSettings.setSansSerifFontFamily("sans-serif"); //-> 设置字体库格式
//        webSettings.setSerifFontFamily("sans-serif"); //-> 设置字体库格式
//        webSettings.setCursiveFontFamily("cursive"); //-> 设置字体库格式
//        webSettings.setFantasyFontFamily("fantasy"); //-> 设置字体库格式
        webSettings.setTextZoom(100); //-> 设置文本缩放的百分比
        webSettings.setMinimumFontSize(8); //-> 设置文本字体的最小值(1~72)
        webSettings.setDefaultFontSize(16); //-> 设置文本字体默认的大小
//
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //-> 按规则重新布局
//        webSettings.setLoadsImagesAutomatically(false); //-> 是否自动加载图片
//        webSettings.setDefaultTextEncodingName("UTF-8"); //-> 设置编码格式
//        webSettings.setNeedInitialFocus(true); //-> 是否需要获取焦点
//        webSettings.setGeolocationEnabled(false); //-> 设置开启定位功能
//        webSettings.setBlockNetworkLoads(true); //-> 是否从网络获取资源

        //设置本地调用对象及其接口
        webView.addJavascriptInterface(new JavaScriptObject(this), "androidJS");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 305 && resultCode != 0) {
            uploadMessage.onReceiveValue(new Uri[]{imageUri});
            uploadMessage = null;
            return;
        }
        if (requestCode == 306) {
            Uri result = intent == null || resultCode != this.RESULT_OK ? null : intent.getData();
            uploadMessage.onReceiveValue(new Uri[]{result});
            uploadMessage = null;
        }
    }

    public void initRefresh() {
        Button button = findViewById(R.id.refresh);
        button.setVisibility(View.INVISIBLE);
//        button.setVisibility(!Config.APK ? View.VISIBLE : View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("点击按钮事件！");
                webView.reload();
            }
        });
    }

    static File createTempImageFile() throws IOException {
        File destFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        destFolder.mkdirs();
        String dateTimeString = new Date().getTime() + "";
        File imageFile = File.createTempFile(dateTimeString + "-", ".png", destFolder);
        return imageFile;
    }

    /**
     * 打开欢迎界面
     */
    public void openWelcome() {
        Intent intent = new Intent();
        intent.setClass(this, WelComeActivity.class);
        startActivity(intent);
    }


}