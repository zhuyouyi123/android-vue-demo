package com.seek.config.services;

import com.ble.blescansdk.batch.BeaconBatchConfigActuator;
import com.seek.config.entity.dto.channel.BatchChannelConfigDTO;
import com.seek.config.entity.dto.channel.ChannelConfigDTO;
import com.seek.config.entity.response.RespVO;

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
    void beaconBatchConfigChannel(BatchChannelConfigDTO dto);

    List<BeaconBatchConfigActuator.ExecutorResult> getBatchConfigList();
}
