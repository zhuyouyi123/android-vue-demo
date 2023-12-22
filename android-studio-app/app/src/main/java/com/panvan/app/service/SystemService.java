package com.panvan.app.service;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import com.panvan.app.Config;
import com.panvan.app.data.constants.ActiveForResultConstants;
import com.panvan.app.data.constants.PermissionsRequestConstants;
import com.panvan.app.response.RespVO;

import java.io.File;
import java.util.Objects;

public class SystemService {

    private static SystemService INSTANCE = null;

    public static SystemService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new SystemService();
        }
        return INSTANCE;
    }

    public boolean installApp(boolean jumpSetting) {
        String path = Config.mainContext.getFilesDir().getAbsolutePath()
                + File.separator + "ANDROID_APP" + File.separator;

        File file = new File(path);

        if (Objects.isNull(file) || !file.exists() || file.listFiles().length == 0) {
            return false;
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
            if (!Config.mainContext.getPackageManager().canRequestPackageInstalls() && jumpSetting) {
                // 请求安装未知应用来源的权限
                Intent intentIns = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + Config.mainContext.getPackageName()));
                Activity activity = (Activity) Config.mainContext;
                activity.startActivityForResult(intentIns, ActiveForResultConstants.REQUEST_INSTALL_PACKAGES_CODE);
                return false;
            }
        }
        Config.mainContext.startActivity(intent);
        return true;
    }
}
