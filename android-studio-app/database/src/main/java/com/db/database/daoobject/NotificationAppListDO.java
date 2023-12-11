package com.db.database.daoobject;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_app_list")
public class NotificationAppListDO {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "app_name")
    public String appName;

    @ColumnInfo(name = "package_name")
    public String packageName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
