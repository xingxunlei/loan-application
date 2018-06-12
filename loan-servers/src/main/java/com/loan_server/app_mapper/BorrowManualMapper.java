package com.loan_server.app_mapper;

import com.loan_entity.app.BorrowManual;

public interface BorrowManualMapper{
    int deleteByPrimaryKey(Integer id);

    int insert(BorrowManual record);

    int insertSelective(BorrowManual record);

    BorrowManual selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BorrowManual record);

    int updateByPrimaryKey(BorrowManual record);

    BorrowManual selectByBorrId(Integer borrId);
}