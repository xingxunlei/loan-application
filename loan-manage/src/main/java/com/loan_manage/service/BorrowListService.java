package com.loan_manage.service;


import com.loan_entity.app.BorrowList;

import java.util.Map;

public interface BorrowListService {
	/**
	 * 人工审核跑批
	 */
	void rejectAudit();
	/**
	 * 百可录打电话
	 */
	void rcCallPhone();

	Map queryLastBorrowList(Integer per_id);
}
