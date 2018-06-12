package com.loan_manage.service;

import com.loan_entity.loan.AutoLoanList;
import com.loan_entity.loan.AutoLoanRuler;

import java.util.List;

/**
 * 自动放款Service
 * Created by chenchao on 2017/11/1.
 */
public interface AutoLoanService {

    /**
     * 获取当前自动放款状态
     * @return
     */
    AutoLoanList getCurrentStatus();

    /**
     * 获取自动放款规则
     * @return
     */
    List<AutoLoanRuler> getAutoLoanRulers();

    /**
     * 插入新的自动放款状态
     * @param autoLoanStatus
     * @return
     */
    int insertAutoLoanStatus(AutoLoanList autoLoanStatus);

    /**
     * 更新自动放款规则
     * @param ruler
     */
    void updateAutoLoanRulers(List<AutoLoanRuler> ruler);
}
