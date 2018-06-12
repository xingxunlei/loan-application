package com.jhh.service;

import java.util.List;

import com.jhh.model.BorrowingExpertsOrder;
import com.jhh.model.BorrowingExpertsPerson;

/**
 * 借款专家合推送接口
 * @author wanzezhong
 * 2017年5月2日 17:08:50
 */
public interface BorrowingExpertsService {

	/**
	 * 推送借款专家用户
	 * @param userSource
	 */
	public List<BorrowingExpertsPerson> pushUser(String userSource);
	
	/**
	 * 推送借款专家订单
	 * @param userSource
	 */
	public List<BorrowingExpertsOrder> pushOrder(String userSource);
}
