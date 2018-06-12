package com.loan_manage.mapper;

import com.loan_manage.entity.AuditLoanDo;
import com.loan_manage.entity.AuditLoanVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 2017/11/20.
 */
public interface AuditLoanMapper {

    public List<AuditLoanDo> getAuditLoanAll(Map map);
}
