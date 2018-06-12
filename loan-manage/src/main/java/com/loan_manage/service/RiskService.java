package com.loan_manage.service;

import com.loan_manage.entity.Result;

/**
 * Create by Jxl on 2017/9/11
 */
public interface RiskService {
    
    /**
     * 取消借款
     * @Author Jxl
     * @Date 2017/9/11 11:26
     * @param borrowId
     * @param userId
     * @return
     */
     Result cancelLoan(String borrowId,String userId);
}
