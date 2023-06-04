package com.seek.config;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.seek.config.entity.constants.ActiveForResultConstants;
import com.seek.config.utils.I18nUtil;
import com.seek.config.utils.JsBridgeUtil;
import com.seek.config.vue.JavaScriptObject;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;


public class AppActivity extends AppInitTools {

    @SuppressLint("StaticFieldLeak")
    public static AppActivity appActivity;
    public HashMap<Integer, Intent> intentMap = new HashMap<>();

    private static Long BACK_TIME;

    public void reloadMainPage(String url) {
        if (url == null) {
            Config.loadUrl();
            return;
        }
        webView.loadUrl(url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获得控件
        openWelcome();
        initRefresh();
        String url = getIntent().getStringExtra("webUrl");
        url = url == null ? Config.getUrl() : url;
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
     * @param url 地址
     * @return in
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

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true); //-> 是否开启JS支持
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //-> 是否允许JS打开新窗口
        webSettings.setUseWideViewPort(true); //-> 缩放至屏幕大小
        webSettings.setLoadWithOverviewMode(true); //-> 缩放至屏幕大小
        webSettings.setSupportZoom(false); //-> 是否支持缩放
        webSettings.setBuiltInZoomControls(false); //-> 是否支持缩放变焦，前提是支持缩放
        webSettings.setDisplayZoomControls(false); //-> 是否隐藏缩放控件
        webSettings.setAllowFileAccess(true); //-> 是否允许访问文件
        webSettings.setAllowContentAccess(true); //-> 是否允许访问文件
        webSettings.setAllowUniversalAccessFromFileURLs(true); //->允许跨域
        webSettings.setDomStorageEnabled(true); //-> 是否节点缓存
        webSettings.setDatabaseEnabled(true); //-> 是否数据缓存
        webSettings.setAppCacheEnabled(true); //-> 是否应用缓存
        String uri = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(uri); //-> 设置缓存路径
        //设置本地调用对象及其接口
        webView.addJavascriptInterface(new JavaScriptObject(), "androidJS");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == ActiveForResultConstants.SCAN_QR_CODE_REQUEST_CODE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null && StringUtils.isNotBlank(obj.originalValue)) {
                SharePreferenceUtil.getInstance().shareSet(ActiveForResultConstants.SCAN_QR_REQUEST_KEY, obj.originalValue);
                String scanResult = obj.originalValue.replaceAll(":", "").toUpperCase();
                JsBridgeUtil.pushEvent(JsBridgeUtil.SCAN_RESULT, scanResult);
            }
        }

    }

    public void initRefresh() {
        Button button = findViewById(R.id.refresh);
        button.setVisibility(!Config.APK ? View.VISIBLE : View.INVISIBLE);
        button.setOnClickListener(v -> {
            webView.reload();
        });
    }


    /**
     * 打开欢迎界面
     */
    public void openWelcome() {
        Intent intent = new Intent();
        intent.setClass(this, WelComeActivity.class);
        startActivity(intent);
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (webView.canGoBack()) {
                webView.goBack();
                return false;
            }
            if (BACK_TIME != null && System.currentTimeMillis() - BACK_TIME < 2000L) {
                finish();
            }
            if (BACK_TIME == null || BACK_TIME < System.currentTimeMillis() - 2000L) {
                BACK_TIME = System.currentTimeMillis();
                Toasty.info(this, I18nUtil.getMessage(I18nUtil.PRESS_AGAIN_TO_EXIT)).show();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }




}