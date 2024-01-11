package com.panvan.app.receiver.call;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.db.database.daoobject.ConfigurationDO;
import com.db.database.enums.ConfigurationGroupEnum;
import com.db.database.enums.ConfigurationTypeEnum;
import com.db.database.service.ConfigurationDataService;
import com.panvan.app.Config;
import com.panvan.app.data.enums.PermissionTypeEnum;
import com.panvan.app.service.PermissionService;
import com.panvan.app.utils.DataConvertUtil;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.SdkUtil;
import com.panvan.app.utils.StringUtils;

import java.nio.charset.StandardCharsets;
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
                    // Toast.makeText(Config.mainContext, "来电号码：" + (inCallContactsEnable ? phoneNumber : "不显示来电号码"), Toast.LENGTH_SHORT).show();
                    byte[] nameBytes = new byte[0];
                    try {
                        if (inCallContactsEnable) {
                            String contactNameFromNumber = getContactNameFromNumber(Config.mainContext, phoneNumber);
                            LogUtil.error("写入指令 名称：" + contactNameFromNumber);
                            String cleanedText = "未知号码";
                            if (StringUtils.isNotBlank(contactNameFromNumber)) {
                                // 使用正则表达式只保留字母、数字和汉字
                                cleanedText = contactNameFromNumber.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5]", "");
                            }

                            nameBytes = cleanedText.getBytes(StandardCharsets.UTF_8);
                        }
                        start(phoneNumber, nameBytes);
                    } catch (Exception e) {
                        start(phoneNumber, nameBytes);
                    }
                }
            }

            @Override
            protected void onStateOffhook(int state, String phoneNumber) {
                if (inCallEnable) {
                    // Toast.makeText(Config.mainContext, "挂断号码：" + phoneNumber, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onStateIdle(int state, String phoneNumber) {
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                if (inCallEnable) {
                    // Toast.makeText(Config.mainContext, "挂断：" + phoneNumber, Toast.LENGTH_SHORT).show();
                    stop();
                }
            }
        };
    }

    private void start(String phoneNumber, byte[] nameBytes) {
        if (StringUtils.isBlank(phoneNumber)) {
            return;
        }
        byte[] bytes = new byte[]{0x68, 0x01};
        StringBuilder hexStr = new StringBuilder(ProtocolUtil.byteArrToHexStr(phoneNumber.getBytes()));
        int diffSize = 15 - phoneNumber.length();
        for (int i = 0; i < diffSize; i++) {
            hexStr.append("00");
        }

        int length = 16 + nameBytes.length;
        byte[] lenBytes = ProtocolUtil.intToByteArrayTwo(length);

        byte[] newLenBytes = new byte[]{lenBytes[1], 0x00};

        String string = ProtocolUtil.byteArrToHexStr(DataConvertUtil.mergeBytes(bytes, newLenBytes));
        string = string + "00" + hexStr + ProtocolUtil.byteArrToHexStr(nameBytes);
        byte calcAddSum = ProtocolUtil.calcAddSum(ProtocolUtil.hexStrToBytes(string));
        string += ProtocolUtil.byteToHexStr(calcAddSum);
        string += "16";

        SdkUtil.writeCommand(string);
        // SdkUtil.writeCommand("6801160000313336353638393837343500000000E5BCA0E4B8893316");
    }

    private void stop() {
        SdkUtil.writeCommand("68010100016B16");
    }

    @SuppressLint("Range")
    public String getContactNameFromNumber(Context context, String number) {
        String name = null;
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            cursor.close();
        }
        return name;
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
