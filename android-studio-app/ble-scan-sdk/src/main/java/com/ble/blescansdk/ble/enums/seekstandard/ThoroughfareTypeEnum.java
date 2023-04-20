package com.ble.blescansdk.ble.enums.seekstandard;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.entity.seek.StandardThoroughfareInfo;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum ThoroughfareTypeEnum {

    I_BEACON("4C00", null, "I_BEACON") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            int startByte = 9;
            String uuid = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 16);
            startByte = startByte + 16;
            int major = ((scanBytes[startByte] & 0x00ff) << 8) + (scanBytes[startByte + 1] & 0x00ff);
            startByte = startByte + 2;
            int minor = ((scanBytes[startByte] & 0x00ff) << 8) + (scanBytes[startByte + 1] & 0x00ff);
            startByte = startByte + 2;
            int measurePower = scanBytes[startByte] & 0x00ff;
            return new StandardThoroughfareInfo(ThoroughfareTypeEnum.I_BEACON.getValue())
                    .setUuid(uuid)
                    .setMajor(major)
                    .setMinor(minor)
                    .setMeasurePower(measurePower)
                    ;
        }
    },
    CORE_IOT_AOA("0D00", null, "CORE_IOT_AOA") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return null;
        }
    },
    QUUPPA_AOA("C700", null, "QUUPPA_AOA") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return null;
        }
    },

    EDDYSTONE_UID("0AFE", "00", "EDDYSTONE_UID") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return null;
        }
    },

    EDDYSTONE_URL("0AFE", "10", "EDDYSTONE_URL") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return null;
        }
    },

    EDDYSTONE_TLM("0AFE", "20", "EDDYSTONE_TLM") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return null;
        }
    },

    LINE("6FFE", null, "LINE") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return new StandardThoroughfareInfo(ThoroughfareTypeEnum.ACC.getIdentity());
        }
    },


    ACC("1918", null, "ACC") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return new StandardThoroughfareInfo(ThoroughfareTypeEnum.ACC.getIdentity());
        }
    },

    INFO("E1FF", null, "INFO") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes) {
            return new StandardThoroughfareInfo(ThoroughfareTypeEnum.ACC.getIdentity());
        }
    },
    ;

    ThoroughfareTypeEnum(String identity, String childIdentity, String value) {
        this.identity = identity;
        this.childIdentity = childIdentity;
        this.value = value;
    }

    private final String identity;
    private final String childIdentity;
    private final String value;

    public abstract StandardThoroughfareInfo analysis(byte[] scanBytes);

    public String getIdentity() {
        return identity;
    }

    public String getValue() {
        return value;
    }

    public String getChildIdentity() {
        return childIdentity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<ThoroughfareTypeEnum> getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return Optional.empty();
        }

        return Arrays.stream(values()).filter(e -> e.getIdentity().equals(type)).findFirst();

    }

    public static ThoroughfareTypeEnum getByType(String identity, String childIdentity) {
        if (StringUtils.isBlank(identity)) {
            return null;
        }

        ThoroughfareTypeEnum type = null;

        for (ThoroughfareTypeEnum typeEnum : values()) {
            if (typeEnum.getIdentity().equals(identity)) {
                if (!EDDYSTONE_UID.getIdentity().equals(identity)) {
                    type = typeEnum;
                } else {
                    if (EDDYSTONE_UID.getChildIdentity().equals(childIdentity)) {
                        type = EDDYSTONE_UID;
                    } else if (EDDYSTONE_URL.getChildIdentity().equals(childIdentity)) {
                        type = EDDYSTONE_URL;
                    } else if (EDDYSTONE_TLM.getChildIdentity().equals(childIdentity)) {
                        type = EDDYSTONE_TLM;
                    }
                }
                break;
            }
        }
        return type;
    }


}
