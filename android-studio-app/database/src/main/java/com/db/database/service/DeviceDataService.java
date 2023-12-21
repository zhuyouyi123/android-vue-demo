package com.db.database.service;

import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;

import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DeviceDataService {

    private static DeviceDataService INSTANCE = null;


    public static DeviceDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DeviceDataService();
        }
        return INSTANCE;
    }

    public void saveDevice(DeviceDO deviceDO) {
        Completable.fromAction(() -> {
                    DeviceDO device = UserDatabase.getInstance().getDeviceDAO().queryByMac(deviceDO.getAddress());
                    if (Objects.isNull(device)) {
                        UserDatabase.getInstance().getDeviceDAO().insert(deviceDO);
                    } else {
                        deviceDO.setId(device.getId());
                        UserDatabase.getInstance().getDeviceDAO().update(deviceDO);
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe();

    }

    public void query( Callback callback) {
        Single.fromCallable(() -> UserDatabase.getInstance().getDeviceDAO().queryInUse())
                .subscribeOn(Schedulers.io()) // 在IO线程进行查询
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程回调结果
                .subscribe(new SingleObserver<DeviceDO>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // 订阅时回调
                    }

                    @Override
                    public void onSuccess(DeviceDO myObject) {
                        // 查询成功时回调
                        // 这里可以将查询结果更新到UI上
                        callback.success(myObject);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 查询出错时回调
                        callback.failed();
                    }
                });
    }

    public void query(String address, Callback callback) {
        Single.fromCallable(() -> UserDatabase.getInstance().getDeviceDAO().queryByMac(address))
                .subscribeOn(Schedulers.io()) // 在IO线程进行查询
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程回调结果
                .subscribe(new SingleObserver<DeviceDO>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // 订阅时回调
                    }

                    @Override
                    public void onSuccess(DeviceDO myObject) {
                        // 查询成功时回调
                        // 这里可以将查询结果更新到UI上
                        callback.success(myObject);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 查询出错时回调
                        callback.failed();
                    }
                });
    }

    public void setModelAndVersion(String modelStr, String version) {
        Completable.fromAction(() -> UserDatabase.getInstance().getDeviceDAO().updateVerAndFirmware(modelStr, version))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }



    public interface Callback {
        void success(DeviceDO deviceDO);

        void failed();
    }
}
