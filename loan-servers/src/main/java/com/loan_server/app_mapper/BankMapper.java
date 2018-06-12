package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.Bank;

public interface BankMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Bank record);

    int insertSelective(Bank record);

    Bank selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Bank record);

    int updateByPrimaryKey(Bank record);
    
    //得到签约页面的信息
    Bank getSignInfo(Integer per_id);
    
    //更改用户所有银行卡状态
    int updateBankStatus(String status,Integer per_id,String oldStatus);
    
    //查询根据银行卡号查询有子协议号的记录
    Bank selectByBankNum(String bankNum);
    
    //查询用户有子协议号的银行卡
    Bank selectByPerId(String per_id);

    // 查询用户所有有效的银行卡
    List<Bank> selectAllBanks(String per_id);
}