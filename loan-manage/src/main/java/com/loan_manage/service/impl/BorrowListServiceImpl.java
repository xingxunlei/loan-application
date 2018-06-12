package com.loan_manage.service.impl;


import com.loan_entity.app.BorrowList;
import com.loan_manage.mapper.BorrowListMapper;
import com.loan_manage.service.BorrowListService;
import com.loan_manage.service.RedisService;
import com.loan_manage.service.RobotService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("borrowListService")
public class BorrowListServiceImpl implements BorrowListService {

	private Logger logger = Logger.getLogger(BorrowListServiceImpl.class);

	@Autowired
	private BorrowListMapper borrowListMapper;
	@Autowired
	private RobotService robotService;

	@Override
	public void rejectAudit() {
		//自动拒绝人工审核订单
		borrowListMapper.rejectAudit();
	}

	@Override
	public void rcCallPhone() {
		List<BorrowList> borrowLists = borrowListMapper.selectUnBaikelu();
		if(borrowLists != null){
			for(BorrowList borrowList : borrowLists){
				try {
					robotService.sendRcOrder(borrowList.getId());
				} catch (Exception e) {
					logger.error("百可录打电话失败ID：" + borrowList.getId() );
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public Map queryLastBorrowList(Integer per_id) {
		return borrowListMapper.selectNow(per_id);
	}
}
