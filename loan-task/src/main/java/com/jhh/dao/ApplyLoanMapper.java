package com.jhh.dao;

import com.jhh.model.ApplyLoanVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  贝壳钱包 审核
 */
public interface ApplyLoanMapper {

    List<ApplyLoanVo> getBorrowListByPerId(@Param("source") List source);
}
