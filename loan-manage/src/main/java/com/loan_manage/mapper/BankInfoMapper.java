package com.loan_manage.mapper;

import com.loan_entity.app.Bank;
import com.loan_entity.app.BankVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BankInfoMapper extends Mapper<Bank> {

    BankVo selectBankInfos(Map<String,Object> map);
    
    
    List<BankVo> selectBankInfosByPerId(Map<String,Object> map);

    /**
     * 查询用户主卡
     * @param userId
     * @return
     */
    BankVo selectMainBankByUserId(Integer userId);
}
