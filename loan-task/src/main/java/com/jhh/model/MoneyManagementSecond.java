package com.jhh.model;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2017年11月7日 12:00:35.
 */
@ExcelTarget("moneyManagementSecond")
public class MoneyManagementSecond implements Serializable{
    @Excel(name="散标名称", orderNum="1")
    private String spreadName ;
    @Excel(name="借款对象", orderNum="2")
    private String loanCustom;
    @Excel(name="借款金额", orderNum="3")
    private String borrAmount;
    @Excel(name="预期年化收益率", orderNum="4")
    private String expectedAnnualized ;
    @Excel(name="还款方式", orderNum="5")
    private String modeRepayment;
    @Excel(name="借款期限", orderNum="6")
    private String lifeLoan;
    @Excel(name="期限类型", orderNum="7")
    private String termType;
    @Excel(name="起投金额", orderNum="8")
    private String investmentAmount;
    @Excel(name="投资上限金额", orderNum="9")
    private String investmentCeilingAmount;
    @Excel(name="投资密码", orderNum="10")
    private String investorPassword;
    @Excel(name="真实姓名", orderNum="11")
    private String realName ;
    @Excel(name="性别", orderNum="12")
    private String sex;
    @Excel(name="年龄", orderNum="13")
    private String age;
    @Excel(name="文化程度", orderNum="14")
    private String education;
    @Excel(name="婚姻状况", orderNum="15")
    private String isMarry;
    @Excel(name="职业", orderNum="16")
    private String profession;
    @Excel(name="借款用途", orderNum="17")
    private String purposeLoan;
    @Excel(name="月收入", orderNum="18")
    private String monthlypay;
    @Excel(name="住房条件", orderNum="19")
    private String housingConditions;
    @Excel(name="是否购车", orderNum="20")
    private String isCar;
    @Excel(name="身份证", orderNum="21")
    private String idCard;
    @Excel(name="户口本", orderNum="22")
    private String residenceBooklet;
    @Excel(name="征信报告", orderNum="23")
    private String creditReport;
    @Excel(name="用户/对公银行流水", orderNum="24")
    private String publicBankNo;
    @Excel(name="工作证明", orderNum="25")
    private String jobLetter;
    @Excel(name="婚姻证明", orderNum="26")
    private String proofMarriage;
    @Excel(name="借款描述", orderNum="27")
    private String borrDescribe;
    @Excel(name="借款对象手机号", orderNum="28")
    private String loanPhone;
    @Excel(name="身份证号", orderNum="29")
    private String idCardNum;

    public String getSpreadName() {
        return spreadName;
    }

    public void setSpreadName(String spreadName) {
        this.spreadName = spreadName;
    }

    public String getLoanCustom() {
        return loanCustom;
    }

    public void setLoanCustom(String loanCustom) {
        this.loanCustom = loanCustom;
    }

    public String getBorrAmount() {
        return borrAmount;
    }

    public void setBorrAmount(String borrAmount) {
        this.borrAmount = borrAmount;
    }

    public String getExpectedAnnualized() {
        return expectedAnnualized;
    }

    public void setExpectedAnnualized(String expectedAnnualized) {
        this.expectedAnnualized = expectedAnnualized;
    }

    public String getModeRepayment() {
        return modeRepayment;
    }

    public void setModeRepayment(String modeRepayment) {
        this.modeRepayment = modeRepayment;
    }

    public String getLifeLoan() {
        return lifeLoan;
    }

    public void setLifeLoan(String lifeLoan) {
        this.lifeLoan = lifeLoan;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(String investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public String getInvestmentCeilingAmount() {
        return investmentCeilingAmount;
    }

    public void setInvestmentCeilingAmount(String investmentCeilingAmount) {
        this.investmentCeilingAmount = investmentCeilingAmount;
    }

    public String getInvestorPassword() {
        return investorPassword;
    }

    public void setInvestorPassword(String investorPassword) {
        investorPassword = investorPassword;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getIsMarry() {
        return isMarry;
    }

    public void setIsMarry(String isMarry) {
        this.isMarry = isMarry;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getPurposeLoan() {
        return purposeLoan;
    }

    public void setPurposeLoan(String purposeLoan) {
        this.purposeLoan = purposeLoan;
    }

    public String getMonthlypay() {
        return monthlypay;
    }

    public void setMonthlypay(String monthlypay) {
        this.monthlypay = monthlypay;
    }

    public String getHousingConditions() {
        return housingConditions;
    }

    public void setHousingConditions(String housingConditions) {
        this.housingConditions = housingConditions;
    }

    public String getIsCar() {
        return isCar;
    }

    public void setIsCar(String isCar) {
        this.isCar = isCar;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getResidenceBooklet() {
        return residenceBooklet;
    }

    public void setResidenceBooklet(String residenceBooklet) {
        this.residenceBooklet = residenceBooklet;
    }

    public String getCreditReport() {
        return creditReport;
    }

    public void setCreditReport(String creditReport) {
        this.creditReport = creditReport;
    }

    public String getPublicBankNo() {
        return publicBankNo;
    }

    public void setPublicBankNo(String publicBankNo) {
        this.publicBankNo = publicBankNo;
    }

    public String getJobLetter() {
        return jobLetter;
    }

    public void setJobLetter(String jobLetter) {
        this.jobLetter = jobLetter;
    }

    public String getProofMarriage() {
        return proofMarriage;
    }

    public void setProofMarriage(String proofMarriage) {
        this.proofMarriage = proofMarriage;
    }

    public String getBorrDescribe() {
        return borrDescribe;
    }

    public void setBorrDescribe(String borrDescribe) {
        this.borrDescribe = borrDescribe;
    }

    public String getLoanPhone() {
        return loanPhone;
    }

    public void setLoanPhone(String loanPhone) {
        this.loanPhone = loanPhone;
    }


}
