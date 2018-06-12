package com.loan_manage.mapper;

import java.util.List;
import java.util.Map;

import com.loan_entity.loan.CollectorsList;
import com.loan_entity.loan.ReceiptUsers;
import com.loan_entity.manager.CollectorsListVo;
import com.loan_entity.manager_vo.DebitInfoVo;
import com.loan_entity.manager_vo.LoanManagementVo;
import com.loan_manage.entity.LoansRemarkOutVo;
import com.loan_manage.entity.LoansRemarkVo;
import tk.mybatis.mapper.common.Mapper;

import com.loan_entity.manager.RepaymentPlan;
import com.loan_entity.manager_vo.RepayPlanVo;

public interface RepayPlanMapper extends Mapper<RepaymentPlan> {
    /**
     * 查询还款计划
     * @param args
     * @return
     */
    List<RepayPlanVo> selectRepaymentPlan(Map<String, Object> args);

    /**
     * 查询还款计划条数
     * @param args
     * @return
     */
    Long selectRepaymentPlanItem(Map<String, Object> args);
    /**
     * 查询扣款信息
     * @param arg
     * @return
     */
    List<DebitInfoVo> selectDebitInfo(Map<String, Object> arg);

    /**
     * 贷后信息查询
     * @param arg
     * @return
     */
    List<LoanManagementVo> selectLoanManagementInfo(Map<String, Object> arg);
    
    /**
     * 查询合同还款信息
     * @param borrNum
     * @return
     */
    List<Map<String, Object>> selectReplanAndBorrowList(String borrNum);

    /**
     * 查询催收人员
     * @param arg
     * @return
     */
    List<ReceiptUsers> selectReceiptUsers(Map<String, Object> arg);
    List<ReceiptUsers> selectReceiptUsersNew(Map<String, Object> arg);

    /**
     * 查询催收队列
     * @param arg
     * @return
     */
    List<CollectorsListVo> selectCollectorsListInfo(Map<String, Object> arg);

    /**
     * 查询催收xinxi
     * @param queryMap
     * @return
     */
    List<CollectorsListVo> selectCollectorsInfo(Map<String, Object> queryMap);

    /**
     * 查询催收xinxi item
     * @param queryMap
     * @return
     */
    Long selectCollectorsInfoItem(Map<String, Object> queryMap);
    /**
     * 查询导出条数
     * @param queryMap
     * @return
     */
    int queryExportCount(Map<String, Object> queryMap);

    /**
     * 查询备注信息导出条数
     * @param queryMap
     * @return
     */
    int selectLoansRemarkVoCount(Map<String, Object> queryMap);

    /**
     * 查询备注信息导出
     * @param queryMap
     * @return
     */
    List<LoansRemarkVo> selectLoansRemarkVo(Map<String, Object> queryMap);

    /**
     * 催收备注导出(联查)
     * @param queryMap
     * @return
     */
    List<LoansRemarkVo> selectExportLoansRemarkVo(Map<String,Object> queryMap);

    /**
     * 查询贷后管理总条数
     * @param queryMap
     * @return
     */
    long selectLoanManagementInfoItems(Map<String, Object> queryMap);

    long selectBatchReduceInfoItems(Map<String, Object> queryMap);
    
    /**
     * 按照分页查询
     * @param queryMap
     * @return
     */
    List<LoanManagementVo> selectLoanManagementInfoBySize(Map<String, Object> queryMap);

    List<LoanManagementVo> selectBatchReduceInfoBySize(Map<String, Object> queryMap);

    /**
     * 导出(联查)
     * @param queryMap
     * @return
     */
    List<LoanManagementVo> selectExportData(Map<String,Object> queryMap);

    /**
     * 导出催收备注(联查,外包)
     * @param queryMap
     * @return
     */
    List<LoansRemarkOutVo> selectExportDataForOutWorkers(Map<String,Object> queryMap);
}
