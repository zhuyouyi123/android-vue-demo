package com.seekcy.bracelet.response;

/**
 * describe:
 * author:jpw
 * Date:2019/12/30
 * Time:16:22
 */
public enum StatusCode {
	Success(0,"成功"),
	Fail(-1,"失败");

	private int code;
	private String msg;

	StatusCode(int code,String msg){
		this.code=code;
		this.msg=msg;
	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
