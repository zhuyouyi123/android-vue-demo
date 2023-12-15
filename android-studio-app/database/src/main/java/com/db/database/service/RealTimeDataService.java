package com.db.database.service;

import com.db.database.AppDatabase;
import com.db.database.daoobject.RealTimeDataDO;
import com.db.database.utils.DateUtils;

import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class RealTimeDataService {

    private static RealTimeDataService INSTANCE = null;

    public static RealTimeDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new RealTimeDataService();
        }
        return INSTANCE;
    }


    public String query() {
        if (Objects.isNull(AppDatabase.getInstance())) {
            return null;
        }
        RealTimeDataDO realTimeDataDO = AppDatabase.getInstance().getRealTimeDataDAO().queryByDateTime(DateUtils.getPreviousIntDate());

        if (Objects.isNull(realTimeDataDO)) {
            return null;
        }

        return realTimeDataDO.getParamsJson();
    }

    /**
     * 保存实时数据
     *
     * @param data 实时数据
     */
    public void save(String data) {
        Completable.fromAction(() -> {
                    AppDatabase.getInstance().getRealTimeDataDAO().deleteAll();
                    RealTimeDataDO realTimeDataDO = new RealTimeDataDO();
                    realTimeDataDO.setDateTime(DateUtils.getPreviousIntDate());
                    realTimeDataDO.setParamsJson(data);
                    AppDatabase.getInstance().getRealTimeDataDAO().insert(realTimeDataDO);
                }).subscribeOn(Schedulers.io())
                .subscribe();
    }
}
