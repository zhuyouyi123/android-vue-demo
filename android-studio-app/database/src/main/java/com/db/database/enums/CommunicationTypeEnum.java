package com.db.database.enums;

public enum CommunicationTypeEnum {

    SLEEP("02", 1, 24),
    STEP("04", 3, 8),
    CALORIE("05", 3, 8),
    HEART_RATE("07", 96, 3),
    BLOOD_OXYGEN("09", 2, 12),
    TEMPERATURE("0B", 6, 4),

    ;

    CommunicationTypeEnum(String type, int totalPacket, int packetSize) {
        this.type = type;
        this.totalPacket = totalPacket;
        this.packetSize = packetSize;
    }

    private final String type;
    private final int totalPacket;
    private final int packetSize;

    public String getType() {
        return type;
    }

    public int getTotalPacket() {
        return totalPacket;
    }

    public int getPacketSize() {
        return packetSize;
    }


    public static CommunicationTypeEnum getByType(String type) {
        CommunicationTypeEnum communicationTypeEnum = null;

        for (CommunicationTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                communicationTypeEnum = value;
                break;
            }
        }
        return communicationTypeEnum;
    }
}
