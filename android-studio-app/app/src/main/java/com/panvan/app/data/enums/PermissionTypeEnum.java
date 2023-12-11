package com.panvan.app.data.enums;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.panvan.app.data.constants.PermissionsRequestConstants;
import com.panvan.app.utils.StringUtils;

import java.util.Arrays;

public enum PermissionTypeEnum {
    READ_CONTACTS(Manifest.permission.READ_CONTACTS, "READ_CONTACTS", "读取联系人", PermissionsRequestConstants.READ_CONTACTS_PERMISSION_REQUEST_CODE),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, "READ_PHONE_STATE", "监听来电", PermissionsRequestConstants.READ_PHONE_STATE_PERMISSION_REQUEST_CODE),
    READ_CALL_LOG(Manifest.permission.READ_CALL_LOG, "READ_CALL_LOG", "读取通话记录", PermissionsRequestConstants.READ_CALL_LOG_PERMISSION_REQUEST_CODE),
    NOTIFICATION("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS", "NOTIFICATION", "通知", PermissionsRequestConstants.OTHER_REQUEST_CODE),
    ;

    private final String name;
    private final String type;
    private final String desc;
    private final int requestCode;

    PermissionTypeEnum(String name, String type, String desc, int requestCode) {
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.requestCode = requestCode;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public int getRequestCode() {
        return requestCode;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static PermissionTypeEnum getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        return Arrays.stream(values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }
}
