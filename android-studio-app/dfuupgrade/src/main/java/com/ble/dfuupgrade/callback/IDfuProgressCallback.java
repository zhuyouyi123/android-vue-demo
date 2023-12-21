package com.ble.dfuupgrade.callback;

public interface IDfuProgressCallback {
    /**
     * 当DFU完成时调用
     *
     * @param deviceAddress 设备地址
     */

    void onDfuCompleted(String deviceAddress);

    /**
     * 当DFU被中止时调用
     *
     * @param deviceAddress 设备地址
     */
    void onDfuAborted(String deviceAddress);

    /**
     * 当DFU发生错误时调用
     *
     * @param deviceAddress 设备地址
     * @param error         错误码
     * @param errorType     错误类型
     * @param message       错误信息
     */
    void onError(String deviceAddress, int error, int errorType, String message);

    /**
     * 当DFU进度发生变化时调用
     *
     * @param deviceAddress 设备地址
     * @param percent       完成百分比
     * @param speed         当前速度
     * @param avgSpeed      平均速度
     * @param currentPart   当前部分
     * @param partsTotal    总部分数
     */
    void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal);

    /**
     * 当DFU过程开始时调用
     *
     * @param deviceAddress 设备地址
     */
    void onDfuProcessStarted(String deviceAddress);

    /**
     * 当设备连接时调用
     *
     * @param deviceAddress 设备地址
     */
    void onDeviceConnecting(String deviceAddress);


    void onDeviceConnected(String deviceAddress);

    /**
     * 当设备断开连接时调用
     *
     * @param deviceAddress 设备地址
     */
    void onDeviceDisconnecting(String deviceAddress) ;
    void onDeviceDisconnected(String deviceAddress) ;
    }
