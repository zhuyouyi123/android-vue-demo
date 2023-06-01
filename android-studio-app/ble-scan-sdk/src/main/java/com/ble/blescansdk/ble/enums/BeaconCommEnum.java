package com.ble.blescansdk.ble.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.entity.RespVO;
import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.utils.BeaconCommUtil;
import com.ble.blescansdk.ble.utils.CRC16Util;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.db.helper.SecretKeyDAOHelper;

public enum BeaconCommEnum {

    /**
     * 秘钥校验
     */
    CHECK_SECRET_REQUEST(1, 0, BeaconCommEnum.REQUEST_TYPE, "CHECK_SECRET_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    /**
     * 秘钥校验
     */
    CHECK_SECRET_RESULT(1, 1, BeaconCommEnum.RESULT_TYPE, "CHECK_SECRET_RESULT") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                // 设置秘钥 SeekStandardDeviceHolder
                String secretKey = SharePreferenceUtil.getInstance().shareGet(SharePreferenceUtil.LAST_USE_SECRET_KEY);
                SecretKeyDAOHelper.saveRecord(secretKey);
                SeekStandardDeviceHolder.getInstance().setSecretKey(secretKey);
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 恢复出厂设置 请求
     */
    RESTORE_FACTORY_SETTINGS_REQUEST(3, 0, BeaconCommEnum.REQUEST_TYPE, "RESTORE_FACTORY_SETTINGS_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    /**
     * 恢复出厂设置 （1）命令收到应答
     */
    RESTORE_FACTORY_SETTINGS_RESPONSE(3, 1, BeaconCommEnum.RESPONSE_TYPE, "RESTORE_FACTORY_SETTINGS_RESPONSE") {
        @Override
        public RespVO handle(String[] strings) {

            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 恢复出厂设置（2）配置完成回复
     */
    RESTORE_FACTORY_SETTINGS_RESULT(3, 2, BeaconCommEnum.RESULT_TYPE, "RESTORE_FACTORY_SETTINGS_RESULT") {
        @Override
        public RespVO handle(String[] strings) {

            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 关机
     */
    SHUTDOWN_REQUEST(4, 0, BeaconCommEnum.REQUEST_TYPE, "SHUTDOWN_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    /**
     * 关机
     */
    SHUTDOWN_RESPONSE(4, 1, BeaconCommEnum.RESPONSE_TYPE, "SHUTDOWN_RESPONSE") {
        @Override
        public RespVO handle(String[] strings) {

            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 关机
     */
    SHUTDOWN_RESULT(4, 2, BeaconCommEnum.RESULT_TYPE, "SHUTDOWN_RESULT") {
        @Override
        public RespVO handle(String[] strings) {

            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 清除秘钥
     */
    REMOVE_SECRET_KEY_REQUEST(5, 0, BeaconCommEnum.REQUEST_TYPE, "REMOVE_SECRET_KEY_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    /**
     * 清除秘钥
     */
    REMOVE_SECRET_KEY_RESULT(5, 1, BeaconCommEnum.RESULT_TYPE, "REMOVE_SECRET_KEY_RESULT") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                // 清除秘钥 SeekStandardDeviceHolder
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 修改秘钥
     */
    UPDATE_SECRET_KEY_REQUEST(6, 0, BeaconCommEnum.REQUEST_TYPE, "UPDATE_SECRET_KEY_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    /**
     * 修改秘钥
     */
    UPDATE_SECRET_KEY_RESULT(6, 1, BeaconCommEnum.RESULT_TYPE, "UPDATE_SECRET_KEY_RESULT") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败 2 秘钥错误
            int code = Integer.parseInt(strings[2]);
            if (code == 2) {
                return RespVO.fail(code, ErrorStatusEnum.SECRET_KEY_ERROR.getErrorCode());
            } else if (code == 1) {
                return RespVO.fail();
            }
            // 更新秘钥 SeekStandardDeviceHolder
            SecretKeyDAOHelper.saveRecord(SharePreferenceUtil.getInstance().shareGet(SharePreferenceUtil.LAST_USE_SECRET_KEY));
            return RespVO.success();
        }
    },


    /**
     * 读取出厂信息 请求
     */
    READ_FACTORY_VERSION_INFO_REQUEST(7, 0, BeaconCommEnum.REQUEST_TYPE, "READ_FACTORY_VERSION_INFO_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 读取出厂信息 应答
     */
    READ_FACTORY_VERSION_INFO_RESPONSE(7, 1, BeaconCommEnum.RESPONSE_TYPE, "READ_FACTORY_VERSION_INFO_RESPONSE") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 读取出厂信息
     */
    READ_FACTORY_VERSION_INFO_RESULT(7, 2, BeaconCommEnum.RESULT_TYPE, "READ_FACTORY_VERSION_INFO_RESULT") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                SeekStandardDeviceHolder.getInstance().setFactoryVersionInfo(strings);
                return RespVO.success(SeekStandardDeviceHolder.getInstance().getFactoryVersionInfo());
            }
            return RespVO.fail();
        }
    },


    /**
     * 读取特征信息
     */
    READ_FEATURE_INFO_REQUEST(8, 0, BeaconCommEnum.REQUEST_TYPE, "READ_FEATURE_INFO_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    /**
     * 读取特征信息
     */
    READ_FEATURE_INFO_RESPONSE(8, 1, BeaconCommEnum.RESPONSE_TYPE, "READ_FEATURE_INFO_RESPONSE") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    READ_FEATURE_INFO_RESULT(8, 2, BeaconCommEnum.RESULT_TYPE, "READ_FEATURE_INFO_RESULT") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                SeekStandardDeviceHolder.getInstance().setFeatureInfo(strings);
                return RespVO.success(SeekStandardDeviceHolder.getInstance().getFeatureInfo());
            }
            return RespVO.fail();
        }
    },

    CHANNEL_CONFIG_BEACON_REQUEST(9, 0, BeaconCommEnum.REQUEST_TYPE, "CHANNEL_CONFIG_BEACON_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    CHANNEL_CONFIG_BEACON_RESPONSE(9, 1, BeaconCommEnum.RESPONSE_TYPE, "CHANNEL_CONFIG_BEACON_RESPONSE") {
        @Override
        public RespVO handle(String[] strings) {
            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    CHANNEL_CONFIG_BEACON_RESULT(9, 2, BeaconCommEnum.RESULT_TYPE, "CHANNEL_CONFIG_BEACON_RESULT") {
        @Override
        public RespVO handle(String[] strings) {

            // 1：成功 0：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    QUERY_CONFIG_AGREEMENT_REQUEST(10, 0, BeaconCommEnum.REQUEST_TYPE, "QUERY_CONFIG_AGREEMENT_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },
    QUERY_CONFIG_AGREEMENT_RESULT(10, 1, BeaconCommEnum.RESULT_TYPE, "QUERY_CONFIG_AGREEMENT_RESULT") {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public RespVO handle(String[] strings) {

            SeekStandardDeviceHolder.getInstance().setAgreementInfo(strings);
            return RespVO.success(SeekStandardDeviceHolder.getInstance());
        }
    },

    /**
     * 需要秘钥连接判断
     */
    NEED_SECRET_CONNECT_REQUEST(11, 0, BeaconCommEnum.REQUEST_TYPE, "NEED_SECRET_CONNECT_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    /**
     * 需要秘钥连接判断
     */
    NEED_SECRET_CONNECT_RESULT(11, 1, BeaconCommEnum.RESULT_TYPE, "NEED_SECRET_CONNECT_RESULT") {
        @Override
        public RespVO handle(String[] strings) {

            // 1：需要密码 0：不需要密码 2秘钥错误
            if ("1".equals(strings[2]) || "2".equals(strings[2])) {
                return RespVO.success(true);
            }
            return RespVO.success(false);
        }
    },

    NOT_CONNECTABLE_CONFIG_REQUEST(12, 0, BeaconCommEnum.REQUEST_TYPE, "NOT_CONNECTABLE_CONFIG_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    NOT_CONNECTABLE_CONFIG_RESULT(12, 1, BeaconCommEnum.RESULT_TYPE, "NOT_CONNECTABLE_CONFIG_RESULT") {
        @Override
        public RespVO handle(String[] strings) {

            // 0：成功 1：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    /**
     * 触发响应时间
     */
    TRIGGER_RESPONSE_TIME_CONFIG_REQUEST(13, 0, BeaconCommEnum.REQUEST_TYPE, "TRIGGER_RESPONSE_TIME_CONFIG_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    TRIGGER_RESPONSE_TIME_CONFIG_RESULT(13, 1, BeaconCommEnum.RESULT_TYPE, "TRIGGER_RESPONSE_TIME_CONFIG_RESULT") {
        @Override
        public RespVO handle(String[] strings) {

            // 0：成功 1：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },

    RESTART_BEACON_REQUEST(14, 0, BeaconCommEnum.REQUEST_TYPE, "RESTART_BEACON_REQUEST") {
        @Override
        public RespVO handle(String[] strings) {
            return RespVO.success();
        }
    },

    RESTART_BEACON_RESULT(14, 1, BeaconCommEnum.RESULT_TYPE, "RESTART_BEACON_RESULT") {
        @Override
        public RespVO handle(String[] strings) {

            // 0：成功 1：失败
            if (BeaconCommEnum.SUCCESS.equals(strings[2])) {
                return RespVO.success();
            }
            return RespVO.fail();
        }
    },
    ;


    BeaconCommEnum(int code, int childrenCode, String type, String value) {
        this.code = code;
        this.childrenCode = childrenCode;
        this.value = value;
        this.type = type;
    }

    public static final String REQUEST_TYPE = "REQUEST";
    public static final String RESPONSE_TYPE = "RESPONSE";
    public static final String RESULT_TYPE = "RESULT";

    public static final String SUCCESS = "0";

    private final int code;
    private final int childrenCode;
    private final String type;
    private final String value;

    public int getCode() {
        return code;
    }

    public int getChildrenCode() {
        return childrenCode;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    /**
     * 凭借指令
     *
     * @param secretKey 秘钥
     * @return 指令
     */
    public String getInstructions(String secretKey, String data) {
        StringBuilder stringBuilder = new StringBuilder("@_");
        stringBuilder.append(secretKey).append("_");
        stringBuilder.append(getCode()).append("_");
        stringBuilder.append(getChildrenCode());

        if (StringUtils.isNotBlank(data)) {
            stringBuilder.append("_").append(data);
        }

        String calcCrc16 = CRC16Util.calcCrc16(stringBuilder.toString());

        stringBuilder.append("_").append(calcCrc16.toLowerCase()).append("_!");
        String string = stringBuilder.toString();
        if (string.contains("__")) {
            string = string.replaceAll("__", "_");
        }

        return string;
    }

    public abstract RespVO handle(String[] strings);

    public static BeaconCommEnum getByCode(int code, int childrenCode) {
        BeaconCommEnum beaconCommEnum = null;
        for (BeaconCommEnum commEnum : values()) {
            if (commEnum.getCode() == code && commEnum.getChildrenCode() == childrenCode) {
                beaconCommEnum = commEnum;
                break;
            }
        }
        return beaconCommEnum;
    }

    public static BeaconCommEnum getByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        BeaconCommEnum commEnum = null;

        for (BeaconCommEnum comm : values()) {
            if (comm.getValue().equals(value)) {
                commEnum = comm;
                break;
            }
        }
        return commEnum;
    }


}
