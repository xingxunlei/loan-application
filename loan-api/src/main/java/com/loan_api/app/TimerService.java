package com.loan_api.app;

public interface TimerService {
	public void smsAlert();
	
	/**
	 * 驳回人工审核
	 */
	public void rejectAudit();

	public void sendRobotData();
}
