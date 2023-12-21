package com.seekcy.bracelet.receiver.call;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.db.database.daoobject.ConfigurationDO;
import com.db.database.enums.ConfigurationGroupEnum;
import com.db.database.enums.ConfigurationTypeEnum;
import com.db.database.service.ConfigurationDataService;
import com.seekcy.bracelet.Config;
import com.seekcy.bracelet.data.enums.PermissionTypeEnum;
import com.seekcy.bracelet.service.PermissionService;
import com.seekcy.bracelet.utils.DataConvertUtil;
import com.seekcy.bracelet.utils.SdkUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CallViewModel {
    private CallModel mCallModel;

    private static boolean isFirst = true;

    private static boolean inCallEnable = false;
    private static boolean inCallContactsEnable = false;

    /**
     * 通知默认MAP
     */
    private static final Map<String, Boolean> NOTIFICATION_CONFIG_MAP = new HashMap<>();

    private static CallViewModel INSTANCE = null;

    public static CallViewModel getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new CallViewModel();
        }
        return INSTANCE;
    }


    public void listenNone() {
        if (Objects.nonNull(mCallModel)) {
            mCallModel.listenNone();
        }
    }

    public void init() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Config.mainContext.getSystemService(Context.TELECOM_SERVICE);
        }

        if (Objects.nonNull(mCallModel)) {
            return;
        }

        mCallModel = new CallModel((TelephonyManager) Config.mainContext.getSystemService(Context.TELEPHONY_SERVICE)) {
            @Override
            protected void onStateRinging(int state, String phoneNumber) {
                if (inCallEnable) {
                    Toast.makeText(Config.mainContext, "来电号码：" + (inCallContactsEnable ? phoneNumber : "不显示来电号码"), Toast.LENGTH_SHORT).show();
                    start("");
                }
            }

            @Override
            protected void onStateOffhook(int state, String phoneNumber) {
                if (inCallEnable) {
                    Toast.makeText(Config.mainContext, "挂断号码：" + phoneNumber, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onStateIdle(int state, String phoneNumber) {
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                if (inCallEnable) {
                    Toast.makeText(Config.mainContext, "挂断：" + phoneNumber, Toast.LENGTH_SHORT).show();
                    stop();
                }
            }
        };
    }

    private void start(String phoneNumber) {
        byte[] bytes = new byte[]{0x68, 0x01};
        String hexStr = ProtocolUtil.byteArrToHexStr("17626525183".getBytes());
        hexStr += "000000000000";
        hexStr += "313131";
        byte[] lenBytes = ProtocolUtil.intToByteArrayTwo(hexStr.length() + 2);

        byte[] mergeLenBytes = DataConvertUtil.mergeBytes(bytes, lenBytes);

        byte[] contentBytes = ProtocolUtil.hexStrToBytes(hexStr);
        byte[] mergeBytes = DataConvertUtil.mergeBytes(mergeLenBytes, contentBytes);

        // SdkUtil.writeCommand(ProtocolUtil.byteArrToHexStr(mergeBytes) + ProtocolUtil.byteToHexStr(ProtocolUtil.calcAddSum(mergeBytes)) + "16");
        SdkUtil.writeCommand("6801160000313336353638393837343500000000E5BCA0E4B8893316");
    }

    private void stop() {
        SdkUtil.writeCommand("68010100016B16");
    }

    public void loadConfig() {
        // 检查是否有权限
        if (!PermissionService.getInstance().queryPermissionExist(PermissionTypeEnum.READ_PHONE_STATE)) {
            return;
        }
        List<ConfigurationTypeEnum> typeEnumList = ConfigurationTypeEnum.getByGroup(ConfigurationGroupEnum.NOTIFICATION);

        List<ConfigurationDO> list = ConfigurationDataService.getInstance().queryByGroup(ConfigurationGroupEnum.NOTIFICATION.getName());
        if (CollectionUtils.isNotEmpty(list)) {
            for (ConfigurationDO configurationDO : list) {
                NOTIFICATION_CONFIG_MAP.put(configurationDO.getType(), configurationDO.getValue() == 1);
            }
        }

        for (ConfigurationTypeEnum configurationTypeEnum : typeEnumList) {
            if (!NOTIFICATION_CONFIG_MAP.containsKey(configurationTypeEnum.getType())) {
                NOTIFICATION_CONFIG_MAP.put(configurationTypeEnum.getType(), configurationTypeEnum.getDefaultValue() == 1);
            }
        }

        inCallEnable = Boolean.TRUE.equals(NOTIFICATION_CONFIG_MAP.get(ConfigurationTypeEnum.NOTIFICATION_IN_CALL.getType()));
        inCallContactsEnable = Boolean.TRUE.equals(NOTIFICATION_CONFIG_MAP.get(ConfigurationTypeEnum.NOTIFICATION_IN_CALL_CONTACTS.getType()));
        if (inCallEnable) {
            init();
        }
    }

    public void loadConfig(ConfigurationTypeEnum type, boolean enable) {

        if (Objects.isNull(mCallModel)) {
            loadConfig();
            return;
        }

        switch (type) {
            case NOTIFICATION_IN_CALL:
                inCallEnable = enable;
                break;
            case NOTIFICATION_IN_CALL_CONTACTS:
                inCallContactsEnable = enable;
                break;
        }
    }
}
