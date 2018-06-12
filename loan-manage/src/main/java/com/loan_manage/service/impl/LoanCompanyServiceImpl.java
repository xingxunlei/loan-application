package com.loan_manage.service.impl;

import com.loan_entity.manager.LoanCompany;
import com.loan_manage.mapper.LoanCompanyMapper;
import com.loan_manage.service.LoanCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公司相关
 */
@Service
public class LoanCompanyServiceImpl implements LoanCompanyService {
    @Autowired
    private LoanCompanyMapper loanCompanyMapper;

    @Override
    public List<LoanCompany> selectAllLoanCompanys() {
        return loanCompanyMapper.selectAll();
    }
}
