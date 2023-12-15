package com.db.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.db.database.dao.AllDayDataDAO;
import com.db.database.dao.CommunicationDataDAO;
import com.db.database.dao.RealTimeDataDAO;
import com.db.database.daoobject.AllDayDataDO;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.daoobject.RealTimeDataDO;

import java.util.Objects;

@Database(entities = {CommunicationDataDO.class, RealTimeDataDO.class, AllDayDataDO.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CommunicationDataDAO getCommunicationDataDAO();

    public abstract RealTimeDataDAO getRealTimeDataDAO();

    public abstract AllDayDataDAO getAllDayDataDAO();

    /**
     * 单例模式  返回 DB
     */
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance() {
        return INSTANCE;
    }

    public static void init(Context context, String databaseName) {
        if (Objects.nonNull(INSTANCE)) {
            return;
        }
        INSTANCE = Room.databaseBuilder
                        (context.getApplicationContext(), AppDatabase.class, databaseName)
                // 如果我们想玩数据库 默认是异步线程
                // 慎用：强制开启 主线程 也可以操作 数据库  （测试可以用， 真实环境下 不要用）
                //.allowMainThreadQueries()
                //                    .addMigrations(AppDatabase.BUILD_CONFIG_1_2)
                .build();
    }
}
