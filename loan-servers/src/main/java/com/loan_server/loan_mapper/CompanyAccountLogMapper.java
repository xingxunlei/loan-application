package com.loan_server.loan_mapper;

import com.loan_entity.loan.CompanyAccountLog;

public interface CompanyAccountLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CompanyAccountLog record);

    int insertSelective(CompanyAccountLog record);

    CompanyAccountLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CompanyAccountLog record);

    int updateByPrimaryKey(CompanyAccountLog record);
}