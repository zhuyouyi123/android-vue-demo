package com.seek.config.entity.response;

import java.util.Arrays;
import java.util.List;

public class RespVO<T>  {

    private static final int DEFAULT_SUCCESS_CODE = 0;
    private static final int DEFAULT_ERROR_CODE = -1;
    private static final String DEFAULT_SUCCESS_MSG = "success";

    /**
     * 错误code
     */
    private Integer errorCode;
    /**
     * 错误信息
     */
    private List<String> errorMsg;
    /**
     * 数据
     */
    private T data;

    private RespVO(Integer errorCode, String... errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = Arrays.asList(errorMsg);
    }


    /**
     * 成功
     *
     * @param <T> 数据类
     * @return {@link RespVO}
     */
    public static <T> RespVO<T> success() {
        return new RespVO<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG);
    }

    /**
     * 成功
     *
     * @param <T>  数据类
     * @param data 数据
     * @return {@link RespVO}
     */
    public static <T> RespVO<T> success(T data) {
        RespVO<T> vo = new RespVO<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG);
        vo.setData(data);
        return vo;
    }

    /**
     * 失败
     *
     * @param <T>       数据类
     * @param errorCode 错误code
     * @param errorMsg  错误信息
     * @return {@link RespVO}
     */
    public static <T> RespVO<T> failure(int errorCode, String... errorMsg) {
        return new RespVO<>(errorCode, errorMsg);
    }

    public static <T> RespVO<T> failure( String... errorMsg) {
        return new RespVO<>(DEFAULT_ERROR_CODE, errorMsg);
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(List<String> errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
