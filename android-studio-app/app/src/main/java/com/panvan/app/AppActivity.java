package com.panvan.app;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class AppActivity extends AppInitTools {

    public WebView webView;
    public static AppActivity appActivity;
    public HashMap<Integer,Intent> intentMap=new HashMap<>();

    public void reloadMainPage(String url){
        if(url==null){
            url=Config.getWebIndexUrl();
        }
        webView.loadUrl(url);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initContentBefore();
//        setContentView(R.layout.activity_app);
        //获得控件
        openWelcome();
        initRefresh();
        String url=getIntent().getStringExtra("webUrl");
        url=url==null?Config.getWebIndexUrl():url;
        System.out.println("========================="+url);
        initWebView(url);
        AppActivity.appActivity = this;
        Config.mainContext = AppActivity.this;

        this.setTitleColor(this.getResources().getColor(R.color.system_color));

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder =new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    /**
     * 打开窗口，并返回窗口句柄
     * @param url
     * @return
     */
    public Intent openWebView(String url){
        Intent intent=new Intent();
        intent.setClass(this,AppActivity.class);
        intent.putExtra("webUrl",url);
        int hashCode=intent.hashCode();
//        startActivity(intent);
        startActivityForResult(intent,hashCode);
        intentMap.put(hashCode,intent);
        return intent;
    }
    public boolean closeWebView(Integer formId){
        finishActivity(formId);
        return true;
    }
    public Intent getWebViewIntent(String hashCode){
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
        /**TODO 调用 1.js代码进行后退 */
        /**TODO  2.如果后退到最顶层，则提示是否要退出 */

        webView.evaluateJavascript("javascript:_androidGoBackBySystemButton()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //此处为 js 返回的结果
                if(value.equals("false") && Config.isShowExitDialogByBackButton){
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
    public void initWebView(String url) {
        webView = findViewById(R.id.mainWebView);
        //访问网页
        webView.loadUrl(url);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                view.addJavascriptInterface(new JavaScriptObject(AppActivity.this), "androidJS");
                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //用javascript自定义错误界面
                try{
                    view.loadUrl(Config.getWebErrorUrl()+"?"+
                            "errorCode="+errorCode+
                            "&errorDescription="+description+
                            "&url="+ failingUrl
                            .replaceAll("[&]","%26")
                            .replaceAll("[=]","%3D")
                            .replaceAll("[?]","%3F")
                            .replaceAll("[#]","%23")
                    );
                }catch(Exception e){
                    e.printStackTrace();
                }
                super.onReceivedError(view,errorCode,description,failingUrl);
            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //用javascript自定义错误界面
                try{
                    view.loadUrl(Config.getWebErrorUrl()+"?"+
                            "errorCode="+error.getErrorCode()+
                            "&errorDescription="+error.getDescription()+
                            "&method="+request.getMethod()+
                            "&url="+ request.getUrl().toString()
                            .replaceAll("[&]","%26")
                            .replaceAll("[=]","%3D")
                            .replaceAll("[?]","%3F")
                            .replaceAll("[#]","%23")
                    );
                }catch(Exception e){
                    e.printStackTrace();
                }
                super.onReceivedError(view,request,error);
            }
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                //用javascript自定义错误界面
                super.onReceivedHttpError(view,request,errorResponse);
                view.loadUrl(Config.getWebErrorUrl()+"?"+
                        "errorCode="+errorResponse.getStatusCode()+
                        "&errorDescription="+errorResponse.getData().toString()+
                        "&method="+request.getMethod()+
                        "&url="+ request.getUrl().toString()
                        .replaceAll("[&]","%26")
                        .replaceAll("[=]","%3D")
                        .replaceAll("[?]","%3F")
                        .replaceAll("[#]","%23")
                );
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {


            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                try {
                    imageUri=null;
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }
                    isCapture = fileChooserParams.isCaptureEnabled();
                    uploadMessage = filePathCallback;
                    if (isCapture) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        File photo = createTempImageFile();
                        imageUri = Uri.fromFile(photo);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, 305);
                        return true;
                    }
                    String [] fileType= fileChooserParams.getAcceptTypes();
                    String fileTypeStr=String.join(",",fileType);
                    Intent  intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(fileTypeStr);
                    startActivityForResult(Intent.createChooser(intent, "File Chooser"), 306);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;

            }
            //低版本 android 拍照支持
            //            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
            //                mUploadMessage = uploadMsg;
            //                File fileUrl = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".png");
            //                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            //                i.addCategory(Intent.CATEGORY_OPENABLE);
            //                i.setType("image/*");
            //                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            //            }

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
//        webView.setDownloadListener(new AppWebViewDownLoadListener());
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

//        //本地HTML里面有跨域的请求 原生webview需要设置之后才能实现跨域请求
//        try {
//            if (Build.VERSION.SDK_INT >= 16) {
//                Class<?> clazz = webView.getSettings().getClass();
//                Method method = clazz.getMethod(
//                        "setAllowUniversalAccessFromFileURLs", boolean.class);
//                if (method != null) {
//                    method.invoke(webView.getSettings(), true);
//                }
//            }
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 305&&resultCode!=0) {
            uploadMessage.onReceiveValue(new Uri[]{imageUri} );
            uploadMessage = null;
            return;
        }
        if (requestCode == 306) {
            Uri result = intent == null || resultCode != this.RESULT_OK ? null : intent.getData();
            uploadMessage.onReceiveValue(new Uri[]{result} );
            uploadMessage = null;
        }
    }
    public void initRefresh() {
        Button button = findViewById(R.id.refresh);
        button.setVisibility( View.INVISIBLE);
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
        String dateTimeString =new Date().getTime()+"";
        File imageFile = File.createTempFile(dateTimeString + "-", ".png", destFolder);
        return imageFile;
    }
    /**
     * 打开欢迎界面
     */
    public void openWelcome(){
        Intent intent=new Intent();
        intent.setClass(this,WelComeActivity.class);
        startActivity(intent);
    }


}