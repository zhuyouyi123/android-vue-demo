package com.seekcy.bracelet.data.enums;

import com.ble.blescansdk.ble.utils.ProtocolUtil;

public enum NotificationTypeEnum {

    MMS(0x00, "MMS", "短信消息", "com.android.mms"),
    MMS_SERVICE(0x00, "MMS", "短信消息", "com.android.mms.service"),
    HONOR_MMS(0x00, "HONOR_MMS", "荣耀短信消息", "com.hihonor.mms"),
    WX(0x01, "WX", "微信消息", "com.tencent.mm"),
    QQ(0x02, "QQ", "QQ消息", "com.tencent.mobileqq"),
    OTHERS(0xFE, "其他消息提醒", "消息提醒", "others"),

    IN_CALL(0xFF, "来电", "来电提醒", "com.android.incallui"),
    ;

    private final int type;
    private final String name;
    private final String title;
    private final String packetName;

    NotificationTypeEnum(int type, String name, String title, String packetName) {
        this.type = type;
        this.name = name;
        this.title = title;
        this.packetName = packetName;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getPacketName() {
        return packetName;
    }

    public static NotificationTypeEnum getPacket(String packetName) {
        NotificationTypeEnum typeEnum = null;

        for (NotificationTypeEnum value : values()) {
            if (value.getPacketName().equals(packetName)) {
                typeEnum = value;
                break;
            }
        }

        return typeEnum;
    }

    public static String getCommand(NotificationTypeEnum typeEnum, String content) {
        StringBuilder stringBuilder = new StringBuilder("680B");

        int totalLength = 3;
        if (content.length() > 80) {
            content = content.substring(0, 80) + "...";
        }
        byte[] contentBytes = content.getBytes();
        totalLength += contentBytes.length;

        if (null == typeEnum) {
            return "";
        }

        String title = typeEnum.getTitle();
        if (title.length() > 25) {
            title = title.substring(0, 25) + "...";
        }
        byte[] titleBytes = title.getBytes();
        totalLength += titleBytes.length;
        // 增加长度
        byte[] totalLengthBytes = new byte[2];
        totalLengthBytes[0] = (byte) (totalLength & 0xff);
        totalLengthBytes[1] = (byte) ((totalLength >> 8) & 0xff);
        stringBuilder.append(String.format("%02X%02X", totalLengthBytes[0], totalLengthBytes[1]));
        // 增加类型
        stringBuilder.append(ProtocolUtil.byteToHexStr((byte) typeEnum.getType()));

        // title 长度和内容
        stringBuilder.append(String.format("%02X", titleBytes.length));
        stringBuilder.append(ProtocolUtil.byteArrToHexStr(titleBytes));
        // 内容长度
        stringBuilder.append(String.format("%02X", contentBytes.length));
        stringBuilder.append(ProtocolUtil.byteArrToHexStr(contentBytes));

        byte addSum = ProtocolUtil.calcAddSum(ProtocolUtil.hexStrToBytes(stringBuilder.toString()));
        return stringBuilder.append(ProtocolUtil.byteToHexStr(addSum)).append("16").toString();
    }
}
