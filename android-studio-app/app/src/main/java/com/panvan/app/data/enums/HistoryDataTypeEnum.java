package com.panvan.app.data.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.panvan.app.utils.DateUtil;
import com.panvan.app.utils.HistoryDataAnalysisUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public enum HistoryDataTypeEnum {

    /**
     * 全天总数据
     */
    TOTAL_DATA(1, 0x00, "00", false) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisTotalData(bytes);
        }
    },
    /**
     * 全天运动情况(频率1小时)-类型0x01
     */
    SPORT(1, 0x01, "01", false) {
        @Override
        public String[] analysis(byte[] bytes) {
            return new String[0];
        }
    },

    /**
     * 全天睡眠情况-类型0x02
     */
    SLEEP(1, 0x02, "02", false) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryTenMinuteData(bytes, SLEEP, 10 * 60, 2);
        }
    },

    /**
     * 全天计步详情(频率5分钟)-类型0x04
     */
    STEP(3, 0x04, "04", true) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, STEP, 5 * 60, 2);
        }
    },

    /**
     * 全天热量卡路里详情(频率5分钟)-类型0x05
     */
    CALORIE(3, 0X05, "05", true) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, CALORIE, 5 * 60, 2);
        }
    },

    /**
     * 全天血氧(频率5分钟)-类型0x09
     */
    BLOOD_OXYGEN(2, 0x09, "09", true) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, BLOOD_OXYGEN, 300, 1);
        }
    },

    /**
     * 全天温度(频率5分钟)-类型0x0B
     */
    TEMPERATURE(6, 0x0B, "0B", true) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, TEMPERATURE, 300, 4);
        }
    },

    /**
     * 全天大气压(频率10分钟)-类型0x0C
     */
    ATMOSPHERIC_PRESSURE(3, 0x0C, "0C", false) {
        @Override
        public String[] analysis(byte[] bytes) {
            return new String[0];
        }
    },

    /**
     * 全天血压数据(频率5分钟)-类型0x0E
     */
    BLOOD_PRESSURE(6, 0x0E, "0E", true) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, BLOOD_PRESSURE, 5 * 60, 3);
        }
    },

    /**
     * 全天心率(频率5秒)-类型0x07
     */
    HEART_RATE(96, 0x07, "07", true) {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisMinuteData(bytes, 5, 5 * 60, 40);
        }
    },
    ;


    HistoryDataTypeEnum(int totalPacket, int key, String keyDes, boolean enable) {
        this.totalPacket = totalPacket;
        this.key = key;
        this.keyDes = keyDes;
        this.enable = enable;
    }

    private final int totalPacket;

    private final int key;

    private final String keyDes;

    private final boolean enable;

    public abstract String[] analysis(byte[] bytes);


    public static List<String> getInstructions(HistoryDataTypeEnum typeEnum, String dateHex) {
        List<String> ins = new ArrayList<>();

        StringBuilder hex;

        for (int i = 0; i < typeEnum.getTotalPacket(); i++) {
            // 包头 68 功能码 17 数据长度 0600
            hex = new StringBuilder("68170600");
            // 年月日
            hex.append(dateHex);
            // 包类型
            hex.append(typeEnum.getKeyDes());
            // 总包数
            hex.append(String.format(Locale.ENGLISH, "%02X", typeEnum.getTotalPacket()));
            // 包序号
            hex.append(String.format(Locale.ENGLISH, "%02X", i + 1));
            String toHexStr = ProtocolUtil.byteArrToHexStr(ProtocolUtil.addSumBytes(ProtocolUtil.hexStrToBytes(hex.toString())));

            ins.add(toHexStr + "16");

            if (DateUtil.checkSortStop(typeEnum.getTotalPacket(),i + 1)){
                break;
            }
        }

        return ins;
    }

    public static HistoryDataTypeEnum getType(byte b) {
        HistoryDataTypeEnum typeEnum = null;

        for (HistoryDataTypeEnum value : values()) {
            if (value.getKey() == b) {
                typeEnum = value;
                break;
            }
        }
        return typeEnum;
    }

    public static HistoryDataTypeEnum getType(String type) {
        HistoryDataTypeEnum typeEnum = null;

        for (HistoryDataTypeEnum value : values()) {
            if (Objects.equals(value.getKeyDes(), type)) {
                typeEnum = value;
                break;
            }
        }
        return typeEnum;
    }

    public static List<HistoryDataTypeEnum> getAllEnable() {
        List<HistoryDataTypeEnum> list = new ArrayList<>();
        for (HistoryDataTypeEnum value : values()) {
            if (value.isEnable()) {
                list.add(value);
            }
        }
        return list;
    }

    public int getTotalPacket() {
        return totalPacket;
    }

    public int getKey() {
        return key;
    }

    public String getKeyDes() {
        return keyDes;
    }

    public boolean isEnable() {
        return enable;
    }
}
