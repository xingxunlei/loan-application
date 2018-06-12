package com.loan_server.manager_mapper;

import java.util.List;

import com.loan_entity.manager.BankList;

public interface BankListMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankList record);

    int insertSelective(BankList record);

    BankList selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankList record);

    int updateByPrimaryKey(BankList record);
    
    List<BankList> selectBySupport(String support);
}