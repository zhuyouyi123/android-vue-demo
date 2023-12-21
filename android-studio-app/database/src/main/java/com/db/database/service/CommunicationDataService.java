package com.db.database.service;


import android.util.Log;

import com.db.database.AppDatabase;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.callback.DBCallback;
import com.db.database.daoobject.AllDayDataDO;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.daoobject.RealTimeDataDO;
import com.db.database.enums.CommunicationTypeEnum;
import com.db.database.utils.DataConvertUtils;
import com.db.database.utils.DateUtils;

import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class CommunicationDataService {

    private static CommunicationDataService INSTANCE = null;


    public static CommunicationDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new CommunicationDataService();
        }
        return INSTANCE;
    }

    /**
     * 缓存数据初始化
     */
    public void cacheDataInit(DBCallback dbCallback) {
        try {
            Completable.fromAction(() -> {
                        if (Objects.isNull(AppDatabase.getInstance())) {
                            return;
                        }
                        List<CommunicationDataDO> list = AppDatabase.getInstance().getCommunicationDataDAO().queryAll();
                        CommunicationDataCache.getInstance().init(list, dbCallback);

                        List<AllDayDataDO> allDayDataDOList = AppDatabase.getInstance().getAllDayDataDAO().queryAll();
                        CommunicationDataCache.getInstance().initAllDay(allDayDataDOList);
                    }).subscribeOn(Schedulers.io())
                    .subscribe();
        } catch (Exception e) {
            dbCallback.failed();
        }
    }


    public void save(String type, Integer date, String[] data, int sort) {
        Completable.fromAction(() -> {
                    CommunicationTypeEnum typeEnum = CommunicationTypeEnum.getByType(type);

                    if (Objects.isNull(typeEnum)) {
                        return;
                    }

                    CommunicationDataDO communicationDataDO = CommunicationDataCache.get(date, type);
                    String[] arr = new String[typeEnum.getTotalPacket() * typeEnum.getPacketSize()];

                    if (null == communicationDataDO) {
                        communicationDataDO = new CommunicationDataDO();
                        communicationDataDO.setType(type);
                        communicationDataDO.setDataDate(date);
                    } else {
                        arr = communicationDataDO.getData().split(",");
                    }

                    communicationDataDO.setCompleteData(!DateUtils.isSameDay(date));

                    if (sort != 0) {
                        for (int i = 0; i < data.length; i++) {
                            int index = (sort - 1) * typeEnum.getPacketSize() + i;
                            arr[index] = data[i];
                        }
                    }

                    for (int i = 0; i < arr.length; i++) {
                        if (Objects.isNull(arr[i])) {
                            arr[i] = "0";
                        }
                    }

                    communicationDataDO.setData(String.join(",", arr));
                    Log.i("RESULT", date + "-" + String.join(",", arr));
                    CommunicationDataCache.getInstance().updateDataCache(communicationDataDO);
                }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    /**
     * 保存数据
     *
     * @param bytes 数据
     */
    public void save(byte[] bytes, String formatDate, int totalPacket, boolean completeData) {
        String type = DataConvertUtils.byteToHexStr(bytes[7]);
        int sort = bytes[9] & 0xff;

        Integer date = Integer.parseInt(formatDate);

        // CommunicationDataDO communicationDataDO = CommunicationDataCache.get(date, type);
        CommunicationDataDO communicationDataDO = AppDatabase.getInstance().getCommunicationDataDAO().queryByDateAndType(date, type);

        switch (type) {
            case "04":

                break;
            case "05":
                break;
        }

        //
        // if (null == communicationDataDO) {
        //     communicationDataDO = new CommunicationDataDO();
        //     communicationDataDO.setType(type);
        //     communicationDataDO.setSort(sort);
        //     communicationDataDO.setPacketTotal(totalPacket);
        //     communicationDataDO.setDataDate(formatDate);
        // }
        //
        // communicationDataDO.setCompleteData(completeData);
        //
        // communicationDataDO.setData(DataConvertUtils.byteArrToHexStr(DataConvertUtils.subBytes(bytes, 10, bytes.length - 10 - 2)));
        //
        // if (Objects.isNull(communicationDataDO.getId())) {
        //     AppDatabase.getInstance().getCommunicationDataDAO().insert(communicationDataDO);
        // } else {
        //     AppDatabase.getInstance().getCommunicationDataDAO().update(communicationDataDO);
        // }
        //
        // CommunicationDataCache.updateDataCache(communicationDataDO);
    }


    public void deleteTodayByType(String type) {
        AppDatabase.getInstance().getCommunicationDataDAO().deleteTodayByType(DateUtils.getPreviousDate(), type);
    }


}
