package com.loan_manage.service;

import com.loan_manage.entity.AuditLoanDo;
import com.loan_manage.entity.AuditLoanVo;

import java.util.List;
import java.util.Map;

/**
 * 审核放款报表
 */
public interface AuditLoanService {

    /**
     *  查询审核员放款情况
     * @param map
     * @return
     */
    public List<AuditLoanDo> getAuditLoanAll(Map map);
}
