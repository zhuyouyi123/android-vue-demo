package com.ble.blescansdk.db.helper;


import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.db.SdkDatabase;
import com.ble.blescansdk.db.dataobject.SecretKeyDO;

public class SecretKeyDAOHelper {

    public static void saveRecord(String secretKey) {
        if (StringUtils.isBlank(secretKey)) {
            return;
        }

        if (!SdkDatabase.supportDatabase) {
            return;
        }
        String address = SeekStandardDeviceHolder.getInstance().getAddress();

        if (StringUtils.isBlank(address)) {
            return;
        }

        SecretKeyDO secretKeyDO = SdkDatabase.getInstance().getSecretKeyDAO().queryFirst(address);

        if (null == secretKeyDO) {
            secretKeyDO = new SecretKeyDO();
            secretKeyDO.setAddress(address);
            secretKeyDO.setSecretKey(secretKey);
            secretKeyDO.setCreateTime(System.currentTimeMillis());
            SdkDatabase.getInstance().getSecretKeyDAO().insert(secretKeyDO);
        } else {
            secretKeyDO.setSecretKey(secretKey);
            SdkDatabase.getInstance().getSecretKeyDAO().update(secretKeyDO);
        }
    }

    public static String queryRecordByAddress(String address) {

        String secretKey = SeekStandardDeviceHolder.getInstance().getSecretKey();

        if (!SdkDatabase.supportDatabase) {
            return secretKey;
        }

        if (StringUtils.isBlank(address)) {
            return secretKey;
        }

        SecretKeyDO secretKeyDO = SdkDatabase.getInstance().getSecretKeyDAO().queryFirst(address);
        if (null == secretKeyDO) {
            return secretKey;
        }

        return secretKeyDO.getSecretKey();

    }
}
