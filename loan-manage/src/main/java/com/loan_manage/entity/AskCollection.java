package com.loan_manage.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * 扣款信息
 */
public class AskCollection implements Serializable{
    private String guid;
    private String borrNum;
    private String name;
    private String idCardNo;
    private String optAmount;
    private String bankId;
    private String borrId;
    private String bankNum;
    private String phone;
    private String description;
    private String serNo;
    private String createUser;
    private String bankInfoId;
    private Integer perId;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public String getBorrId() {
        return borrId;
    }

    public void setBorrId(String borrId) {
        this.borrId = borrId;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getOptAmount() {
        return optAmount;
    }

    public void setOptAmount(String optAmount) {
        this.optAmount = optAmount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
            this.description = description;
    }

    public String getSerNo() {
        return serNo;
    }

    public void setSerNo(String serNo) {
        this.serNo = serNo;
    }

    public String getBankInfoId() {
        return bankInfoId;
    }

    public void setBankInfoId(String bankInfoId) {
        this.bankInfoId = bankInfoId;
    }

    public Integer getPerId() {
        return perId;
    }

    public void setPerId(Integer perId) {
        this.perId = perId;
    }
}
