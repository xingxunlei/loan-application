package com.loan_entity.contract;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2017/11/23.
 */
public class IdEntity implements Serializable {

    private String borrNum;
    private String name;
    private String cardNum;
    private String phone;
    private String bankName;
    private String bankNum;
    private String bankPhone;
    private String payDate;
    private Integer prodId;
    private String planRepay;
    private Integer perId;
    private String perCouponId;
    private String email;
    private String borrDate;
    private String planRepayDate;

    public String getPlanRepayDate() {
        return planRepayDate;
    }

    public void setPlanRepayDate(String planRepayDate) {
        this.planRepayDate = planRepayDate;
    }

    public String getBorrDate() {
        return borrDate;
    }

    public void setBorrDate(String borrDate) {
        this.borrDate = borrDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    public String getPlanRepay() {
        return planRepay;
    }

    public void setPlanRepay(String planRepay) {
        this.planRepay = planRepay;
    }

    public Integer getPerId() {
        return perId;
    }

    public void setPerId(Integer perId) {
        this.perId = perId;
    }

    public String getPerCouponId() {
        return perCouponId;
    }

    public void setPerCouponId(String perCouponId) {
        this.perCouponId = perCouponId;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public String getBankPhone() {
        return bankPhone;
    }

    public void setBankPhone(String bankPhone) {
        this.bankPhone = bankPhone;
    }

    public String getBorrNum() {
        return borrNum;
    }

    public void setBorrNum(String borrNum) {
        this.borrNum = borrNum;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
