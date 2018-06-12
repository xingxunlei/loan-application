package com.loan_manage.service;


public interface PageQueryService {
    /**
     * 贷后管理特定参数的总条数
     */
    void initLoanManagementInfoItem();

    /**
     *还款流水特定参数的总条数
     */
    void initRepaymentInfoItem();

    /**
     * 还款计划特定参数的总条数
     */
    void initRepaymentPlanInfoItem();

    /**
     * 催收信息特定参数的总条数
     */
    void initCollectorsInfoItem();

    void initAuthManageInfoItem();
}
