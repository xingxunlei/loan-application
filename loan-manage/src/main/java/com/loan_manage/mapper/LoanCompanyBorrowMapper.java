package com.loan_manage.mapper;


import com.loan_entity.manager.LoanCompanyBorrow;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface LoanCompanyBorrowMapper extends Mapper<LoanCompanyBorrow> {

    /**
     * 批量更新
     * @param updateCompanyBorrow
     * @return
     */
    int batchUpdate(List<LoanCompanyBorrow> updateCompanyBorrow);

    /**
     * 批量插入
     * @param insertCompanyBorrow
     * @return
     */
    int batchInsert(List<LoanCompanyBorrow> insertCompanyBorrow);
}
