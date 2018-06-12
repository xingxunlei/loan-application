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
public class AuditLoanDo {

    /**
     * 审核人员id
     */
    private Integer id;
    /**
     * 审核人员
     */
    private String emplloyeeName;
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
}
