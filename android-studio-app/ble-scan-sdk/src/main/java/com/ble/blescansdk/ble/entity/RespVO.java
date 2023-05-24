package com.ble.blescansdk.ble.entity;

public class RespVO {
    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;

    private final int code;

    private Object data;

    private final Integer errorMsg;

    public RespVO(int code, Integer errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public static RespVO success() {
        return new RespVO(SUCCESS_CODE, null);
    }

    public static RespVO success(Object data) {
        RespVO result = new RespVO(SUCCESS_CODE, null);
        result.setData(data);
        return result;
    }

    public static RespVO fail(int errorCode, Integer errorMsg) {
        return new RespVO(errorCode, errorMsg);
    }


    public static RespVO fail() {
        return new RespVO(ERROR_CODE, null);
    }


    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
