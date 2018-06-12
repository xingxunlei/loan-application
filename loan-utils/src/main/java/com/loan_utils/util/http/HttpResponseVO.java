package com.loan_utils.util.http;


import java.io.Serializable;

/**
 * HTTP请求响应值对象
 * 
 * @author chailongjie
 *
 */
public class HttpResponseVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//HTTP请求状态码
	private String status;
	
	//HTTP返回值
	private String out;

	//请求参数
	private String sendParam;
    
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOut() {
		return out;
	}

	public void setOut(String out) {
		this.out = out;
	}

	public String getSendParam() {
		return sendParam;
	}

	public void setSendParam(String sendParam) {
		this.sendParam = sendParam;
	}

	@Override
	public String toString() {
		return "HttpResponseVO{" +
				"status='" + status + '\'' +
				", out='" + out + '\'' +
				", sendParam='" + sendParam + '\'' +
				'}';
	}
}
