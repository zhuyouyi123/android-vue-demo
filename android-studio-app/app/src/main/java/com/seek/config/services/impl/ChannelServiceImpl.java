package com.seek.config.services.impl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.batch.BeaconBatchConfigActuator;
import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.entity.seek.config.ChannelConfig;
import com.ble.blescansdk.ble.enums.BeaconCommEnum;
import com.ble.blescansdk.ble.enums.seekstandard.ThoroughfareTypeEnum;
import com.ble.blescansdk.ble.enums.BroadcastPowerEnum;
import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.seek.config.entity.dto.channel.BatchChannelConfigDTO;
import com.seek.config.entity.dto.channel.ChannelConfigDTO;
import com.seek.config.entity.response.RespVO;
import com.seek.config.services.ChannelService;
import com.seek.config.utils.I18nUtil;
import com.seek.config.utils.helper.SeekStandardCommunicationHelper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChannelServiceImpl implements ChannelService {

    private static ChannelServiceImpl instance = null;

    public static ChannelServiceImpl getInstance() {
        if (instance == null) {
            instance = new ChannelServiceImpl();
        }
        return instance;
    }

    /**
     * 配置Beacon通道
     *
     * @param dto {@link ChannelConfigDTO}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public RespVO<Void> checkChannel(ChannelConfigDTO dto) {
        List<SeekStandardDeviceHolder.AgreementInfo> agreementInfoList = SeekStandardDeviceHolder.getInstance().getAgreementInfoList();

        Boolean supportACC = SeekStandardDeviceHolder.getInstance().getSupportACC();
        if ((null == supportACC || !supportACC) && ThoroughfareTypeEnum.ACC.getValue().equals(dto.getFrameType())) {
            return RespVO.failure("此设备不支持ACC协议");
        }

        if (CollectionUtils.isEmpty(agreementInfoList)) {
            return RespVO.failure("通道不存在，配置失败");
        }

        // 找到通道不为空的通道
        agreementInfoList = agreementInfoList.stream().filter(e -> !Objects.equals(e.getAgreementType(), ThoroughfareTypeEnum.EMPTY.getType())).collect(Collectors.toList());

        // 页面配置的通道类型
        ThoroughfareTypeEnum thoroughfareTypeEnum = ThoroughfareTypeEnum.getByValue(dto.getFrameType());
        // 只有一个通道
        if (agreementInfoList.size() == 1) {
            SeekStandardDeviceHolder.AgreementInfo info = agreementInfoList.get(0);
            // 如果配置的是空的
            if (info.getChannelNumber() == dto.getChannelNumber()) {
                if (thoroughfareTypeEnum == ThoroughfareTypeEnum.EMPTY) {
                    return RespVO.failure("信标需要至少支持一条通道");
                }
                if (thoroughfareTypeEnum == ThoroughfareTypeEnum.CORE_IOT_AOA) {
                    return RespVO.failure("仅有一条通道不支持配置此协议");
                }
                if (!dto.getAlwaysBroadcast()) {
                    return RespVO.failure("仅有一条通道必须开启始终广播");
                }
            }
        } else {
            if (agreementInfoList.stream().noneMatch(SeekStandardDeviceHolder.AgreementInfo::getAlwaysBroadcast)) {
                return RespVO.failure("至少有一条通道需要开启始终广播");
            }
        }

        return RespVO.success();
    }

    /**
     * 配置Beacon通道
     *
     * @param dto {@link ChannelConfigDTO}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public RespVO<Void> configBeaconChannel(ChannelConfigDTO dto) {

        RespVO<Void> respVO = checkChannel(dto);
        if (respVO.getErrorCode() != 0) {
            return respVO;
        }

        ChannelConfig config = new ChannelConfig();

        ThoroughfareTypeEnum thoroughfareTypeEnum = ThoroughfareTypeEnum.getByValue(dto.getFrameType());
        config.setFrameType(thoroughfareTypeEnum);
        config.setChannelNumber(dto.getChannelNumber());
        if (thoroughfareTypeEnum == ThoroughfareTypeEnum.EMPTY) {
            SeekStandardCommunicationHelper.getInstance().write(BeaconCommEnum.CHANNEL_CONFIG_BEACON_REQUEST.getValue(), config.getSendMessage());
            return RespVO.success();
        } else if (ThoroughfareTypeEnum.I_BEACON == thoroughfareTypeEnum) {
            config.setResponseSwitch(dto.getResponseSwitch());
        }

        config.setDeviceName(dto.getDeviceName());
        config.setBroadcastInterval(dto.getBroadcastInterval());
        config.setCalibrationDistance(dto.getCalibrationDistance());
        config.setBroadcastPower(BroadcastPowerEnum.getByPower(dto.getBroadcastPower()).getLevel());
        config.setTriggerSwitch(dto.getTriggerSwitch());
        config.setTriggerCondition(dto.getTriggerCondition());
        config.setTriggerBroadcastTime(dto.getTriggerBroadcastTime());
        config.setTriggerBroadcastInterval(dto.getTriggerBroadcastInterval());
        config.setTriggerBroadcastPower(BroadcastPowerEnum.getByPower(dto.getTriggerBroadcastPower()).getLevel());
        config.setAlwaysBroadcast(dto.getAlwaysBroadcast());
        config.setBroadcastData(dto.getBroadcastData());

        SeekStandardCommunicationHelper.getInstance().write(BeaconCommEnum.CHANNEL_CONFIG_BEACON_REQUEST.getValue(), config.getSendMessage());
        return RespVO.success();
    }

    /**
     * 批量配置通道
     *
     * @param dto {@link BatchChannelConfigDTO}
     */
    @Override
    public void beaconBatchConfigChannel(BatchChannelConfigDTO dto) {
        BleSdkManager.getInstance().batchConfig(dto.getAddressList(), dto.getChannelInfo());
    }

    @Override
    public List<BeaconBatchConfigActuator.ExecutorResult> getBatchConfigList() {
        return BleSdkManager.getInstance().getBatchConfigList();
    }
}