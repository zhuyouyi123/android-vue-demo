package com.seekcy.bracelet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.db.database.UserDatabase;
import com.db.database.daoobject.NotificationAppListDO;
import com.seekcy.bracelet.Receiver.service.NotificationMonitorService;
import com.seekcy.bracelet.data.entity.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppListActivity extends AppCompatActivity {
    private ListView listView;
    private AppInfoAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        listView = findViewById(R.id.listView);

        // 获取非系统应用列表及其图标
        List<AppInfo> nonSystemApps = getNonSystemApps(this);

        Bundle extras = getIntent().getExtras();
        Map<String, String> map = new HashMap<>();
        if (Objects.nonNull(extras)) {
            for (String packageName : extras.keySet()) {
                map.put(packageName, extras.get(packageName).toString());
            }
        }

        // 使用自定义的 ArrayAdapter 将应用列表显示在 ListView 中
        adapter = new AppInfoAdapter(this, nonSystemApps);
        listView.setAdapter(adapter);

        // 设置选中项
        for (AppInfo appInfo : nonSystemApps) {
            if (map.containsKey(appInfo.getPackageName())) {
                int position = adapter.getPosition(appInfo);
                if (position != -1) {
                    adapter.setSelectedPosition(position, appInfo);
                }
            }
        }

        // 设置列表项点击事件监听器
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            // 获取选中项的信息
            AppInfo appInfo = (AppInfo) adapterView.getItemAtPosition(position);
            // 例如，获取点击项的信息或执行特定操作
            adapter.setSelectedPosition(position, appInfo); // 设置选中项
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(() -> {
            UserDatabase.getInstance().getNotificationAppListDAO().deleteAll();
            UserDatabase.getInstance().getNotificationAppListDAO().insert(adapter.listAppInfo().stream().map(e -> {
                NotificationAppListDO appListDO = new NotificationAppListDO();
                appListDO.setAppName(e.getAppName());
                appListDO.setPackageName(e.getPackageName());
                return appListDO;
            }).toArray(NotificationAppListDO[]::new));
            NotificationMonitorService.reloadEnable();
        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private List<AppInfo> getNonSystemApps(Context context) {
        List<AppInfo> nonSystemApps = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装的应用
        @SuppressLint("QueryPermissionsNeeded")
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);

        // 过滤非系统应用
        for (ApplicationInfo appInfo : appList) {
            String appName = appInfo.loadLabel(packageManager).toString();

            // 判断是否是系统应用
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                Drawable appIcon = appInfo.loadIcon(packageManager);
                nonSystemApps.add(new AppInfo(appName, appInfo.packageName, appIcon));
            }
        }

        return nonSystemApps;
    }


}
