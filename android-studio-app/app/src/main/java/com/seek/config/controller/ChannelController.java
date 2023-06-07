package com.seek.config.controller;


import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.holder.SeekStandardDeviceHolder;
import com.ble.blescansdk.config.BeaconBatchConfigActuator;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.annotation.AppRequestMethod;
import com.seek.config.entity.dto.channel.BatchChannelConfigDTO;
import com.seek.config.entity.dto.channel.BatchConfigListQueryDTO;
import com.seek.config.entity.dto.channel.ChannelConfigDTO;
import com.seek.config.entity.dto.channel.FrameTypeDropdownDTO;
import com.seek.config.entity.response.RespVO;
import com.seek.config.entity.vo.channel.BatchConfigRecordVO;
import com.seek.config.entity.vo.channel.FrameTypeDropdownVO;
import com.seek.config.services.ChannelService;
import com.seek.config.services.impl.ChannelServiceImpl;
import com.seek.config.utils.I18nUtil;

import java.util.ArrayList;
import java.util.List;

@AppController(path = "channel")
public class ChannelController {

    private final ChannelService channelService = ChannelServiceImpl.getInstance();

    /**
     * 获取帧类型下拉框
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @AppRequestMapper(path = "/frame-type/dropdown")
    public RespVO<List<FrameTypeDropdownVO>> getFrameTypeDropDown(FrameTypeDropdownDTO dto) {
        List<String> frameTypeDropDown = SeekStandardDeviceHolder.getInstance().getFrameTypeDropDown(dto.getCurrentFrameType());

        List<FrameTypeDropdownVO> list = new ArrayList<>();

        for (String frame : frameTypeDropDown) {
            FrameTypeDropdownVO frameTypeDropdownVO = new FrameTypeDropdownVO();
            frameTypeDropdownVO.setName(frame);
            list.add(frameTypeDropdownVO);
        }

        return RespVO.success(list);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @AppRequestMapper(path = "/save", method = AppRequestMethod.POST)
    public RespVO<Void> beaconConfigChannel(ChannelConfigDTO dto) {
        return channelService.configBeaconChannel(dto);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @AppRequestMapper(path = "/batch/save/channel", method = AppRequestMethod.POST)
    public RespVO<Void> beaconBatchConfigChannel(BatchChannelConfigDTO dto) {
        if (channelService.beaconBatchConfigChannel(dto)) {
            return RespVO.success();
        }
        return RespVO.failure(I18nUtil.getMessage(I18nUtil.REQUEST_ERROR));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @AppRequestMapper(path = "/batch/save/secret-key", method = AppRequestMethod.POST)
    public RespVO<Boolean> beaconBatchConfigSecretKey(BatchChannelConfigDTO dto) {
        if (channelService.beaconBatchConfigSecretKey(dto)) {
            return RespVO.success();
        }
        return RespVO.failure(I18nUtil.getMessage(I18nUtil.REQUEST_ERROR));
    }

    @AppRequestMapper(path = "/batch/shutdown", method = AppRequestMethod.POST)
    public RespVO<Boolean> beaconBatchShutdown(BatchChannelConfigDTO dto) {
        if (channelService.beaconBatchShutdown(dto)) {
            return RespVO.success();
        }
        return RespVO.failure(I18nUtil.getMessage(I18nUtil.REQUEST_ERROR));
    }

    @AppRequestMapper(path = "/batch/config/list")
    public RespVO<List<BeaconBatchConfigActuator.ExecutorResult>> beaconBatchConfigResult() {
        return RespVO.success(channelService.getBatchConfigList());
    }

    @AppRequestMapper(path = "/batch/records")
    public RespVO<List<BatchConfigRecordVO>> queryBatchRecord(BatchConfigListQueryDTO dto) {
        return RespVO.success(channelService.queryBatchRecord(dto));
    }

    @AppRequestMapper(path = "/batch/failure-list")
    public RespVO<List<BatchConfigRecordVO>> queryBatchConfigFailureRecord() {
        return RespVO.success(channelService.queryBatchConfigFailureRecord());
    }
}
