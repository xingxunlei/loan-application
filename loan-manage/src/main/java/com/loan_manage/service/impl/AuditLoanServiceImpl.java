package com.loan_manage.service.impl;

import com.loan_manage.entity.AuditLoanDo;
import com.loan_manage.entity.AuditLoanVo;
import com.loan_manage.mapper.AuditLoanMapper;
import com.loan_manage.service.AuditLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 2017/11/20.
 */
@Service
public class AuditLoanServiceImpl implements AuditLoanService{

    @Autowired
    private AuditLoanMapper auditLoanMapper;

    @Override
    public List<AuditLoanDo> getAuditLoanAll(Map map) {
        List<AuditLoanDo> ado = auditLoanMapper.getAuditLoanAll(map);
        int limit = ado.stream().map(AuditLoanDo::getLoanLimit).reduce(0,Integer::sum);
        double borrAmount = ado.stream().map(AuditLoanDo::getBorrAmount).reduce(0.00,Double::sum);
        double monthQuota = ado.stream().map(AuditLoanDo::getMonthQuota).reduce(0.00,Double::sum);
        //求合计
        AuditLoanDo ldo = new AuditLoanDo();
        ldo.setEmplloyeeName("合计");
        ldo.setLoanLimit(limit);
        ldo.setBorrAmount(borrAmount);
        ldo.setMonthQuota(monthQuota);
        ado.add(ldo);
        return ado;
    }
}
