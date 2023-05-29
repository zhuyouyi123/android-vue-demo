package com.ble.blescansdk.db.helper;

import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.db.SdkDatabase;
import com.ble.blescansdk.db.dataobject.BatchConfigRecordDO;
import com.ble.blescansdk.db.enums.BatchConfigResultEnum;
import com.ble.blescansdk.db.enums.BatchConfigTypeEnum;

import java.util.List;

public class BatchConfigRecordHelper {

    public static void save(String address,BatchConfigResultEnum resultEnum, BatchConfigTypeEnum typeEnum, int errorCode) {
        if (!SdkDatabase.supportDatabase) {
            return;
        }

        if (StringUtils.isBlank(address)) {
            return;
        }

        BatchConfigRecordDO recordDO = new BatchConfigRecordDO();

        recordDO.setType(typeEnum.getCode());
        recordDO.setResult(resultEnum.getCode());
        recordDO.setAddress(address);
        recordDO.setFailReason(errorCode);

        SdkDatabase.getInstance().getBatchConfigRecordDAO().insert(recordDO);
    }

    public static void deleteByType(BatchConfigTypeEnum typeEnum) {
        SdkDatabase.getInstance().getBatchConfigRecordDAO().deleteByType(typeEnum.getCode());
    }

    public static List<BatchConfigRecordDO> queryByResult(int code) {
        return SdkDatabase.getInstance().getBatchConfigRecordDAO().listByResult(code);
    }
}
