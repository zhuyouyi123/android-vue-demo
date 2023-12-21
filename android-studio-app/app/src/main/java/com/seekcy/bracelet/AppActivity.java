package com.seekcy.bracelet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.db.database.utils.DateUtils;
import com.seekcy.bracelet.data.constants.SharePreferenceConstants;
import com.seekcy.bracelet.utils.ActivityResultUtil;

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
                if (value.equals("false")) {
                    // new AlertDialog.Builder(AppActivity.this)
                    //         .setMessage("是否要退出当前应用?")
                    //         .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    //             @Override
                    //             public void onClick(DialogInterface dialog, int which) {
                    //
                    //             }
                    //         })
                    //         .setNegativeButton("否", null)
                    //         .show();
                    AppActivity.super.onBackPressed();
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

        if (StringUtils.isBlank(shareGet)) {
            SharePreferenceUtil.getInstance().shareSet(SharePreferenceConstants.FIRST_USE_TIME, DateUtils.getPreviousDate(3));
        }
    }


}