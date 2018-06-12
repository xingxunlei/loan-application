package com.loan_manage.service;


import com.loan_entity.manager.LoanCompany;

import java.util.List;

public interface LoanCompanyService {

    /**
     * 查询所有公司
     * @return
     */
    List<LoanCompany> selectAllLoanCompanys();
}
