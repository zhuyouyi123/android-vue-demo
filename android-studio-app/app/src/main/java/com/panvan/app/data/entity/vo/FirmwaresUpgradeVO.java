package com.panvan.app.data.entity.vo;

public class FirmwaresUpgradeVO {

    private String key;

    private Integer value;

    private String errorMsg;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public static FirmwaresUpgradeVO success(String key) {
        FirmwaresUpgradeVO firmwaresUpgradeVO = new FirmwaresUpgradeVO();
        firmwaresUpgradeVO.setKey(key);
        return firmwaresUpgradeVO;
    }

    public static FirmwaresUpgradeVO success(String key,Integer value) {
        FirmwaresUpgradeVO firmwaresUpgradeVO = new FirmwaresUpgradeVO();
        firmwaresUpgradeVO.setKey(key);
        firmwaresUpgradeVO.setValue(value);
        return firmwaresUpgradeVO;
    }

    public static FirmwaresUpgradeVO failed(String key,String errorMsg) {
        FirmwaresUpgradeVO firmwaresUpgradeVO = new FirmwaresUpgradeVO();
        firmwaresUpgradeVO.setKey(key);
        firmwaresUpgradeVO.setErrorMsg(errorMsg);
        return firmwaresUpgradeVO;
    }
}
