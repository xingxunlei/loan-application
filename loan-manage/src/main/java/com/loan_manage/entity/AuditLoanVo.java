package com.loan_manage.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 审核放款报表
 */
@Setter
@Getter
@ToString
public class AuditLoanVo {

    /**
     * 开始时间
     */
    private String beginDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 审核人员
     */
    private String employId;

    /**
     * 放款次数
     */
    private Integer loanLimit;
    /**
     * 放款金额
     */
    private Double borrAmount;
    /**
     * 合同金额
     */
    private Double monthQuota;

    private String selector;

    private String desc;
}
