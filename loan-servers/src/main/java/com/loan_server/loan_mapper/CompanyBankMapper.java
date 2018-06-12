package com.loan_server.loan_mapper;

import com.loan_entity.loan.CompanyBank;

public interface CompanyBankMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CompanyBank record);

    int insertSelective(CompanyBank record);

    CompanyBank selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CompanyBank record);

    int updateByPrimaryKeyWithBLOBs(CompanyBank record);

    int updateByPrimaryKey(CompanyBank record);
}