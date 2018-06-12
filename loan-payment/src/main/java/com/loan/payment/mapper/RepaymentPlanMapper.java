package com.loan.payment.mapper;

import com.loan_entity.manager.RepaymentPlan;
import com.loan_entity.manager_vo.RepaymentPlanVo;
import tk.mybatis.mapper.common.Mapper;

public interface RepaymentPlanMapper extends Mapper<RepaymentPlan> {

    RepaymentPlan selectOnePlanByContractId(Integer contractId);
    
    int updateByBrroNum(RepaymentPlanVo record);

    Double selectAlsoRepay(String borrId);
}