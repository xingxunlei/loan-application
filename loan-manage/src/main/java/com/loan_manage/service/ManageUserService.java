package com.loan_manage.service;

import com.github.pagehelper.PageInfo;
import com.loan_manage.entity.Result;

import java.util.List;
import java.util.Map;

public interface ManageUserService {

	List getusers(Map<String, String[]> args);

	List getaudits(Map<String, String[]> parameterMap);

	List getauditsforUser(Map<String, String[]> parameterMap);

	List getManuallyReview(Map<String, String[]> parameterMap, int offset, int size, boolean count);

	List getSource(String code);

	PageInfo<Map<String,Object>> selectAudits(Map<String,Object> queryParams,Integer start, Integer size, String userNo);

	/**
	 * 风控审核报表
	 * @param map
	 * @return
	 */
	List manualAuditReport(Map map);

	/**
	 * 获取风控审核人员
	 * @param
	 * @return
	 */
	List getRiewer();
}
