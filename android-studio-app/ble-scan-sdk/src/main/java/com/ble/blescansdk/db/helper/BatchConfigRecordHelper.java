package com.ble.blescansdk.db.helper;

import com.ble.blescansdk.ble.utils.BleLogUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.db.SdkDatabase;
import com.ble.blescansdk.db.dataobject.BatchConfigRecordDO;
import com.ble.blescansdk.db.enums.BatchConfigResultEnum;
import com.ble.blescansdk.db.enums.BatchConfigTypeEnum;

import java.util.List;

public class BatchConfigRecordHelper {

    public static void save(String address, BatchConfigResultEnum resultEnum, BatchConfigTypeEnum typeEnum, int errorCode) {
        if (!SdkDatabase.supportDatabase) {
            return;
        }

        if (StringUtils.isBlank(address)) {
            return;
        }

        final BatchConfigRecordDO batchConfigRecordDO = SdkDatabase.getInstance().getBatchConfigRecordDAO().queryOne(address);

        BleLogUtil.i("批量升级，保存记录:" + address + "result:" + resultEnum.getType());

        if (null == batchConfigRecordDO) {
            BatchConfigRecordDO recordDO = new BatchConfigRecordDO();
            recordDO.setType(typeEnum.getCode());
            recordDO.setResult(resultEnum.getCode());
            recordDO.setAddress(address);
            recordDO.setFailReason(errorCode);
            SdkDatabase.getInstance().getBatchConfigRecordDAO().insert(recordDO);
        } else {
            batchConfigRecordDO.setType(typeEnum.getCode());
            batchConfigRecordDO.setResult(resultEnum.getCode());
            batchConfigRecordDO.setFailReason(errorCode);
            SdkDatabase.getInstance().getBatchConfigRecordDAO().update(batchConfigRecordDO);
        }


    }

    public static void deleteByType(BatchConfigTypeEnum typeEnum) {
        SdkDatabase.getInstance().getBatchConfigRecordDAO().deleteByType(typeEnum.getCode());
    }

    public static List<BatchConfigRecordDO> queryByResult(int code) {
        return SdkDatabase.getInstance().getBatchConfigRecordDAO().listByResult(code);
    }

    public static List<BatchConfigRecordDO> queryByType(int type) {
        return SdkDatabase.getInstance().getBatchConfigRecordDAO().listByType(type);
    }
}
