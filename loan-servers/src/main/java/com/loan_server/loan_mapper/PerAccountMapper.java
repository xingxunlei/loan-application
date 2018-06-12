package com.loan_server.loan_mapper;

import com.loan_entity.loan.PerAccount;

public interface PerAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PerAccount record);

    int insertSelective(PerAccount record);

    PerAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PerAccount record);

    int updateByPrimaryKey(PerAccount record);
}