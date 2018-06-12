package com.loan_manage.entity;

import com.loan_entity.enums.BorrowStatusEnum;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

public class LoansRemarkVo implements Serializable {
    @Excel(name="逾期天数")
    private String bedueDays;
    @Excel(name="姓名")
    private String customerName;
    private String customerId;
    @Excel(name="身份证",width = 25)
    private String customerIdValue;
    @Excel(name="手机号码",width = 15)
    private String customerMobile;
    private String productId;
    @Excel(name="产品类型")
    private String productName;
    @Excel(name="贷款金额")
    private String amount;
    @Excel(name="应还利息")
    private String totalInterest;
    @Excel(name="应还违约金")
    private String penalty;
    @Excel(name="应还罚息")
    private String penaltyInterest;
    @Excel(name="应还合计")
    private String sumAmount;
    @Excel(name="剩余还款总额")
    private String surplusTotalAmount;
    @Excel(name="到期日")
    private String endDateString;
    @Excel(name="结清日",width = 30)
    private String settleDateString;
    @Excel(name="借款状态")
    private String stateString;
    @Excel(name="催收人")
    private String auditer;
    @Excel(name="最新催收时间",width = 25)
    private String lastCallDateString;
    @Excel(name="合同编号",width = 20)
    private String contractID;
    private String contractKey;
    @Excel(name="放款金额")
    private String loanAmount;
    private String repayAmount;
    @Excel(name = "催款备注",width = 30)
    private String remark;
    @Excel(name = "添加备注时间",width = 25)
    private String lastCall;
    @Excel(name = "添加备注人")
    private String callName;

    public String getBedueDays() {
        return bedueDays;
    }

    public void setBedueDays(String bedueDays) {
        this.bedueDays = bedueDays;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerIdValue() {
        return customerIdValue;
    }

    public void setCustomerIdValue(String customerIdValue) {
        this.customerIdValue = customerIdValue;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(String totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getPenaltyInterest() {
        return penaltyInterest;
    }

    public void setPenaltyInterest(String penaltyInterest) {
        this.penaltyInterest = penaltyInterest;
    }

    public String getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(String sumAmount) {
        this.sumAmount = sumAmount;
    }

    public String getSurplusTotalAmount() {
        return surplusTotalAmount;
    }

    public void setSurplusTotalAmount(String surplusTotalAmount) {
        this.surplusTotalAmount = surplusTotalAmount;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public String getSettleDateString() {
        return settleDateString;
    }

    public void setSettleDateString(String settleDateString) {
        this.settleDateString = settleDateString;
    }

    public String getStateString() {
        return stateString;
    }

    public void setStateString(String stateString) {
        String desc = BorrowStatusEnum.getDescByCode(stateString);
        if(StringUtils.isNotBlank(desc)){
            this.stateString = desc;
        }else{
            this.stateString = stateString;
        }
    }

    public String getAuditer() {
        return auditer;
    }

    public void setAuditer(String auditer) {
        this.auditer = auditer;
    }

    public String getLastCallDateString() {
        return lastCallDateString;
    }

    public void setLastCallDateString(String lastCallDateString) {
        this.lastCallDateString = lastCallDateString;
    }

    public String getContractID() {
        return contractID;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    public String getContractKey() {
        return contractKey;
    }

    public void setContractKey(String contractKey) {
        this.contractKey = contractKey;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLastCall() {
        return lastCall;
    }

    public void setLastCall(String lastCall) {
        this.lastCall = lastCall;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }
}
