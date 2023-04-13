//package com.seek.config;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.webkit.ValueCallback;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//
//public class AppWebChromeClient  extends WebChromeClient {
//    ValueCallback<Uri[]> uploadMessageAboveL;
//    ValueCallback<Uri> uploadMessage;
//    //For Android  >= 5.0
//    @Override
//    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//        uploadMessageAboveL = filePathCallback;
//        //调用系统相机或者相册
//        uploadPicture();
//        return true;
//    }
//    //For Android  >= 4.1
//    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
//        uploadMessage = valueCallback;
//        //调用系统相机或者相册
//        uploadPicture();
//    }
//}
//
//
