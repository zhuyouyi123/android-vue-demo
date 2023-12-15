package com.panvan.app.Receiver.call;

import android.content.Context;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.db.database.daoobject.ConfigurationDO;
import com.db.database.enums.ConfigurationGroupEnum;
import com.db.database.enums.ConfigurationTypeEnum;
import com.db.database.service.ConfigurationDataService;
import com.panvan.app.Config;
import com.panvan.app.data.enums.PermissionTypeEnum;
import com.panvan.app.service.PermissionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CallViewModel {
    private CallModel mCallModel;

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
                if (inCallEnable) {
                    Toast.makeText(Config.mainContext, "挂断空闲：" + phoneNumber, Toast.LENGTH_SHORT).show();
                }
            }
        };
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