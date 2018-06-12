package com.jhh.model;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2017/9/22.
 */
@ExcelTarget("moneyManagement")
public class MoneyManagement implements Serializable{
    @Excel(name="ID", orderNum="1")
    private Integer id ;
    @Excel(name="出借编号", orderNum="2")
    private String borrNum;
    @Excel(name="放款日期", orderNum="3")
    private String payDate;
    @Excel(name="客户姓名", orderNum="4")
    private String name;
    @Excel(name="客户身份证号", orderNum="5")
    private String cardNum;
    @Excel(name="合同金额", orderNum="6")
    private String monthQuota;
    @Excel(name="放款金额", orderNum="7")
    private String borrAmount;
    @Excel(name="利息", orderNum="8")
    private String interest;
    @Excel(name="本金", orderNum="9")
    private String monthMoney;
    @Excel(name="本息", orderNum="10")
    private String principalInterest;
    @Excel(name="贷款期限(天）", orderNum="11")
    private String lengthMaturity;
    @Excel(name="签订日", orderNum="12")
    private String makeborrDate;
    @Excel(name="交易日", orderNum="13")
    private String interestAccrual;
    @Excel(name="计息日", orderNum="14")
    private String datedDate;
    @Excel(name="到期日", orderNum="15")
    private String planrepayDate;
    @Excel(name="开户行", orderNum="16")
    private String bankName;
    @Excel(name="开户名", orderNum="17")
    private String accountName;
    @Excel(name="开户卡号", orderNum="18")
    private String bankNum;
    @Excel(name="手机号码", orderNum="19")
    private String phone;
    @Excel(name="产品类型", orderNum="20")
    private String productName;
    @Excel(name="收回金额", orderNum="21")
    private String recoverableAmount;
    @Excel(name="平台管理费", orderNum="22")
    private String platformAmount;
    @Excel(name="信审费", orderNum="23")
    private String auditAmount;
    @Excel(name="优惠金额", orderNum="24")
    private String discountsAmount;
    @Excel(name="订单编号", orderNum="25")
    private String serialNo;
    @Excel(name="员工编号", orderNum="26")
    private String employNum;
    @Excel(name="员工姓名", orderNum="27")
    private String emplloyeeName;
    @Excel(name="渠道", orderNum="28")
    private String meaning;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBorrNum() {
        return borrNum;
    }

    public void setBorrNum(String borrNum) {
        this.borrNum = borrNum;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getMonthQuota() {
        return monthQuota;
    }

    public void setMonthQuota(String monthQuota) {
        this.monthQuota = monthQuota;
    }

    public String getBorrAmount() {
        return borrAmount;
    }

    public void setBorrAmount(String borrAmount) {
        this.borrAmount = borrAmount;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getMonthMoney() {
        return monthMoney;
    }

    public void setMonthMoney(String monthMoney) {
        this.monthMoney = monthMoney;
    }

    public String getPrincipalInterest() {
        return principalInterest;
    }

    public void setPrincipalInterest(String principalInterest) {
        this.principalInterest = principalInterest;
    }

    public String getLengthMaturity() {
        return lengthMaturity;
    }

    public void setLengthMaturity(String lengthMaturity) {
        this.lengthMaturity = lengthMaturity;
    }

    public String getMakeborrDate() {
        return makeborrDate;
    }

    public void setMakeborrDate(String makeborrDate) {
        this.makeborrDate = makeborrDate;
    }

    public String getInterestAccrual() {
        return interestAccrual;
    }

    public void setInterestAccrual(String interestAccrual) {
        this.interestAccrual = interestAccrual;
    }

    public String getDatedDate() {
        return datedDate;
    }

    public void setDatedDate(String datedDate) {
        this.datedDate = datedDate;
    }

    public String getPlanrepayDate() {
        return planrepayDate;
    }

    public void setPlanrepayDate(String planrepayDate) {
        this.planrepayDate = planrepayDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRecoverableAmount() {
        return recoverableAmount;
    }

    public void setRecoverableAmount(String recoverableAmount) {
        this.recoverableAmount = recoverableAmount;
    }

    public String getPlatformAmount() {
        return platformAmount;
    }

    public void setPlatformAmount(String platformAmount) {
        this.platformAmount = platformAmount;
    }

    public String getAuditAmount() {
        return auditAmount;
    }

    public void setAuditAmount(String auditAmount) {
        this.auditAmount = auditAmount;
    }

    public String getDiscountsAmount() {
        return discountsAmount;
    }

    public void setDiscountsAmount(String discountsAmount) {
        this.discountsAmount = discountsAmount;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getEmployNum() {
        return employNum;
    }

    public void setEmployNum(String employNum) {
        this.employNum = employNum;
    }

    public String getEmplloyeeName() {
        return emplloyeeName;
    }

    public void setEmplloyeeName(String emplloyeeName) {
        this.emplloyeeName = emplloyeeName;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
