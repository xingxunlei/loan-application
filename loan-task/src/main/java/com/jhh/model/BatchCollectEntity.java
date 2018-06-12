package com.jhh.model;

import java.io.Serializable;

public class BatchCollectEntity implements Serializable {

    private String guid;
    private String perId;
    private String borrId;
    private String name;
    private String idCardNo;
    private String optAmount;
    private String bankId;
    private String bankNum;
    private String phone;
    private String description;
    private String createUser;
    private String collectionUser;
    private String deductionsType;

    public String getDeductionsType() {
        return deductionsType;
    }

    public void setDeductionsType(String deductionsType) {
        this.deductionsType = deductionsType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCollectionUser() {
        return collectionUser;
    }

    public void setCollectionUser(String collectionUser) {
        this.collectionUser = collectionUser;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPerId() {
        return perId;
    }

    public void setPerId(String perId) {
        this.perId = perId;
    }

    public String getBorrId() {
        return borrId;
    }

    public void setBorrId(String borrId) {
        this.borrId = borrId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
