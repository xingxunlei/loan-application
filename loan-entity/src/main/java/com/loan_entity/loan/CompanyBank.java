package com.loan_entity.loan;

import java.io.Serializable;
import java.util.Date;

public class CompanyBank implements Serializable{
    private Integer id;

    private Integer companyId;

    private String bankName;

    private String bankId;

    private String cardNo;

    private Integer cardCity;

    private Integer cardProvince;

    private String cardType;

    private Date addtime;

    private String addip;

    private Integer deleted;

    private String deleteReason;

    private Integer sysUserId;

    private String deleteIp;

    private String sync;

    private byte[] cardZhihang;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getCardCity() {
        return cardCity;
    }

    public void setCardCity(Integer cardCity) {
        this.cardCity = cardCity;
    }

    public Integer getCardProvince() {
        return cardProvince;
    }

    public void setCardProvince(Integer cardProvince) {
        this.cardProvince = cardProvince;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public String getAddip() {
        return addip;
    }

    public void setAddip(String addip) {
        this.addip = addip;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    public String getDeleteIp() {
        return deleteIp;
    }

    public void setDeleteIp(String deleteIp) {
        this.deleteIp = deleteIp;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public byte[] getCardZhihang() {
        return cardZhihang;
    }

    public void setCardZhihang(byte[] cardZhihang) {
        this.cardZhihang = cardZhihang;
    }
}