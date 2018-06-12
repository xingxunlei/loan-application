package com.loan_server.app_mapper;

import java.util.List;
import java.util.Map;

import com.loan_entity.app.DsAppReport;

public interface DsAppReportMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DsAppReport record);

    int insertSelective(DsAppReport record);

    DsAppReport selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DsAppReport record);

    int updateByPrimaryKey(DsAppReport record);

	List<DsAppReport> getAll(Map<String, Object> args);

	List<DsAppReport> getBadBorrows();

	List<DsAppReport> getGoodBorrows();
}