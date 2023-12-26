package com.panvan.app.data.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.db.database.service.CommunicationDataService;
import com.db.database.service.DeviceDataService;
import com.db.database.utils.DataConvertUtils;
import com.db.database.utils.DateUtils;
import com.panvan.app.callback.AgreementCallback;
import com.panvan.app.data.constants.JsBridgeConstants;
import com.panvan.app.data.holder.DeviceHolder;
import com.panvan.app.scheduled.CommandRetryScheduled;
import com.panvan.app.service.CommunicationService;
import com.panvan.app.utils.DataConvertUtil;
import com.panvan.app.utils.DateUtil;
import com.panvan.app.utils.JsBridgeUtil;
import com.panvan.app.utils.LogUtil;
import com.panvan.app.utils.StringUtils;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public enum AgreementEnum {

    FUNCTION_SWITCH(0X02, 0X82, "FUNCTION_SWITCH") {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            CommandRetryScheduled.getInstance().remove("02");
            CommunicationService.getInstance().pushFunctionSwitchInfo(bytes);
            callback.success(FUNCTION_SWITCH);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            return ProtocolUtil.hexStrToBytes("680205000007080B0C9516");
        }
    },

    /**
     * 电量结果
     */
    BATTERY(0x03, 0x83, "BATTERY") {
        @Override
        public void responseHandle(byte[] data, AgreementCallback callback) {
            // 68830100645016
            int battery = -1;
            if (Objects.nonNull(data) && data.length == 7) {
                battery = data[4] & 0xff;
            }

            DeviceHolder.getInstance().getInfo().setBattery(battery);

            JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_BATTERY, battery);

            CommandRetryScheduled.getInstance().remove((byte) BATTERY.getRequestKey());
            callback.success(BATTERY);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            LogUtil.info("写入数据:" + "680300006B16");
            return ProtocolUtil.hexStrToBytes("680300006B16");
        }
    },

    REAL_TIME_HEART_RATE(-0x01, 0x04, "REAL_TIME_HEART_RATE") {
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {

        }

        @Override
        public byte[] getRequestCommand(String params) {
            // String hex="6802"
            return new byte[0];
        }
    },

    // diagnostic
    DIAGNOSTIC_DATA(-0x01, 0x3A, "DIAGNOSTIC_DATA") {
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            LogUtil.info("3A接收数据：" + ProtocolUtil.byteArrToHexStr(bytes));
        }

        @Override
        public byte[] getRequestCommand(String params) {
            return new byte[0];
        }
    },

    /**
     * 恢复出厂设置
     */
    RESET(0x11, 0x91, "RESET") {
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            callback.success(RESET);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            return ProtocolUtil.hexStrToBytes("68110100017B16");
        }
    },

    /**
     * 实时数据
     */
    REAL_TIME(0x06, 0x86, "REAL_TIME") {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            if (bytes[bytes.length - 1] != 0x16 || !DataConvertUtil.checkSum(bytes)) {
                callback.failed(REAL_TIME, bytes);
                return;
            }

            int index = 5;
            // 心率
            DeviceHolder.getInstance().getInfo().setHeartRate(bytes[index++] & 0xff);

            // 步数
            int stepNumber = ProtocolUtil.byteArrayToInt(DataConvertUtil.getSubArray(bytes, index, 4), false);
            DeviceHolder.getInstance().getInfo().getStepInfo().setStepNumber(stepNumber);
            index += 4;

            // 里程
            int mileage = ProtocolUtil.byteArrayToInt(DataConvertUtil.getSubArray(bytes, index, 4), false);
            DeviceHolder.getInstance().getInfo().getStepInfo().setMileage(mileage);
            index += 4;

            // 热量
            int calories = ProtocolUtil.byteArrayToInt(DataConvertUtil.getSubArray(bytes, index, 4), false);
            DeviceHolder.getInstance().getInfo().getStepInfo().setCalories(calories);
            index += 4;
            // 步速 1 体表温度 2 环境温度 2
            index += 5;

            // 佩戴状态
            DeviceHolder.getInstance().getInfo().setWearingStatus((bytes[index++] & 0xff) == 0x01);
            // 血氧
            DeviceHolder.getInstance().getInfo().getBloodPressureInfo().setBloodOxygen((bytes[index++] & 0xff));
            // 舒张压
            DeviceHolder.getInstance().getInfo().getBloodPressureInfo().setBloodOxygen((bytes[index++] & 0xff));
            // 收缩压
            DeviceHolder.getInstance().getInfo().getBloodPressureInfo().setBloodOxygen((bytes[index++] & 0xff));
            // 收缩压
            DeviceHolder.getInstance().getInfo().getBloodPressureInfo().setBloodViscosity((bytes[index] & 0xff));

            JsBridgeUtil.pushEvent(JsBridgeConstants.DEVICE_REAL_TIME, DeviceHolder.getInstance().getInfo());

            CommunicationService.getInstance().saveRealTimeInfo();
            CommandRetryScheduled.getInstance().remove((byte) REAL_TIME.getRequestKey());
            callback.success(REAL_TIME);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            LogUtil.info("写入数据:" + "68060100006F16");
            return ProtocolUtil.hexStrToBytes("68060100006F16");
        }
    },


    DEVICE_INFO(0X16, 0X96, "DEVICE_INFO") {
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            if (bytes[bytes.length - 1] != 0x16 || !DataConvertUtil.checkSum(bytes)) {
                callback.failed(DEVICE_INFO, bytes);
                return;
            }
            LogUtil.info("设备信息", ProtocolUtil.byteArrToHexStr(bytes));

            byte[] model = DataConvertUtil.getSubArray(bytes, 9, 4);
            // 指定使用 UTF-8 编码将字节数组转换为字符串
            String modelStr = new String(model, StandardCharsets.UTF_8);
            byte[] subArray = DataConvertUtil.getSubArray(bytes, bytes.length - 30, 14);
            String version = new String(subArray, StandardCharsets.UTF_8);

            DeviceHolder.getInstance().getInfo().setModel(modelStr);
            DeviceHolder.getInstance().getInfo().setFirmwareVersion(version);

            DeviceDataService.getInstance().setModelAndVersion(modelStr, version);

            CommandRetryScheduled.getInstance().remove("16");
            callback.success(DEVICE_INFO);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            String hex = "68160200FB007B16";
            return ProtocolUtil.hexStrToBytes(hex);
        }
    },

    HISTORY_DATA(0X17, 0X17, "HISTORY_DATA") {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            if (bytes[bytes.length - 1] != 0x16 || !DataConvertUtil.checkSum(bytes)) {
                callback.failed(HISTORY_DATA, bytes);
                return;
            }

            HistoryDataTypeEnum typeEnum = HistoryDataTypeEnum.getType(bytes[7]);

            if (Objects.isNull(typeEnum)) {
                return;
            }

            String formatDate = DateUtils.formatDate(bytes[4], bytes[5], bytes[6]);

            LogUtil.info("HISTORY_DATA:" + formatDate + "-" + typeEnum.getKeyDes() + "-" + ProtocolUtil.byteArrToHexStr(bytes));

            int sort = bytes[9] & 0xff;
            String[] analysis;
            switch (typeEnum) {
                case STEP:
                case CALORIE:
                case BLOOD_OXYGEN:
                case HEART_RATE:
                case TEMPERATURE:
                case BLOOD_PRESSURE:
                case TOTAL_DATA:
                    analysis = typeEnum.analysis(bytes);
                    CommunicationDataService.getInstance().save(typeEnum.getKeyDes(), Integer.parseInt(formatDate), analysis, sort);
                    break;
            }

            String dateHex = ProtocolUtil.byteArrToHexStr(DataConvertUtils.subBytes(bytes, 4, 3));
            String command = "170600" + dateHex;
            if (sort == 0) {
                command += typeEnum.getKeyDes();
            } else {
                command += ProtocolUtil.byteArrToHexStr(DataConvertUtils.subBytes(bytes, 7, 3));
            }

            CommandRetryScheduled.getInstance().remove(command);
            callback.success(HISTORY_DATA);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            String hex = "68170600";
            hex += DateUtil.getCurrentDateHex();
            hex += params;
            hex = ProtocolUtil.byteArrToHexStr(ProtocolUtil.addSumBytes(ProtocolUtil.hexStrToBytes(hex)));
            return ProtocolUtil.hexStrToBytes(hex + "16");
        }
    },

    TIMING(0x20, 0xA0, "TIMING") {
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            CommandRetryScheduled.getInstance().remove("200400");
            callback.success(TIMING);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            String hex = "68200400";
            byte[] longToBytes = DateUtil.longToBytes(System.currentTimeMillis() / 1000 + 28800, ByteOrder.LITTLE_ENDIAN);
            String dateHex = ProtocolUtil.byteArrToHexStr(longToBytes);
            byte addSum = ProtocolUtil.calcAddSum(ProtocolUtil.hexStrToBytes(hex + dateHex));
            return ProtocolUtil.hexStrToBytes(hex + dateHex + ProtocolUtil.byteToHexStr(addSum) + "16");
        }
    },

    SPORTS_REPORTING(0X22, 0XA2, "SPORTS_REPORTING") {
        @Override
        public void responseHandle(byte[] bytes, AgreementCallback callback) {
            if (bytes[bytes.length - 1] != 0x16 || !DataConvertUtil.checkSum(bytes)) {
                callback.failed(HISTORY_DATA, bytes);
                return;
            }
            LogUtil.info("接收到SPORTS_REPORTING:" + ProtocolUtil.byteArrToHexStr(bytes));
            callback.success(SPORTS_REPORTING);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            return new byte[0];
        }
    },


    UN_KNOWN(-1, -1, "UN_KNOWN") {
        @Override
        public void responseHandle(byte[] data, AgreementCallback callback) {
            callback.success(UN_KNOWN);
        }

        @Override
        public byte[] getRequestCommand(String params) {
            return null;
        }
    },

    ;

    private final int requestKey;

    private final int responseKey;

    private final String type;

    public abstract void responseHandle(byte[] bytes, AgreementCallback callback);

    public abstract byte[] getRequestCommand(String params);

    AgreementEnum(int requestKey, int responseKey, String type) {
        this.requestKey = requestKey;
        this.responseKey = responseKey;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getRequestKey() {
        return requestKey;
    }

    public int getResponseKey() {
        return responseKey;
    }

    public static AgreementEnum getAgreementByResponse(byte[] response) {
        if (Objects.isNull(response)) {
            return UN_KNOWN;
        } else if (response.length == 2 && (response[0] & 0xff) == REAL_TIME_HEART_RATE.getResponseKey()) {
            return REAL_TIME_HEART_RATE;
        }

        AgreementEnum agreementEnum = UN_KNOWN;

        for (AgreementEnum anEnum : values()) {
            if (anEnum.getResponseKey() == (response[1] & 0xff)) {
                agreementEnum = anEnum;
                break;
            }
        }
        return agreementEnum;
    }

    private static boolean isInvalidSameDayData(byte[] bytes) {
        return (bytes[8] & 0xff) == 0 && (bytes[9] & 0xff) == 0;
    }
}
