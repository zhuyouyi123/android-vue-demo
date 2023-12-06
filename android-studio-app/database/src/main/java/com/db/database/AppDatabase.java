package com.db.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.db.database.dao.CommunicationDataDAO;
import com.db.database.daoobject.CommunicationDataDO;

@Database(entities = {CommunicationDataDO.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CommunicationDataDAO getCommunicationDataDAO();

    /**
     * 单例模式  返回 DB
     */
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance() {
        return INSTANCE;
    }

    public static void init(Context context, String databaseName) {
        INSTANCE = Room.databaseBuilder
                        (context.getApplicationContext(), AppDatabase.class, databaseName)
                // 如果我们想玩数据库 默认是异步线程
                // 慎用：强制开启 主线程 也可以操作 数据库  （测试可以用， 真实环境下 不要用）
                //.allowMainThreadQueries()
                //                    .addMigrations(AppDatabase.BUILD_CONFIG_1_2)
                .build();
    }
}
