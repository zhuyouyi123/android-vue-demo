package com.seek.config.services;

import com.seek.config.entity.dto.ScanInitDTO;
import com.seek.config.entity.vo.ScanDataVO;

public interface BleService {

    /**
     * 扫描初始化
     */
    void init(ScanInitDTO dto);

    /**
     * 开始扫描
     */
    void startScan();

    /**
     * 停止扫描
     */
    void stopScan();

    /**
     * 获取扫描设备列表
     * @return 设备列表
     */
    ScanDataVO scanDevices();
}
