package com.seek.config.services.impl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.BleSdkManager;
import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.ble.blescansdk.ble.utils.StringUtils;
import com.ble.blescansdk.config.BeaconBatchConfigActuator;
import com.ble.blescansdk.config.entity.BeaconConfig;
import com.ble.blescansdk.config.entity.ConfigResult;
import com.ble.blescansdk.config.entity.dto.ChannelConfigInfo;
import com.ble.blescansdk.config.helper.BeaconSingleConfigHelper;
import com.ble.blescansdk.db.dataobject.BatchConfigRecordDO;
import com.ble.blescansdk.db.enums.BatchConfigResultEnum;
import com.ble.blescansdk.db.helper.BatchConfigRecordHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seek.config.entity.dto.channel.BatchChannelConfigDTO;
import com.seek.config.entity.dto.channel.BatchConfigListQueryDTO;
import com.seek.config.entity.dto.channel.ChannelConfigDTO;
import com.seek.config.entity.enums.ErrorEnum;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.channel.BatchConfigRecordVO;
import com.seek.config.services.ChannelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @Override
    public RespVO<Void> configBeaconChannel(ChannelConfigDTO dto) {

        final ConfigResult configResult = BeaconSingleConfigHelper.getInstance().configBeaconChannel(new ChannelConfigInfo(dto.getChannelNumber(), dto.getFrameType(),
                dto.getAlwaysBroadcast(), dto.getBroadcastDataString(),
                dto.getBroadcastInterval(), dto.getCalibrationDistance(),
                dto.getBroadcastPower(), dto.getTriggerSwitch(),
                dto.getTriggerBroadcastTime(), dto.getTriggerBroadcastInterval()
                , dto.getTriggerBroadcastPower(), dto.getTriggerCondition(),
                dto.getResponseSwitch(), dto.getDeviceName()
        ));

        if (configResult.getErrorCode() != 0) {
            return RespVO.failure(ErrorEnum.getFailMessage(configResult.getErrorCode()));
        }

        return RespVO.success();
    }

    /**
     * 批量配置通道
     *
     * @param dto {@link BatchChannelConfigDTO}
     */
    @Override
    public boolean beaconBatchConfigChannel(BatchChannelConfigDTO dto) {
        if (dto.getRetry()) {
            String shareGet = SharePreferenceUtil.getInstance().shareGet(SharePreferenceUtil.BATCH_CONFIG_CHANNEL_LIST_KEY);
            if (StringUtils.isBlank(shareGet)) {
                return false;
            }

            List<BeaconConfig> list = new Gson().fromJson(shareGet, new TypeToken<List<BeaconConfig>>() {
            }.getType());
            return BleSdkManager.getInstance().batchConfigChannel(dto.getAddressList(), list, dto.getSecretKey());
        }
        return BleSdkManager.getInstance().batchConfigChannel(dto.getAddressList(), dto.getChannelInfo(), dto.getSecretKey());
    }

    /**
     * 批量配置秘钥
     *
     * @param dto {@link BatchChannelConfigDTO}
     */
    @Override
    public boolean beaconBatchConfigSecretKey(BatchChannelConfigDTO dto) {
        return BleSdkManager.getInstance().batchConfigSecretKey(dto.getAddressList(), dto.getSecretKey(), dto.getOldSecretKey());
    }

    /**
     * 批量配置关机
     *
     * @param dto {@link BatchChannelConfigDTO}
     */
    @Override
    public boolean beaconBatchShutdown(BatchChannelConfigDTO dto) {
        return BleSdkManager.getInstance().batchShutdown(dto.getAddressList(), dto.getSecretKey(), dto.getClearHistory());
    }

    @Override
    public List<BeaconBatchConfigActuator.ExecutorResult> getBatchConfigList() {
        return BleSdkManager.getInstance().getBatchConfigList();
    }

    /**
     * 查询批量记录
     *
     * @return 结果
     */
    @Override
    public List<BatchConfigRecordVO> queryBatchRecord(BatchConfigListQueryDTO dto) {
        if (!BleSdkManager.getBleOptions().isDatabaseSupport()) {
            return Collections.emptyList();
        }

        List<BatchConfigRecordDO> recordDOS = BatchConfigRecordHelper.queryByType(dto.getType());

        if (CollectionUtils.isEmpty(recordDOS)) {
            return Collections.emptyList();
        }

        List<BatchConfigRecordVO> list = new ArrayList<>();
        for (BatchConfigRecordDO recordDO : recordDOS) {
            BatchConfigRecordVO vo = new BatchConfigRecordVO();
            vo.setType(recordDO.getType());
            vo.setResult(recordDO.getResult());
            vo.setAddress(recordDO.getAddress());
            if (0 != recordDO.getFailReason()) {
                vo.setFailReason(ErrorEnum.getFailMessage(recordDO.getFailReason()));
            }

            list.add(vo);
        }
        return list;
    }

    /**
     * 查询批量配置失败列表
     */
    @Override
    public List<BatchConfigRecordVO> queryBatchConfigFailureRecord() {
        if (!BleSdkManager.getBleOptions().isDatabaseSupport()) {
            return Collections.emptyList();
        }

        List<BatchConfigRecordDO> recordDOS = BatchConfigRecordHelper.queryByResult(BatchConfigResultEnum.FAIL.getCode());

        if (CollectionUtils.isEmpty(recordDOS)) {
            return Collections.emptyList();
        }

        List<BatchConfigRecordVO> list = new ArrayList<>();
        for (BatchConfigRecordDO recordDO : recordDOS) {
            BatchConfigRecordVO vo = new BatchConfigRecordVO();
            vo.setType(recordDO.getType());
            vo.setResult(recordDO.getResult());
            vo.setAddress(recordDO.getAddress());
            vo.setFailReason(ErrorEnum.getFailMessage(recordDO.getFailReason()));

            list.add(vo);
        }
        return list;

    }
}
