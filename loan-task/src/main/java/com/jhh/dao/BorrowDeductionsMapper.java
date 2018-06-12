package com.jhh.dao;

import com.jhh.model.BorrowDeductions;

import java.util.List;


public interface BorrowDeductionsMapper{

    /**
     * 查询全量数据
     * @return
     */
    List<BorrowDeductions> selectAllData();

    /**
     * 创建数据
     * @param borrowDeductions
     * @return
     */
    int createBorrowDeductions(List<BorrowDeductions> borrowDeductions);

    /**
     * 查询昨天流水数据
     * @return
     */
    List<BorrowDeductions> selectByYesterdayOrder();

    /**
     * 查询银行卡
     * @return
     */
    List<Integer> selectIdByBankId();

    /**
     * 更新错误状态
     * @param ids
     * @return
     */
    int saveFailSatuts(List<Integer> ids);

    /**
     * 更新昨天流水状态
     * @param borrowDeductions
     * @return
     */
    int saveByYesterdayOrder(List<BorrowDeductions> borrowDeductions);

}