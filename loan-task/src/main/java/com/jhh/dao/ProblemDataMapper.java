package com.jhh.dao;

import java.util.List;
import java.util.Map;

public interface ProblemDataMapper {

	/**
	 * 还款计划是负数
	 */
	List<String> repaymentNegative(Map m);
	/**
	 * 合同未结清，还款计划结清
	 */
	List<String> contractNoDoneRepaymentDone();
	/**
	 * 合同结清，还款计划未结清
	 */
	List<String> contractDoneRepaymentNoDone(Map m);
	/**
	 * 有合同没还款计划
	 * */
	List<String> contractWithoutRepayment(Map m);
	/**
	 * 合同未成立，有还款计划
	 * */
	List<String> contractNoEstablishWithRepayment();
	/**
	 * 多笔同时申请的合同
	 * */
	List<Map> muitiApplyContract(Map m);
	/**
	 * 重复手机号
	 * */
	List<String> repetitivePhone();
	/**
	 * 正常结清和逾期结清中总还款金额不等于应还金额,关注负数金额
	 * */
	List<Map> repaymentAmountNotrepaymentPlan(Map m);
	/**
	 * 一个合同多条还款计划
	 * */
	List<Map> multiRepaymentOfContract(Map m);
	/**
	 * 逾期的状态还是待还款
	 * */
	List<String> exceedLimitAndWaitinRepayment(Map m);
	/**
	 * 多张相同的银行卡
	 * */
	List<String> multiBankNum(Map m);
	/**
	 * 一个合同同时分派给多个人审核
	 * */
	List<String> multiOperatorOfContract();
	/**
	 * 一笔订单放款多次！人工处理不修改数据！
	 * */
	List<String> multiLoanOfOrder(Map m);
	/**
	 * 正常结清实际上是逾期的
	 * */
	List<String> actualExceedLimit(Map m);
	/**
	 * 检查一笔放款或还款，有没有在order表中生成2笔相同订单号的流水
	 * */
	List<String> multiOrderOfLoanAndRepayment(Map m);
	/**
	 * 放款中的合同没有订单流水，要改为放款失败
	 * */
	List<String> noContractOrder(Map m);
	/**
	 * 非已放款、逾期、正常结清、逾期结清的合同是否有放款记录
	 * */
	List<String> unsualLoanOrder(Map m);
	/**
	 * 一个人有重复的个人信息
	 * */
	List<String> multiProfile(Map m);
	/**
	 * 一个人有2张主银行卡
	 * */
	List<String> multiMainCard();
	/**
	 *逾期结清但实际还款日小于应还款日+1天
	 * */
	List<String> exceedRepamentActualNotExceed(Map m);
	/**
	 * 合同是放款中，实际上是放款失败
	 * */
	List<String> contractLoaningAndActualFailed(Map m);
	/**
	 *  已签约的合同没有审核人
	 * */
	List<String> signedContractNoAuditor(Map m);
	/**
	 * 人工审核的合同没有审核人
	 * */
	List<String> manualContractNoAuditor(Map m);
	/**
	 * 逾期结清的贷后催收数据没更新
	 * */
	List<Map> exceedRepaymentNoCollectionUpd(Map m);
}