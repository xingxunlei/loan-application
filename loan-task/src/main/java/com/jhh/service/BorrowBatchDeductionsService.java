package com.jhh.service;

/**
 * 批量扣款状态
 * @author wanzezhong
 * 2018年4月2日 14:59:00
 */
public interface BorrowBatchDeductionsService {

	/**
	 * 全量新增批量扣款状态
	 */
	 void creatFullDeductionsStatus();

	/**
	 * 更新农行用户批量扣款状态
	 */
	int saveAgriculturalBankStatus();

	/**
	 * 每日更新批量扣款状态
	 */
	int saveDeductionsStatus();
}
