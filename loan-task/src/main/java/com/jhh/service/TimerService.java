package com.jhh.service;

public interface TimerService {
	/**
	 * 还款短信
	 */
	void smsAlert();

	/**
	 * 机器人上传文件
	 */
	void sendRobotData();

	/**
	 * 资金端发送报表
	 */
	void sendMoneyManagement();

	/**
	 *  短信提醒用户继续认证
     */
	void personSmsRemind();

	/**
	 * 海尔每日还款流水给财务
	 */
	void sendHaierOrder();
}
