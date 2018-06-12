package com.loan_server.manager_mapper;

import com.loan_entity.manager.RepaymentPlan;
import com.loan_entity.manager_vo.RepaymentPlanVo;

public interface RepaymentPlanMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RepaymentPlan record);

    int insertSelective(RepaymentPlan record);

    RepaymentPlan selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RepaymentPlan record);

    int updateByPrimaryKey(RepaymentPlan record);
    
    RepaymentPlan selectOnePlanByContractId(Integer contractId);
    
    int updateByBrroNum(RepaymentPlanVo record);

    Double selectAlsoRepay(String borrId);

    double selectCanPay(String borrId);
}