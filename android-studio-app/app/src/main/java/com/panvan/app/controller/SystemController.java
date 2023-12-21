package com.panvan.app.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.db.database.UserDatabase;
import com.db.database.daoobject.NotificationAppListDO;
import com.panvan.app.AppActivity;
import com.panvan.app.AppListActivity;
import com.panvan.app.Config;
import com.panvan.app.SecondActivity;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.annotation.AppRequestMethod;
import com.panvan.app.data.constants.PermissionsRequestConstants;
import com.panvan.app.response.RespVO;
import com.panvan.app.utils.DownloadUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@AppController(path = "system")
public class SystemController {

    @AppRequestMapper(path = "/setTitle")
    public void setTitle(Boolean isShow) throws Exception {
        throw new Exception("暂不支持，请使用vue相关的标题栏组件");
    }

    @AppRequestMapper(path = "/setStatusBar")
    public void setStatusBar(Boolean isShow) throws Exception {
        throw new Exception("暂不支持，请使用vue相关的状态栏组件");
    }

    @AppRequestMapper(path = "/setStatusBarHeight")
    public double setStatusBarHeight() throws Exception {
        int result = 0;
        int resourceId = AppActivity.appActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            System.out.println(resourceId);
            result = AppActivity.appActivity.getResources().getDimensionPixelSize(resourceId);

        }
        return Config.Dp2Px(result);
    }

    @AppRequestMapper(path = "/pushMessage", method = AppRequestMethod.POST)
    public Boolean pushMessage(String title, String text) {

        Toast.makeText(Config.mainContext, "text", Toast.LENGTH_SHORT).show();

        return true;
    }


    @AppRequestMapper(path = "/goPage", method = AppRequestMethod.POST)
    public Boolean goPage() {
        Intent intent = new Intent();
        intent.setClass(AppActivity.appActivity, SecondActivity.class);
        AppActivity.appActivity.startActivity(intent);
        return true;
    }

    /**
     * 获取设备唯一编号
     *
     * @return
     */
    @AppRequestMapper(path = "/openWebView", method = AppRequestMethod.GET)
    public String openWebView(String url) {
        Intent intent = AppActivity.appActivity.openWebView(url);
        return intent.hashCode() + "";
    }

    /**
     * 获取设备唯一编号
     *
     * @return
     */
    @AppRequestMapper(path = "/closeWebView", method = AppRequestMethod.GET)
    public Boolean closeWebView(Integer formId) {
        return AppActivity.appActivity.closeWebView(formId);
    }

    /**
     * 获取系统 apk版本
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    @AppRequestMapper(path = "/versionInfo", method = AppRequestMethod.GET)
    public HashMap<String, String> getVersionInfo() throws Exception {
        Context context = AppActivity.appActivity;
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.toString();
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.put("versionName", packInfo.versionName);
        hash.put("versionLongCode", packInfo.getLongVersionCode() + "");
        hash.put("versionBaseRevisionCode", packInfo.baseRevisionCode + "");
        hash.put("versionCode", packInfo.versionCode + "");
        return hash;
    }

    @AppRequestMapper(path = "/manager-app", method = AppRequestMethod.POST)
    public void openPage() {
        List<NotificationAppListDO> queryAll = UserDatabase.getInstance().getNotificationAppListDAO().queryAll();
        Intent intent = new Intent(Config.mainContext, AppListActivity.class);
        if (CollectionUtils.isNotEmpty(queryAll)) {
            for (NotificationAppListDO appListDO : queryAll) {
                intent.putExtra(appListDO.getPackageName(), appListDO.getAppName());
            }
        }
        Config.mainContext.startActivity(intent);
    }


    @AppRequestMapper(path = "/download", method = AppRequestMethod.POST)
    public RespVO<Void> downloadFile(String fileType, String fileName) {
        String url = "http://172.16.31.158:40001/api-node/app/file-download/BCG_WRISTBAND/" + fileType + "/" + fileName + "/download";
        String path = Config.mainContext.getFilesDir().getAbsolutePath()
                + File.separator + fileType;
        return DownloadUtil.downloadFile(url, path, fileName);
    }


    @AppRequestMapper(path = "/app-version")
    public RespVO<String> getAppVersion() {
        try {
            PackageInfo packageInfo = Config.mainContext.getPackageManager().getPackageInfo(Config.mainContext.getPackageName(), 0);
            // 版本号
            return RespVO.success(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return RespVO.success();
    }

    @AppRequestMapper(path = "/app-install", method = AppRequestMethod.POST)
    public RespVO<Void> installApp() {
        String path = Config.mainContext.getFilesDir().getAbsolutePath()
                + File.separator + "ANDROID_APP" + File.separator;

        File file = new File(path);

        if (Objects.isNull(file) || !file.exists() || file.listFiles().length == 0) {
            return RespVO.failure("文件不存在");
        }

        File newFile = null;
        for (File listFile : file.listFiles()) {
            if (!listFile.isHidden()) {
                if (Objects.isNull(newFile)) {
                    newFile = listFile;
                } else if (listFile.lastModified() > newFile.lastModified()) {
                    newFile = listFile;
                }
            }
        }
        Uri uri = FileProvider.getUriForFile(Config.mainContext, Config.mainContext.getPackageName() + ".file_provider", newFile);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(newFile), "application/vnd.android.package-archive");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Config.mainContext.getPackageManager().canRequestPackageInstalls()) {
                // 请求安装未知应用来源的权限
                Intent intentIns = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + Config.mainContext.getPackageName()));
                Activity activity = (Activity) Config.mainContext;
                activity.startActivityForResult(intentIns, PermissionsRequestConstants.REQUEST_INSTALL_PACKAGES_CODE);
                return RespVO.failure("当前没有权限安装");
            }
        }
        Config.mainContext.startActivity(intent);
        return RespVO.success();
    }

}
