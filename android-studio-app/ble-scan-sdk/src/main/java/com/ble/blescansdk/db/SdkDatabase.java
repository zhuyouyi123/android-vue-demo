package com.ble.blescansdk.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.db.dao.SecretKeyDAO;
import com.ble.blescansdk.db.dataobject.SecretKeyDO;

/**
 * @author zhuyouyi
 * 数据库 关联  之前的 表  数据库信息
 * 用户只需要操作dao，我们必须暴露dao，dao被用户拿到后，就能对数据库 增删改查了
 */
@Database(entities = {SecretKeyDO.class},
        version = 1, exportSchema = false)
public abstract class SdkDatabase extends RoomDatabase {

    public abstract SecretKeyDAO getSecretKeyDAO();

    /**
     * 单例模式  返回 DB
     */
    private static SdkDatabase INSTANCE = null;

    public static boolean supportDatabase = false;

    public static void init() {
        supportDatabase = BleSdkManager.getBleOptions().isDatabaseSupport();
    }

    public static synchronized SdkDatabase getInstance() {
        if (INSTANCE == null && supportDatabase) {
            INSTANCE = Room.databaseBuilder(BleSdkManager.getContext().getApplicationContext(), SdkDatabase.class, "loc_sdk_database").build();
        }
        return INSTANCE;
    }


}
