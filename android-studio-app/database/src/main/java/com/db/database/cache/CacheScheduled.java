package com.db.database.cache;

import android.util.Log;

import com.db.database.AppDatabase;
import com.db.database.daoobject.CommunicationDataDO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheScheduled {

    private static CacheScheduled INSTANCE = null;

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static CacheScheduled getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new CacheScheduled();
        }
        return INSTANCE;
    }

    public void start() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            List<CommunicationDataDO> addList = new ArrayList<>();
            List<CommunicationDataDO> updateList = new ArrayList<>();
            ConcurrentMap<Integer, ConcurrentMap<String, Long>> cacheMap = CommunicationDataCache.getInstance().getCacheMap();
            for (Map.Entry<Integer, ConcurrentMap<String, Long>> entry : cacheMap.entrySet()) {
                ConcurrentMap<String, Long> value = entry.getValue();
                if (Objects.isNull(value)) {
                    continue;
                }

                for (Map.Entry<String, Long> dataDOEntry : value.entrySet()) {
                    Long dataDOEntryValue = dataDOEntry.getValue();

                    if (Objects.isNull(dataDOEntryValue) || System.currentTimeMillis() - dataDOEntryValue > 3000) {
                        CommunicationDataDO communicationDataDO = CommunicationDataCache.get(entry.getKey(), dataDOEntry.getKey());
                        if (null != communicationDataDO) {
                            if (Objects.isNull(communicationDataDO.getId())) {
                                addList.add(communicationDataDO);
                            } else {
                                updateList.add(communicationDataDO);
                            }
                        }
                    }

                }
            }

            CommunicationDataCache.getInstance().clearCacheMap();

            if (addList.size() > 0) {
                AppDatabase.getInstance().getCommunicationDataDAO().insert(addList.toArray(new CommunicationDataDO[0]));
                Log.w("数据库-插入数据", addList.size() + "");
            }

            if (updateList.size() > 0) {
                AppDatabase.getInstance().getCommunicationDataDAO().update(updateList.toArray(new CommunicationDataDO[0]));
                Log.w("数据库-修改数据", updateList.size() + "");
            }

        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }


}
