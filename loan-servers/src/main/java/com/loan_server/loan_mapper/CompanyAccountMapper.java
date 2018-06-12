package com.loan_server.loan_mapper;

import com.loan_entity.loan.CompanyAccount;

public interface CompanyAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CompanyAccount record);

    int insertSelective(CompanyAccount record);

    CompanyAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CompanyAccount record);

    int updateByPrimaryKey(CompanyAccount record);
    
    //根据company_id查询账户
    CompanyAccount selectByComId(Integer company_id);
    
}