package com.loan_entity.payment;

import java.io.Serializable;

/**
 * 代收
 */
public class Gather implements Serializable {
    /**
     * guid
     */
    private String guid;
    /**
     * 合同号
     */
    private String borrNum;
    /**
     * 人员ID
     */
    private Integer perId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 操作金额
     */
    private String optAmount;
    /**
     * 银行卡所属银行ID
     */
    private String bankId;

    /**
     * 借款银行ID
     */
    private String bankInfoId;
    /**
     * 将合同ID
     */
    private String borrId;
    /**
     * 扣款银行
     */
    private String bankNum;
    /**
     * 持卡人电话
     */
    private String phone;
    /**
     * 操作说明
     */
    private String description;
    /**
     * 流水号
     */
    private String serNo;
    /**
     * 操作人
     */
    private String createUser;
    /**
     * 催收人
     */
    private String collectionUser;

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

    public String getBorrId() {
        return borrId;
    }

    public void setBorrId(String borrId) {
        this.borrId = borrId;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getPerId() {
        return perId;
    }

    public void setPerId(Integer perId) {
        this.perId = perId;
    }

    public String getCollectionUser() {
        return collectionUser;
    }

    public void setCollectionUser(String collectionUser) {
        this.collectionUser = collectionUser;
    }

    public String getBankInfoId() {
        return bankInfoId;
    }

    public void setBankInfoId(String bankInfoId) {
        this.bankInfoId = bankInfoId;
    }
}
