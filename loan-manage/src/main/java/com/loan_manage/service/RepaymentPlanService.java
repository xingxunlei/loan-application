package com.loan_manage.service;


import com.github.pagehelper.PageInfo;
import com.loan_entity.manager_vo.DebitInfoVo;
import com.loan_entity.manager_vo.RepayPlanVo;

import java.util.List;
import java.util.Map;

public interface RepaymentPlanService {
    /**
     * 分页获取还款计划
     * @param args 查询参数
     * @return
     */
    PageInfo<RepayPlanVo> selectRepaymentPlan(Map<String, Object> args);

    /**
     * 分页查询扣款信息
     * @return
     */
    PageInfo<DebitInfoVo> selectDebitInfo(Map<String, Object> queryMap);

    /**
     * 分页查询扣款信息
     * @return
     */
    PageInfo selectRepaymentPlan(Map<String, Object> queryMap, int offset, int size, String userNo);
}
