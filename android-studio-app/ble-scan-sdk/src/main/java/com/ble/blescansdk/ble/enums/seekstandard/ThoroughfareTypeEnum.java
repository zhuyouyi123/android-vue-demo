package com.ble.blescansdk.ble.enums.seekstandard;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.entity.seek.StandardThoroughfareInfo;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.Acc;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.IBeacon;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.Info;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.Line;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.Quuppa;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.TLM;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.UID;
import com.ble.blescansdk.ble.entity.seek.thoroughfare.URL;
import com.ble.blescansdk.ble.enums.EddystoneUrlPrefixEnum;
import com.ble.blescansdk.ble.enums.EddystoneUrlSuffixEnum;
import com.ble.blescansdk.ble.utils.AsciiUtil;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.ble.utils.TimeUtil;

import java.util.Arrays;
import java.util.Optional;

public enum ThoroughfareTypeEnum {

    I_BEACON("4C00", null, "iBeacon", "1") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 9 : 6;
            String uuid = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 16);
            startByte = startByte + 16;
            int major = ((scanBytes[startByte] & 0x00ff) << 8) + (scanBytes[startByte + 1] & 0x00ff);
            startByte = startByte + 2;
            int minor = ((scanBytes[startByte] & 0x00ff) << 8) + (scanBytes[startByte + 1] & 0x00ff);
            startByte = startByte + 2;
            int measurePower = (scanBytes[startByte] & 0x00ff) - 0xFF - 1;

            String name = AsciiUtil.convertHexToString(ProtocolUtil.analysisByStartByte(scanBytes, scanBytes.length - 8, 6));
            if (StringUtils.isBlank(name.trim())) {
                name = "";
            } else {
                int battery = scanBytes[scanBytes.length - 21] & 0xff;
                standardThoroughfareInfo.setBattery(battery);
            }

            return standardThoroughfareInfo.addBeacon(
                            new IBeacon(I_BEACON.getValue())
                                    .setUuid(uuid)
                                    .setMajor(major)
                                    .setMinor(minor)
                                    .setMeasurePower(measurePower))
                    .setDeviceName(name.trim());
        }
    },

    EDDYSTONE_UID("AAFE", "00", "UID", "2") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 12 : 9;
            int measurePower = (scanBytes[startByte] & 0x00ff) - 0xFF - 1;
            startByte = startByte + 1;
            String namespaceId = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 10);
            startByte = startByte + 10;
            String instanceId = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 6);
            return standardThoroughfareInfo.addUid(new UID(EDDYSTONE_UID.getValue())
                    .setInstanceId(instanceId)
                    .setNamespaceId(namespaceId)
                    .setMeasurePower(measurePower));
        }
    },

    EDDYSTONE_URL("AAFE", "10", "URL", "3") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 8 : 5;
            // 数据长度
            int length = scanBytes[startByte] & 0xff;
            startByte = startByte + 4;
            int measurePower = (scanBytes[startByte] & 0x00ff) - 0xFF - 1;
            startByte++;
            String url = "";
            url += EddystoneUrlPrefixEnum.getByCode(ProtocolUtil.byteToHexStr(scanBytes[startByte]));
            startByte++;
            int diff = length - (startByte - 7);
            url += AsciiUtil.convertHexToString(ProtocolUtil.analysisByStartByte(scanBytes, startByte, diff)).trim();
            url += EddystoneUrlSuffixEnum.getByCode(ProtocolUtil.byteToHexStr(scanBytes[length-3]));
            return standardThoroughfareInfo
                    .addUrl(new URL(EDDYSTONE_URL.getValue())
                            .setLink(url)
                            .setMeasurePower(measurePower));
        }
    },

    EDDYSTONE_TLM("AAFE", "20", "TLM", "4") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 13 : 10;
            // 电压
            byte[] copyBytes = ProtocolUtil.copyBytes(scanBytes, startByte, 2);
            short voltage = ProtocolUtil.bytes2LenToShort(copyBytes);
            startByte = startByte + 2;
            // 温度
            String temperature = (scanBytes[startByte] & 0xFF) + "." + (scanBytes[startByte + 1] & 0xFF);
            startByte = startByte + 2;
            byte[] pduCountBytes = ProtocolUtil.copyBytes(scanBytes, startByte, 4);
            long pduCount = ProtocolUtil.bytes4LenToLong(pduCountBytes);
            startByte = startByte + 4;
            byte[] wordTimeBytes = ProtocolUtil.copyBytes(scanBytes, startByte, 4);
            String workTime = TimeUtil.convertTimeToDay(ProtocolUtil.bytes4LenToLong(wordTimeBytes));

            return standardThoroughfareInfo.setTlm(new TLM(EDDYSTONE_TLM.getValue())
                    .setPduCount(pduCount)
                    .setTemperature(temperature)
                    .setWorkTime(workTime)
                    .setVoltage(voltage)
            );
        }
    },


    ACC("1918", null, "ACC", "5") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 13 : 10;
            int battery = scanBytes[startByte] & 0xFF;
            startByte++;
            // xAxis
            double xAxis = ProtocolUtil.convertCharToShort((char) scanBytes[startByte]);
            startByte = startByte + 2;
            // yAxis
            double yAxis = ProtocolUtil.convertCharToShort((char) scanBytes[startByte]);
            startByte = startByte + 2;
            // zAxis
            double zAxis = ProtocolUtil.convertCharToShort((char) scanBytes[startByte]);

            return standardThoroughfareInfo.setAcc(new Acc(ACC.getValue())
                            .setxAxis(xAxis)
                            .setyAxis(yAxis)
                            .setzAxis(zAxis))
                    .setBattery(battery);
        }
    },

    INFO("E1FF", null, "DeviceInfo", "6") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 13 : 10;
            int battery = scanBytes[startByte] & 0xFF;
            startByte++;
            String mac = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 6).toUpperCase();
            startByte = startByte + 8;
            String name = AsciiUtil.convertHexToString(ProtocolUtil.analysisByStartByte(scanBytes, startByte, 6));

            return standardThoroughfareInfo
                    .setInfo(new Info(INFO.getValue())
                            .setMac(mac))
                    .setDeviceName(name)
                    .setBattery(battery);
        }
    },

    LINE("6FFE", null, "LINE", "7") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 12 : 9;
            String hwid = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 5);
            startByte = startByte + 5;
            int measurePower = (scanBytes[startByte++] & 0xFF) - 0xFF - 1;
            String secureMessage = ProtocolUtil.analysisByStartByte(scanBytes, startByte, 4);
            startByte = startByte + 4;
            short timeStamp = ProtocolUtil.bytes2LenToShort(ProtocolUtil.copyBytes(scanBytes, startByte, 2));
            startByte = startByte + 2;
            int battery = scanBytes[startByte] & 0xFF;
            return standardThoroughfareInfo.setLine(new Line(LINE.getValue())
                    .setHwid(hwid)
                    .setMeasurePower(measurePower)
                    .setSecureMessage(secureMessage)
                    .setTimesTamp(timeStamp)
            ).setBattery(battery);
        }
    },

    CORE_IOT_AOA("0D00", null, "Coreaiot", "8") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {

            return standardThoroughfareInfo;
        }
    },
    QUUPPA_AOA("C700", null, "Quuppa", "9") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            int startByte = isConnectable ? 10 : 7;
            return standardThoroughfareInfo.setQuuppa(new Quuppa(QUUPPA_AOA.getValue()).setTagId(ProtocolUtil.analysisByStartByte(scanBytes, startByte, 6)));
        }
    },

    EMPTY("EMPTY", null, "EMPTY", "0") {
        @Override
        public StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable) {
            return standardThoroughfareInfo;
        }
    },
    ;

    ThoroughfareTypeEnum(String identity, String childIdentity, String value, String type) {
        this.identity = identity;
        this.childIdentity = childIdentity;
        this.value = value;
        this.type = type;
    }

    private final String identity;
    private final String childIdentity;
    private final String value;
    private final String type;

    public abstract StandardThoroughfareInfo analysis(byte[] scanBytes, StandardThoroughfareInfo standardThoroughfareInfo, boolean isConnectable);

    public String getIdentity() {
        return identity;
    }

    public String getValue() {
        return value;
    }

    public String getChildIdentity() {
        return childIdentity;
    }

    public String getType() {
        return type;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<ThoroughfareTypeEnum> getByType(String type) {
        if (StringUtils.isBlank(type)) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(e -> e.getType().equals(type)).findFirst();

    }

    public static ThoroughfareTypeEnum getByType(String identity, String childIdentity) {
        if (StringUtils.isBlank(identity)) {
            return EMPTY;
        }

        ThoroughfareTypeEnum type = EMPTY;

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

    public static ThoroughfareTypeEnum getByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return EMPTY;
        }
        ThoroughfareTypeEnum typeEnum = EMPTY;

        for (ThoroughfareTypeEnum thoroughfareTypeEnum : values()) {
            if (thoroughfareTypeEnum.getValue().equals(value)) {
                typeEnum = thoroughfareTypeEnum;
                break;
            }
        }
        return typeEnum;
    }


}
