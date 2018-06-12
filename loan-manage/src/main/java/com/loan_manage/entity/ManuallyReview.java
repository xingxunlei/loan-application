package com.loan_manage.entity;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2017/9/27.
 */
@ExcelTarget("RobotData")
public class ManuallyReview implements Serializable{
    @Excel(name = "合同编号",width = 30, orderNum = "1")
    private String borr_num;
    private String id;
    @Excel(name = "姓名",width = 30, orderNum = "2")
    private String name;
    @Excel(name = "身份证号码", width = 30, orderNum = "3")
    private String card_num;
    @Excel(name = "手机号码", width = 30, orderNum = "4")
    private String phone;
    @Excel(name = "产品类型", width = 30, orderNum = "6")
    private String product_name;
    @Excel(name = "贷款金额", width = 30, orderNum = "7")
    private String maximum_amount;
    @Excel(name = "银行名称", width = 30, orderNum = "8")
    private String bank_name;
    @Excel(name = "银行卡号", width = 30, orderNum = "9")
    private String bank_num;
    @Excel(name = "贷款状态", width = 30, orderNum = "10")
    private String borr_status;
    @Excel(name = "拒绝理由", width = 30, orderNum = "12")
    private String reason;
    private String employ_num;
    @Excel(name = "审核人", width = 30, orderNum = "14")
    private String emplloyee_name;
    private String borr_id;
    private String per_id;
    private String makeborr_date;
    private String meaning;
    @Excel(name = "上单状态", width = 30, orderNum = "11")
    private String up_status;
    @Excel(name = "认证说明", width = 30, orderNum = "13")
    private String description;
    @Excel(name = "通讯录个数",width = 30,  orderNum = "5")
    private String contactNum;
    private String isManual;
    private String state;
    @Excel(name = "审核时间", width = 30, orderNum = "15")
    private String auditTime;
    @Excel(name = "签约时间",width = 30,  orderNum = "16")
    private String createDate;

    public String getBorr_num() {
        return borr_num;
    }

    public void setBorr_num(String borr_num) {
        this.borr_num = borr_num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getMaximum_amount() {
        return maximum_amount;
    }

    public void setMaximum_amount(String maximum_amount) {
        this.maximum_amount = maximum_amount;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_num() {
        return bank_num;
    }

    public void setBank_num(String bank_num) {
        this.bank_num = bank_num;
    }

    public String getBorr_status() {
        return borr_status;
    }

    public void setBorr_status(String borr_status) {
        this.borr_status = borr_status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEmploy_num() {
        return employ_num;
    }

    public void setEmploy_num(String employ_num) {
        this.employ_num = employ_num;
    }

    public String getEmplloyee_name() {
        return emplloyee_name;
    }

    public void setEmplloyee_name(String emplloyee_name) {
        this.emplloyee_name = emplloyee_name;
    }

    public String getBorr_id() {
        return borr_id;
    }

    public void setBorr_id(String borr_id) {
        this.borr_id = borr_id;
    }

    public String getPer_id() {
        return per_id;
    }

    public void setPer_id(String per_id) {
        this.per_id = per_id;
    }

    public String getMakeborr_date() {
        return makeborr_date;
    }

    public void setMakeborr_date(String makeborr_date) {
        this.makeborr_date = makeborr_date;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getUp_status() {
        return up_status;
    }

    public void setUp_status(String up_status) {
        this.up_status = up_status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getIsManual() {
        return isManual;
    }

    public void setIsManual(String isManual) {
        this.isManual = isManual;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
