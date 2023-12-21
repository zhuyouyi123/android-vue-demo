package com.db.database.service;

import com.db.database.AppDatabase;
import com.db.database.UserDatabase;
import com.db.database.daoobject.AllDayDataDO;
import com.db.database.daoobject.ConfigurationDO;
import com.db.database.daoobject.RealTimeDataDO;
import com.db.database.enums.ConfigurationTypeEnum;
import com.db.database.utils.DateUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class AllDayDataService {
    private static AllDayDataService INSTANCE = null;

    private static Integer STEP_TARGET = null;

    public static AllDayDataService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new AllDayDataService();
        }
        return INSTANCE;
    }

    /**
     * 保存实时数据
     *
     * @param
     */
    public void save(AllDayDataDO allDayDataDO) {
        Completable.fromAction(() -> {
                    if (Objects.isNull(UserDatabase.getInstance())) {
                        return;
                    }
                    if (Objects.isNull(AppDatabase.getInstance())) {
                        return;
                    }

                    if (Objects.isNull(STEP_TARGET)) {
                        ConfigurationDO configurationDO = UserDatabase.getInstance().getConfigurationDAO().queryByGroupAndType(ConfigurationTypeEnum.TARGET_STEP.getGroup().getName(), ConfigurationTypeEnum.TARGET_STEP.getType());
                        STEP_TARGET = Objects.isNull(configurationDO) ? ConfigurationTypeEnum.TARGET_STEP.getDefaultValue() : configurationDO.getValue();
                    }

                    // 设置是否达标
                    allDayDataDO.setCompliance(allDayDataDO.getStep() >= STEP_TARGET);

                    AllDayDataDO dayDataDO = AppDatabase.getInstance().getAllDayDataDAO().queryByDate(allDayDataDO.getDateTime());

                    if (Objects.isNull(dayDataDO)) {
                        AppDatabase.getInstance().getAllDayDataDAO().insert(allDayDataDO);
                    } else {
                        allDayDataDO.setId(dayDataDO.getId());
                        AppDatabase.getInstance().getAllDayDataDAO().update(allDayDataDO);
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    public List<Boolean> queryComplianceDays() {
        if (Objects.isNull(AppDatabase.getInstance())) {
            return Collections.emptyList();
        }

        return AppDatabase.getInstance().getAllDayDataDAO().queryAllCompliance();
    }
}
