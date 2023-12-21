package com.db.database.cache;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.db.database.callback.DBCallback;
import com.db.database.daoobject.AllDayDataDO;
import com.db.database.daoobject.CommunicationDataDO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class CommunicationDataCache {

    /**
     * 缓存数据
     */
    private static final ConcurrentMap<Integer, ConcurrentMap<String, CommunicationDataDO>> dataCacheMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, ConcurrentMap<String, Long>> CACHE_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Integer, AllDayDataDO> ALL_DAY_DATA_DO_CONCURRENT_MAP = new ConcurrentHashMap<>();

    private static CommunicationDataCache INSTANCE = null;

    public static CommunicationDataCache getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new CommunicationDataCache();
        }
        return INSTANCE;
    }

    public void init(List<CommunicationDataDO> list, DBCallback dbCallback) {
        dataCacheMap.clear();
        CACHE_MAP.clear();
        if (Objects.nonNull(list) && !list.isEmpty()) {
            Map<Integer, Map<String, CommunicationDataDO>> dataDOMap = new HashMap<>();

            for (CommunicationDataDO dataDO : list) {
                String data = dataDO.getData();
                if ("".equals(data) || null == data) {
                    Integer dataDate = dataDO.getDataDate();

                    Map<String, CommunicationDataDO> communicationDataDOMap = dataDOMap.get(dataDate);
                    if (null == communicationDataDOMap) {
                        communicationDataDOMap = new HashMap<>();
                    }
                    communicationDataDOMap.put(dataDO.getType(), dataDO);

                    dataDOMap.put(dataDate, communicationDataDOMap);
                } else {
                    push(dataDO.getDataDate(), dataDO);
                }
            }

            for (Map.Entry<Integer, Map<String, CommunicationDataDO>> mapEntry : dataDOMap.entrySet()) {
                for (Map.Entry<String, CommunicationDataDO> dataDOEntry : mapEntry.getValue().entrySet()) {
                    CommunicationDataDO value = dataDOEntry.getValue();

                    Integer dataDate = value.getDataDate();

                    for (int i = 0; i < 1; i++) {
                        CommunicationDataDO communicationDataDO = new CommunicationDataDO();
                        communicationDataDO.setId(value.getId());
                        communicationDataDO.setType(value.getType());
                        communicationDataDO.setData(value.getData());
                        communicationDataDO.setDataDate(value.getDataDate());
                        communicationDataDO.setCompleteData(value.getCompleteData());

                        push(dataDate, communicationDataDO);
                    }
                }
            }
        }
        dbCallback.success();
    }

    public ConcurrentMap<Integer, ConcurrentMap<String, Long>> getCacheMap() {
        return CACHE_MAP;
    }

    public void clearCacheMap() {
        CACHE_MAP.clear();
    }

    public void addCacheMap(Integer date, String type) {
        ConcurrentMap<String, Long> map;
        if (CACHE_MAP.containsKey(date)) {
            map = CACHE_MAP.get(date);
            if (null == map) {
                map = new ConcurrentHashMap<>();
            }
        } else {
            map = new ConcurrentHashMap<>();
            map.put(type, System.currentTimeMillis());
        }

        map.put(type, System.currentTimeMillis());
        CACHE_MAP.put(date, map);
    }

    public static void push(Integer date, CommunicationDataDO communicationDataDO) {
        ConcurrentMap<String, CommunicationDataDO> map;
        if (!dataCacheMap.containsKey(date)) {
            map = new ConcurrentHashMap<>();
        } else {
            map = dataCacheMap.get(date);
            if (null == map) {
                map = new ConcurrentHashMap<>();
            }
        }

        map.put(communicationDataDO.getType(), communicationDataDO);

        dataCacheMap.put(date, map);
    }

    public void updateDataCache(CommunicationDataDO communicationDataDO) {
        push(communicationDataDO.getDataDate(), communicationDataDO);
        addCacheMap(communicationDataDO.getDataDate(), communicationDataDO.getType());
    }

    public static ConcurrentMap<String, CommunicationDataDO> get(Integer date) {
        if (dataCacheMap.containsKey(date)) {
            return dataCacheMap.get(date);
        }
        return new ConcurrentHashMap<>();
    }

    public static CommunicationDataDO get(Integer date, String type) {
        ConcurrentMap<String, CommunicationDataDO> map = get(date);

        if (map.containsKey(type)) {
            return map.get(type);
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<CommunicationDataDO> listByType(String type) {
        List<CommunicationDataDO> list = new ArrayList<>();
        for (Map.Entry<Integer, ConcurrentMap<String, CommunicationDataDO>> entry : dataCacheMap.entrySet()) {
            for (Map.Entry<String, CommunicationDataDO> doEntry : entry.getValue().entrySet()) {
                if (doEntry.getKey().startsWith(type)) {
                    list.add(doEntry.getValue());
                }
            }
        }
        return list.stream()
                .sorted(Comparator.comparing(CommunicationDataDO::getDataDate))
                .collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<CommunicationDataDO> listByType(Integer date, String type) {
        ConcurrentMap<String, CommunicationDataDO> map = get(date);

        List<CommunicationDataDO> list = new ArrayList<>();

        for (Map.Entry<String, CommunicationDataDO> dataDOEntry : map.entrySet()) {
            if (dataDOEntry.getKey().startsWith(type)) {
                list.add(dataDOEntry.getValue());
            }
        }

        return list.stream()
                .sorted(Comparator.comparing(CommunicationDataDO::getDataDate))
                .collect(Collectors.toList());
    }

    public static String getKey(String type, int sort) {
        return type + "-" + sort;
    }


    public void initAllDay(List<AllDayDataDO> list) {
        ALL_DAY_DATA_DO_CONCURRENT_MAP.clear();
        if (Objects.nonNull(list) && !list.isEmpty()) {
            for (AllDayDataDO allDayDataDO : list) {
                ALL_DAY_DATA_DO_CONCURRENT_MAP.put(allDayDataDO.getDateTime(), allDayDataDO);
            }
        }
    }

    public ConcurrentMap<Integer, AllDayDataDO> getAllDayMap() {
        return ALL_DAY_DATA_DO_CONCURRENT_MAP;
    }
}
