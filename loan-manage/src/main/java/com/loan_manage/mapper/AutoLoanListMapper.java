package com.loan_manage.mapper;

import com.loan_entity.app.Bank;
import com.loan_entity.loan.AutoLoanList;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created by chenchao on 2017/11/1.
 */
public interface AutoLoanListMapper extends Mapper<AutoLoanList> {

    AutoLoanList selectByMax();

}
