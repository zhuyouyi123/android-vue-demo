package com.seek.config.entity.holder;

/**
 * 信标holder 存储信息
 */
public class SeekStandardDeviceHolder {

    private static SeekStandardDeviceHolder instance = null;


    public SeekStandardDeviceHolder() {

    }

    public static SeekStandardDeviceHolder setInstance() {
        if (null == instance) {
            instance = new SeekStandardDeviceHolder();
        }
        return instance;
    }




    /**
     * 释放资源
     */
    public static void release() {
        instance = null;
    }
}
