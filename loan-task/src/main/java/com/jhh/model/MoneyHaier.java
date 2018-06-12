package com.jhh.model;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2017/9/22.
 */
@ExcelTarget("moneyHaier")
public class MoneyHaier implements Serializable{
    @Excel(name="订单号", orderNum="1")
    private Integer orderCode ;
    @Excel(name="发生时间", orderNum="2")
    private String payDate;
    @Excel(name="收入金额", orderNum="3")
    private String money;

    public Integer getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(Integer orderCode) {
        this.orderCode = orderCode;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
