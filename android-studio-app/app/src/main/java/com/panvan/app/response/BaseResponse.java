package com.panvan.app.response;

import java.util.UUID;

/**
 * describe:
 * author:jpw
 * Date:2019/12/30
 * Time:16:19
 */
public class BaseResponse<T> {

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应描述
     */
    private String msg;
    /**
     * 响应数据
     */
    private T data;
    /**
     * token
     */
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseResponse() {
        this.code = StatusCode.Success.getCode();
        this.token = UUID.randomUUID().toString();
    }

    public BaseResponse(StatusCode code, String msg) {
        this.code = code.getCode();
        this.token = UUID.randomUUID().toString();
        this.msg = msg;
    }

    public BaseResponse(StatusCode code, String msg, T data) {
        this.code = code.getCode();
        this.token = UUID.randomUUID().toString();
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse(StatusCode code, T data, String token) {
        this.code = code.getCode();
        this.token = UUID.randomUUID().toString();
        this.data = data;
        this.token = token;
    }

    public BaseResponse(StatusCode code, String msg, T data, String token) {
        this.code = code.getCode();
        this.token = UUID.randomUUID().toString();
        this.msg = msg;
        this.data = data;
        this.token = token;
    }


    public BaseResponse(String msg) {
        this.code = StatusCode.Success.getCode();
        this.token = UUID.randomUUID().toString();
        this.msg = msg;
    }

    public BaseResponse(T data) {
        this.code = StatusCode.Success.getCode();
        this.token = UUID.randomUUID().toString();
        this.data = data;
    }

    public BaseResponse(StatusCode code, T data) {
        this.code = code.getCode();
        this.token = UUID.randomUUID().toString();
        this.data = data;
    }


    @Override
    public String toString() {
        return "{" +
                "code:" + code +
                ", msg:'" + msg + '\'' +
                ", data:" + data +
                ", token:'" + token + '\'' +
                '}';
    }
}


