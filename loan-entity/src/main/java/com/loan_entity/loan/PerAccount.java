package com.loan_entity.loan;

import java.io.Serializable;
import java.math.BigDecimal;

public class PerAccount implements Serializable{
    private Integer id;

    private String guid;

    private Integer perId;

    private BigDecimal useMoney;

    private BigDecimal noUseMoney;

    private BigDecimal withdrawMoney;

    private BigDecimal sumCollection;

    private BigDecimal sumRecharge;

    private BigDecimal sumOfflineRecharge;

    private BigDecimal sumWithdraw;

    private BigDecimal paymentMoney;

    private BigDecimal sumRepayAmount;

    private Integer countBorrow;

    private String sync;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getPerId() {
        return perId;
    }

    public void setPerId(Integer perId) {
        this.perId = perId;
    }

    public BigDecimal getUseMoney() {
        return useMoney;
    }

    public void setUseMoney(BigDecimal useMoney) {
        this.useMoney = useMoney;
    }

    public BigDecimal getNoUseMoney() {
        return noUseMoney;
    }

    public void setNoUseMoney(BigDecimal noUseMoney) {
        this.noUseMoney = noUseMoney;
    }

    public BigDecimal getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(BigDecimal withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public BigDecimal getSumCollection() {
        return sumCollection;
    }

    public void setSumCollection(BigDecimal sumCollection) {
        this.sumCollection = sumCollection;
    }

    public BigDecimal getSumRecharge() {
        return sumRecharge;
    }

    public void setSumRecharge(BigDecimal sumRecharge) {
        this.sumRecharge = sumRecharge;
    }

    public BigDecimal getSumOfflineRecharge() {
        return sumOfflineRecharge;
    }

    public void setSumOfflineRecharge(BigDecimal sumOfflineRecharge) {
        this.sumOfflineRecharge = sumOfflineRecharge;
    }

    public BigDecimal getSumWithdraw() {
        return sumWithdraw;
    }

    public void setSumWithdraw(BigDecimal sumWithdraw) {
        this.sumWithdraw = sumWithdraw;
    }

    public BigDecimal getPaymentMoney() {
        return paymentMoney;
    }

    public void setPaymentMoney(BigDecimal paymentMoney) {
        this.paymentMoney = paymentMoney;
    }

    public BigDecimal getSumRepayAmount() {
        return sumRepayAmount;
    }

    public void setSumRepayAmount(BigDecimal sumRepayAmount) {
        this.sumRepayAmount = sumRepayAmount;
    }

    public Integer getCountBorrow() {
        return countBorrow;
    }

    public void setCountBorrow(Integer countBorrow) {
        this.countBorrow = countBorrow;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }
}