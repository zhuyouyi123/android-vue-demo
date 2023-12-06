package com.panvan.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.db.database.utils.DateUtils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.panvan.app.connect.DeviceConnectHandle;
import com.panvan.app.data.constants.ActiveForResultConstants;
import com.panvan.app.data.constants.SharePreferenceConstants;
import com.panvan.app.utils.ActivityResultUtil;
import com.panvan.app.utils.PermissionsUtil;
import com.panvan.app.utils.SdkUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;


public class AppActivity extends AppInitTools {

    @SuppressLint("StaticFieldLeak")
    public static AppActivity appActivity;
    public HashMap<Integer, Intent> intentMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获得控件
        openWelcome();
        initRefresh();

        AppActivity.appActivity = this;
        this.setTitleColor(this.getResources().getColor(R.color.system_color));



        setFirstUseTime();
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
        /**TODO 调用 1.js代码进行后退 */
        /**TODO  2.如果后退到最顶层，则提示是否要退出 */

        webView.evaluateJavascript("javascript:_androidGoBackBySystemButton()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //此处为 js 返回的结果
                if (value.equals("false") && Config.isShowExitDialogByBackButton) {
                    new AlertDialog.Builder(AppActivity.this)
                            .setMessage("是否要退出当前应用?")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AppActivity.super.onBackPressed();
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
                }
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ActivityResultUtil.handleResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ActivityResultUtil.handlePermissions(requestCode, permissions, grantResults);

    }


    public void initRefresh() {
        Button button = findViewById(R.id.refresh);
        button.setVisibility(View.INVISIBLE);
       button.setVisibility(!Config.APK ? View.VISIBLE : View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("点击按钮事件！");
                webView.reload();
            }
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


    private void setFirstUseTime() {
        String shareGet = SharePreferenceUtil.getInstance().shareGet(SharePreferenceConstants.FIRST_USE_TIME);

        if (StringUtils.isBlank(shareGet)){
            SharePreferenceUtil.getInstance().shareSet(SharePreferenceConstants.FIRST_USE_TIME, DateUtils.getPreviousDate(3));
        }
    }


}