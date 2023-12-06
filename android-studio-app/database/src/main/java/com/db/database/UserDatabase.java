package com.db.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.db.database.dao.ConfigurationDAO;
import com.db.database.dao.DeviceDAO;
import com.db.database.dao.UserInfoDAO;
import com.db.database.dao.UserTargetConfigurationDAO;
import com.db.database.daoobject.ConfigurationDO;
import com.db.database.daoobject.DeviceDO;
import com.db.database.daoobject.UserInfoDO;
import com.db.database.daoobject.UserTargetConfigurationDO;

@Database(entities = {UserInfoDO.class, DeviceDO.class, UserTargetConfigurationDO.class, ConfigurationDO.class}, version = 1, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    public abstract UserInfoDAO getUserInfoDAO();

    public abstract DeviceDAO getDeviceDAO();

    public abstract ConfigurationDAO getConfigurationDAO();

    public abstract UserTargetConfigurationDAO getUserTargetConfigurationDAO();

    /**
     * 单例模式  返回 DB
     */
    private static UserDatabase INSTANCE;

    public static synchronized UserDatabase getInstance() {
        return INSTANCE;
    }

    public static void init(Context context, String databaseName) {
        INSTANCE = Room.databaseBuilder
                        (context.getApplicationContext(), UserDatabase.class, databaseName)
                .build();
    }
}
