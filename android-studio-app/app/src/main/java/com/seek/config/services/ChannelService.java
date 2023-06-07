package com.seek.config.services;

import com.ble.blescansdk.config.BeaconBatchConfigActuator;
import com.seek.config.entity.dto.channel.BatchChannelConfigDTO;
import com.seek.config.entity.dto.channel.BatchConfigListQueryDTO;
import com.seek.config.entity.dto.channel.ChannelConfigDTO;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.channel.BatchConfigRecordVO;

import java.util.List;

public interface ChannelService {

    /**
     * 配置Beacon通道
     *
     * @param dto {@link ChannelConfigDTO}
     */
    RespVO<Void> configBeaconChannel(ChannelConfigDTO dto);

    /**
     * 批量配置通道
     *
     * @param dto {@link BatchChannelConfigDTO}
     */
    boolean beaconBatchConfigChannel(BatchChannelConfigDTO dto);

    /**
     * 批量配置秘钥
     */
    boolean beaconBatchConfigSecretKey(BatchChannelConfigDTO dto);

    /**
     * 批量关机
     */
    boolean beaconBatchShutdown(BatchChannelConfigDTO dto);

    /**
     * 获取批量配置结果列表
     *
     * @return 结果
     */
    List<BeaconBatchConfigActuator.ExecutorResult> getBatchConfigList();

    /**
     * 查询批量记录
     *
     * @return 结果
     */
    List<BatchConfigRecordVO> queryBatchRecord(BatchConfigListQueryDTO dto);

    /**
     * 查询批量配置失败列表
     */
    List<BatchConfigRecordVO> queryBatchConfigFailureRecord();
}
