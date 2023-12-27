package com.db.database.service;

import com.db.database.UserDatabase;
import com.db.database.daoobject.DeviceDO;
import com.db.database.daoobject.NotificationAppListDO;

import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationAppListDataService {
    private static NotificationAppListDataService INSTANCE = null;


    public static NotificationAppListDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new NotificationAppListDataService();
        }
        return INSTANCE;
    }

    public void query(Callback callback) {
        Single.fromCallable(() -> UserDatabase.getInstance().getNotificationAppListDAO().queryAll())
                .subscribeOn(Schedulers.io()) // 在IO线程进行查询
                .observeOn(AndroidSchedulers.mainThread()) // 在主线程回调结果
                .subscribe(new SingleObserver<List<NotificationAppListDO>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // 订阅时回调
                    }

                    @Override
                    public void onSuccess(List<NotificationAppListDO> myObject) {
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

    public interface Callback {
        void success(List<NotificationAppListDO> doList);

        void failed();
    }
}
