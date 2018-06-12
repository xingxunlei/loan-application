package com.loan.mq.domain;

import java.io.Serializable;
import java.util.Map;

public class RequestDomain implements Serializable{
	/**
	 * 状态码 错误
	 */
	public static final int ERROR = 500;
	/**
	 * 状态码 正确
	 */

	public static final int SUCCESS = 200;
	/**
	 * 附加信息Map格式
	 */
	private Map<String, String> header;
	/**
	 * 消息体
	 */
	private String body;
	/**
	 * 状态码
	 */
	private int status;

	private static final long serialVersionUID = 5470355440844643751L;
	public RequestDomain(Map<String, String> header, String body, int status) {
		super();
		this.header = header;
		this.body = body;
		this.status = status;
	}

	public RequestDomain() {
		super();
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
