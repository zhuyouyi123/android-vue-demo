package com.panvan.app.data.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.panvan.app.utils.HistoryDataAnalysisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public enum HistoryDataTypeEnum {

    /**
     * 全天总数据
     */
    TOTAL_DATA(1, 0x00, "00") {
        @Override
        public String[] analysis(byte[] bytes) {
            return new String[0];
        }
    },
    /**
     * 全天运动情况(频率1小时)-类型0x01
     */
    // SPORT(1, 0x01, "01"),

    /**
     * 全天睡眠情况-类型0x02
     */
    SLEEP(1, 0x02, "02") {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryTenMinuteData(bytes, SLEEP, 10 * 60, 2);
        }
    },

    /**
     * 全天计步详情(频率5分钟)-类型0x04
     */
    STEP(3, 0x04, "04") {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, STEP, 5 * 60, 2);
        }
    },

    /**
     * 全天热量卡路里详情(频率5分钟)-类型0x05
     */
    CALORIE(3, 0X05, "05") {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, CALORIE, 5 * 60, 2);
        }
    },

    /**
     * 全天血氧(频率5分钟)-类型0x09
     */
    BLOOD_OXYGEN(2, 0x09, "09") {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, BLOOD_OXYGEN, 300, 1);
        }
    },

    /**
     * 全天温度(频率5分钟)-类型0x0B
     */
    TEMPERATURE(6, 0x0B, "0B") {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisEveryHourData(bytes, TEMPERATURE, 300, 4);
        }
    },
    //
    // /**
    //  * 全天大气压(频率10分钟)-类型0x0C
    //  */
    // ATMOSPHERIC_PRESSURE(3, 0x0C, "0C") {
    //     @Override
    //     public boolean checkDataComplete(String date, int sort) {
    //         if (DateUtils.isSameDay(date)) {
    //             return DateUtil.isBeforeNow(DateUtil.getTimestamp(sort * 8));
    //         }
    //         return true;
    //     }
    // },
    //
    /**
     * 全天血压数据(频率5分钟)-类型0x0E
     */
    BLOOD_PRESSURE(6, 0x0E, "0E") {
        @Override
        public String[] analysis(byte[] bytes) {
            return new String[0];
        }
    },

    /**
     * 全天心率(频率5秒)-类型0x07
     */
    HEART_RATE(96, 0x07, "07") {
        @Override
        public String[] analysis(byte[] bytes) {
            return HistoryDataAnalysisUtil.analysisMinuteData(bytes, 5, 5 * 60);
        }
    },
    ;


    private static final String TAG = HistoryDataTypeEnum.class.getSimpleName();

    HistoryDataTypeEnum(int totalPacket, int key, String keyDes) {
        this.totalPacket = totalPacket;
        this.keyDes = keyDes;
        this.key = key;
    }

    private final int totalPacket;

    private final int key;

    private final String keyDes;

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

    public int getTotalPacket() {
        return totalPacket;
    }

    public int getKey() {
        return key;
    }

    public String getKeyDes() {
        return keyDes;
    }

}
