package com.loan_entity.loan;

import java.io.Serializable;
import java.math.BigDecimal;

public class CompanyAccount implements Serializable{
    private Integer id;

    private Integer companyId;

    private String useMoney;

    private String noUseMoney;

    private String withdrawMoney;

    private String collectionMoney;

    private String sumRecharge;

    private String sumWithdraw;

    private String sumCollection;

    private String sumOnlineRecharge;

    private String sumOfflineRecharge;

    private String sumPayment;

    private Integer countPayment;

    private String sync;

    public String getUseMoney() {
        return useMoney;
    }

    public String getNoUseMoney() {
        return noUseMoney;
    }

    public String getWithdrawMoney() {
        return withdrawMoney;
    }

    public String getCollectionMoney() {
        return collectionMoney;
    }

    public String getSumRecharge() {
        return sumRecharge;
    }

    public String getSumWithdraw() {
        return sumWithdraw;
    }

    public String getSumCollection() {
        return sumCollection;
    }

    public String getSumOnlineRecharge() {
        return sumOnlineRecharge;
    }

    public String getSumOfflineRecharge() {
        return sumOfflineRecharge;
    }

    public String getSumPayment() {
        return sumPayment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUseMoney(String useMoney) {
        this.useMoney = useMoney;
    }

    public void setNoUseMoney(String noUseMoney) {
        this.noUseMoney = noUseMoney;
    }

    public void setWithdrawMoney(String withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public void setCollectionMoney(String collectionMoney) {
        this.collectionMoney = collectionMoney;
    }

    public void setSumRecharge(String sumRecharge) {
        this.sumRecharge = sumRecharge;
    }

    public void setSumWithdraw(String sumWithdraw) {
        this.sumWithdraw = sumWithdraw;
    }

    public void setSumCollection(String sumCollection) {
        this.sumCollection = sumCollection;
    }

    public void setSumOnlineRecharge(String sumOnlineRecharge) {
        this.sumOnlineRecharge = sumOnlineRecharge;
    }

    public void setSumOfflineRecharge(String sumOfflineRecharge) {
        this.sumOfflineRecharge = sumOfflineRecharge;
    }

    public void setSumPayment(String sumPayment) {
        this.sumPayment = sumPayment;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getCountPayment() {
        return countPayment;
    }

    public void setCountPayment(Integer countPayment) {
        this.countPayment = countPayment;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }
}