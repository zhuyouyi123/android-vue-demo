package com.db.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.db.database.daoobject.NotificationAppListDO;

import java.util.List;

@Dao
public interface NotificationAppListDAO {

    @Insert
    void insert(NotificationAppListDO... notificationAppArr);

    @Query("SELECT * FROM notification_app_list")
    List<NotificationAppListDO> queryAll();

    @Query("DELETE FROM notification_app_list WHERE package_name=:packageName")
    void deleteByPackageName(String packageName);

    @Query("DELETE FROM notification_app_list")
    void deleteAll();
}
