package com.loan_entity.manager_vo;

import com.loan_entity.enums.BorrowStatusEnum;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

//贷后管理VO
public class LoanManagementVo implements Serializable{
    @Excel(name="逾期天数")
    private Integer bedueDays;
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
    @Excel(name="结清日")
    private String settleDateString;
    @Excel(name="借款状态")
    private String stateString;
    @Excel(name="催收人")
    private String auditer;
    @Excel(name="最新催收时间")
    private String lastCallDateString;
    @Excel(name="合同编号",width = 20)
    private String contractID;
    private String contractKey;
    @Excel(name="放款金额")
    private String loanAmount;
    private String repayAmount;
    private String createUser;
    @Excel(name="最新扣款时间")
    private String orderString;
    @Excel(name="是否黑名单")
    private String blackList;
    private Integer bdStatus;
    private String reason;

    public Integer getBdStatus() {
        return bdStatus;
    }

    public void setBdStatus(Integer bdStatus) {
        this.bdStatus = bdStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        if(StringUtils.isBlank(blackList) || StringUtils.equals("N",blackList)){
            this.blackList = "No";
        }else if(StringUtils.equals("Y",blackList)) {
            this.blackList = "Yes";
        }else{
            this.blackList = blackList;
        }
    }

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getContractKey() {
        return contractKey;
    }

    public void setContractKey(String contractKey) {
        this.contractKey = contractKey;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getBedueDays() {
        return bedueDays;
    }

    public void setBedueDays(Integer bedueDays) {
        this.bedueDays = bedueDays;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}
