package com.loan_web.common.exception;

public class CommonException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String resultCode;

	public CommonException(String resultCode, String resultMessage) {
		super(resultMessage);
		this.resultCode = resultCode;

	}

	public String getResultCode() {
		return resultCode;
	}

}
