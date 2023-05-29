package com.ble.blescansdk.ble.holder;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.enums.BroadcastPowerEnum;
import com.ble.blescansdk.ble.enums.EddystoneUrlPrefixEnum;
import com.ble.blescansdk.ble.enums.EddystoneUrlSuffixEnum;
import com.ble.blescansdk.ble.enums.seekstandard.ThoroughfareTypeEnum;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 信标holder 存储信息
 */
public class SeekStandardDeviceHolder {

    private static volatile SeekStandardDeviceHolder instance = null;

    private String address;

    /**
     * 可连接
     */
    private boolean connectable;

    /**
     * 连接状态
     */
    private boolean connectState;

    private Boolean supportACC;

    private FactoryVersionInfo factoryVersionInfo;

    private FeatureInfo featureInfo;

    private List<AgreementInfo> agreementInfoList;

    private String secretKey = "loc666";

    private boolean needSecretKey = true;

    public static SeekStandardDeviceHolder getInstance() {
        if (null == instance) {
            instance = new SeekStandardDeviceHolder();
        }
        return instance;
    }

    public static class FactoryVersionInfo {
        private String mac;
        private String manufactor;
        private String sdkVersion;
        private String firmwareVersion;
        private String hardwareVersion;
        private String model;

        private Integer checkTime;

        public void setMac(String mac) {
            this.mac = mac;
        }

        public void setManufactor(String manufactor) {
            this.manufactor = manufactor;
        }

        public void setSdkVersion(String sdkVersion) {
            this.sdkVersion = sdkVersion;
        }

        public void setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
        }

        public void setHardwareVersion(String hardwareVersion) {
            this.hardwareVersion = hardwareVersion;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public void setCheckTime(Integer checkTime) {
            this.checkTime = checkTime;
        }
    }


    public static class FeatureInfo {
        private String channelInfo;

        private String supportProAgreement;

        private String supportTxPower;

        public void setChannelInfo(String channelInfo) {
            this.channelInfo = channelInfo;
        }

        public void setSupportProAgreement(String supportProAgreement) {
            this.supportProAgreement = supportProAgreement;
        }

        public void setSupportTxPower(String supportTxPower) {
            this.supportTxPower = supportTxPower;
        }
    }

    public static class AgreementInfo {
        /**
         * 通道数
         */
        private Integer channelNumber;
        /**
         * 协议类型
         */
        private String agreementType;

        /**
         * 协议名
         */
        private String agreementName;
        /**
         * 广播间隔
         */
        private Integer broadcastInterval;
        /**
         * 校准距离
         */
        private Integer calibrationDistance;
        /**
         * 广播功率
         */
        private Integer broadcastPower;

        private Double broadcastPowerValue;
        /**
         * 是否开启触发器
         */
        private Boolean triggerSwitch;
        /**
         * 触发条件
         */
        private Integer triggerCondition;
        /**
         * 触发时间
         */
        private Integer triggerTime;
        /**
         * 始终广播
         */
        private Boolean alwaysBroadcast;
        /**
         * 发射间隔
         */
        private Integer txInterval;
        /**
         * 始终广播
         */
        private Integer txPower;

        private Double triggerBroadcastPowerValue;
        /**
         * 始终广播
         */
        private List<String> agreementData;

        public Integer getChannelNumber() {
            return channelNumber;
        }

        public void setChannelNumber(Integer channelNumber) {
            this.channelNumber = channelNumber;
        }

        public String getAgreementType() {
            return agreementType;
        }

        public void setAgreementType(String agreementType) {
            this.agreementType = agreementType;
        }

        public String getAgreementName() {
            return agreementName;
        }

        public void setAgreementName(String agreementName) {
            this.agreementName = agreementName;
        }

        public void setBroadcastInterval(Integer broadcastInterval) {
            this.broadcastInterval = broadcastInterval;
        }

        public void setCalibrationDistance(Integer calibrationDistance) {
            this.calibrationDistance = calibrationDistance;
        }

        public void setBroadcastPower(Integer broadcastPower) {
            this.broadcastPower = broadcastPower;
        }

        public void setBroadcastPowerValue(Double broadcastPowerValue) {
            this.broadcastPowerValue = broadcastPowerValue;
        }

        public void setTriggerSwitch(Boolean triggerSwitch) {
            this.triggerSwitch = triggerSwitch;
        }

        public void setTriggerCondition(Integer triggerCondition) {
            this.triggerCondition = triggerCondition;
        }

        public void setTriggerTime(Integer triggerTime) {
            this.triggerTime = triggerTime;
        }

        public void setAlwaysBroadcast(Boolean alwaysBroadcast) {
            this.alwaysBroadcast = alwaysBroadcast;
        }

        public Boolean getAlwaysBroadcast() {
            return alwaysBroadcast;
        }

        public void setTxInterval(Integer txInterval) {
            this.txInterval = txInterval;
        }

        public void setTxPower(Integer txPower) {
            this.txPower = txPower;
        }

        public void setTriggerBroadcastPowerValue(Double triggerBroadcastPowerValue) {
            this.triggerBroadcastPowerValue = triggerBroadcastPowerValue;
        }

        public void setAgreementData(List<String> agreementData) {
            this.agreementData = agreementData;
        }

        public List<String> getAgreementData() {
            return agreementData;
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        instance = null;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isConnectable() {
        return connectable;
    }

    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }

    public boolean isConnectState() {
        return connectState;
    }

    public void setConnectState(boolean connectState) {
        this.connectState = connectState;
    }

    public Boolean getSupportACC() {
        return supportACC;
    }

    public void setSupportACC(Boolean supportACC) {
        this.supportACC = supportACC;
    }

    public FactoryVersionInfo getFactoryVersionInfo() {
        return factoryVersionInfo;
    }

    public void setFactoryVersionInfo(FactoryVersionInfo factoryVersionInfo) {
        this.factoryVersionInfo = factoryVersionInfo;
    }

    public void setFactoryVersionInfo(String[] splits) {
        FactoryVersionInfo info = new FactoryVersionInfo();
        int length = splits.length;
        info.setMac(length > 3 ? splits[3] : "");
        info.setManufactor(length > 4 ? splits[4] : "");
        info.setSdkVersion(length > 5 ? splits[5] : "");
        info.setFirmwareVersion(length > 6 ? StringUtils.convertString(splits[6]) : "");
        if (length > 7) {
            String split = splits[7];
            info.setHardwareVersion(StringUtils.convertString(split));
            byte[] bytes = ProtocolUtil.hexStrToBytes(split);
            if (null != bytes) {
                info.setModel("02".equals(ProtocolUtil.byteToHexStr(bytes[bytes.length - 1])) ? "C8" : "L8");
                byte[] toBitBytes = ProtocolUtil.byteToBit(bytes[3]);
                setSupportACC(toBitBytes[toBitBytes.length - 1] == 0x01);
            }
        }
        info.setCheckTime(length > 8 ? Integer.parseInt(splits[8]) : 5);
        this.factoryVersionInfo = info;
    }

    public FeatureInfo getFeatureInfo() {
        return featureInfo;
    }

    public void setFeatureInfo(FeatureInfo featureInfo) {
        this.featureInfo = featureInfo;
    }

    public void setFeatureInfo(String[] splits) {
        int length = splits.length;

        FeatureInfo info = new FeatureInfo();
        info.setChannelInfo(length > 3 ? splits[3] : "");
        info.setSupportProAgreement(length > 4 ? splits[4] : "");
        info.setSupportTxPower(length > 5 ? splits[5] : "");
        this.featureInfo = info;
    }

    public List<AgreementInfo> getAgreementInfoList() {
        if (CollectionUtils.isEmpty(agreementInfoList)) {
            agreementInfoList = new ArrayList<>();
        }
        return agreementInfoList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAgreementInfo(String[] splits) {
        int length = splits.length;
        AgreementInfo info = new AgreementInfo();
        int start = 2;
        // 通道号
        info.setChannelNumber(Integer.parseInt(splits[start]));
        start++;
        // 协议类型
        Optional<ThoroughfareTypeEnum> optional = ThoroughfareTypeEnum.getByType(splits[start]);

        if (!optional.isPresent() || optional.get() == ThoroughfareTypeEnum.EMPTY) {
            info.setAgreementType(ThoroughfareTypeEnum.EMPTY.getType());
            this.addAgreementInfo(info);
            return;
        }
        info.setAgreementType(optional.get().getType());
        info.setAgreementName(optional.get().getValue());
        start++;
        // 广播间隔
        info.setBroadcastInterval(length > start ? Integer.parseInt(splits[start]) : 100);
        start++;
        // 校准距离
        info.setCalibrationDistance(length > start ? Integer.parseInt(splits[start]) : 205);
        start++;
        // 广播功率
        int level = 0;
        if (length > start) {
            level = Integer.parseInt(splits[start]);
        }
        info.setBroadcastPower(level);
        info.setBroadcastPowerValue(BroadcastPowerEnum.getByLevel(level).getPower());
        start++;
        // 是否开启触发器
        info.setTriggerSwitch(length > start && splits[start].equals("1"));
        start++;
        // 触发条件  1 双击 2 三 3 加速度
        info.setTriggerCondition(length > start ? Integer.parseInt(splits[start]) : 1);
        start++;
        // 触发时间
        info.setTriggerTime(length > start ? Integer.parseInt(splits[start]) : 6000);
        start++;
        // 是否始终开启广播
        info.setAlwaysBroadcast("1".equals(splits[start]));
        start++;
        // 发射间隔
        info.setTxInterval(length > start ? Integer.parseInt(splits[start]) : 100);
        start++;
        // 发射功率
        level = 0;
        if (length > start) {
            level = Integer.parseInt(splits[start]);
        }
        info.setTxPower(length > start ? Integer.parseInt(splits[start]) : 9);
        info.setTriggerBroadcastPowerValue(BroadcastPowerEnum.getByLevel(level).getPower());
        // 协议数据
        start++;

        if (length > start) {
            ArrayList<String> strings = new ArrayList<>(Arrays.asList(splits).subList(start, length - 1));
            List<String> list = new ArrayList<>();
            if (optional.get() == ThoroughfareTypeEnum.EDDYSTONE_URL) {
                if (strings.size() > 0) {
                    list.add(EddystoneUrlPrefixEnum.getByCode(strings.get(0)));
                }
                if (strings.size() > 2) {
                    // 处理下特殊字符
                    String urlContent = strings.get(2).replaceAll("\b", "").trim();
                    list.add(urlContent);
                }
                if (strings.size() > 3) {
                    list.add(EddystoneUrlSuffixEnum.getByCode(strings.get(3)));
                }
                info.setAgreementData(list);
            } else {
                info.setAgreementData(strings);
            }
        }
        this.addAgreementInfo(info);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAgreementInfoList(List<AgreementInfo> agreementInfoList) {
        if (CollectionUtils.isNotEmpty(agreementInfoList)) {
            agreementInfoList = agreementInfoList.stream().sorted(Comparator.comparingInt(AgreementInfo::getChannelNumber)).collect(Collectors.toList());
        }
        this.agreementInfoList = agreementInfoList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addAgreementInfo(AgreementInfo agreementInfo) {

        if (null == agreementInfo) {
            return;
        }

        List<AgreementInfo> agreementInfoLists = getAgreementInfoList();

        agreementInfoLists = agreementInfoLists.stream().filter(e -> !Objects.equals(e.getChannelNumber(), agreementInfo.getChannelNumber())).collect(Collectors.toList());

        agreementInfoLists.add(agreementInfo);

        this.agreementInfoList = agreementInfoLists.stream().sorted(Comparator.comparingInt(AgreementInfo::getChannelNumber)).collect(Collectors.toList());
    }


    public boolean isNeedSecretKey() {
        return needSecretKey;
    }

    public void setNeedSecretKey(boolean needSecretKey) {
        this.needSecretKey = needSecretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getFrameTypeDropDown(String currentFrameType) {
        List<AgreementInfo> agreementInfoList = getAgreementInfoList();

        ThoroughfareTypeEnum[] typeEnums = ThoroughfareTypeEnum.values();
        List<String> types = new ArrayList<>();
        // 协议为空
        for (ThoroughfareTypeEnum typeEnum : typeEnums) {
            if (!typeEnum.getType().equals(ThoroughfareTypeEnum.EMPTY.getType())) {
                types.add(typeEnum.getValue());
            }
        }
        if (CollectionUtils.isEmpty(agreementInfoList)) {
            return types;
        }
        // 根据已存在协议 去除
        for (AgreementInfo info : agreementInfoList) {
            String agreementType = info.getAgreementType();
            Optional<ThoroughfareTypeEnum> typeEnum = ThoroughfareTypeEnum.getByType(agreementType);
            if (!typeEnum.isPresent()) {
                continue;
            }
            String agreementName = info.getAgreementName();

            switch (typeEnum.get()) {
                case EMPTY:
                case I_BEACON:
                case EDDYSTONE_UID:
                case EDDYSTONE_URL:
                    continue;
                default:
                    for (int i = 0; i < types.size(); i++) {
                        if (types.get(i).equals(agreementName)) {
                            if (StringUtils.isNotBlank(currentFrameType) && agreementName.equals(currentFrameType)) {
                                continue;
                            }
                            types.remove(i);
                            break;
                        }
                    }
            }
        }
        return types;
    }
}
