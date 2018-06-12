package com.loan_manage.mapper;

import java.util.List;
import java.util.Map;

import com.loan_manage.entity.Riewer;
import tk.mybatis.mapper.common.Mapper;

public interface RiewerMapper extends Mapper<Riewer> {


	List getusers(Map map);
	
	List getusersOld(Map map);
	
	List getusersNew(Map map);

	List getLoanUsersNew(Map map);

	List getaudits(Map map);

	List getauditsforUser(Map map);

	List<Map<String,Object>> selectAudits(Map map);

    Long selectAuditsCount(Map map);

	List getManuallyReview(Map map);

	List getRegisterSource(String soruce);

	int updateByBorrId(Map<String, Object> map);

	int insertReview(Map<String, Object> map);

	List manualAuditReport(Map map);
}