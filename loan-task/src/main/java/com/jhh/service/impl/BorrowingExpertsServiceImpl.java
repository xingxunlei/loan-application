package com.jhh.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jhh.dao.BorrowListMapper;
import com.jhh.dao.PersonMapper;
import com.jhh.model.BorrowingExpertsOrder;
import com.jhh.model.BorrowingExpertsPerson;
import com.jhh.service.BorrowingExpertsService;
import com.jhh.util.Assertion;

/**
 * 借款专家业务类
 * @author wanzezhong
 * 2017年5月2日 17:12:22
 */
@Service("borrowingExpertsService")
public class BorrowingExpertsServiceImpl implements BorrowingExpertsService{
	
	@Autowired
	private BorrowListMapper borrowListMapper;
	
	@Autowired
	private PersonMapper personMapper;
	
	@Override
	public List<BorrowingExpertsPerson> pushUser(String userSource) {
		Assertion.notEmpty(userSource, "用户来源不能为空");
		List<BorrowingExpertsPerson> data =  personMapper.getPhoneAndCreateDate(userSource);
		return data;
	}

	@Override
	public List<BorrowingExpertsOrder> pushOrder(String userSource) {
		Assertion.notEmpty(userSource, "用户来源不能为空");
		List<BorrowingExpertsOrder> data = borrowListMapper.getBorrowingOrder(userSource);
		return data;
	}
}
