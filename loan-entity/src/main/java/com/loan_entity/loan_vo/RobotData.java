package com.loan_entity.loan_vo;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

/**
 * Created by xuepengfei on 2017/8/2.
 */
@ExcelTarget("RobotData")
public class RobotData {
    @Excel(name = "案件编号", width = 30, orderNum = "1")
    private String borrNum;
    @Excel(name = "手机号", width = 20, orderNum = "2")
    private String phone;
    @Excel(name = "借款日期", width = 20, orderNum = "3")
    private String payDate;
    @Excel(name = "借款金额", width = 20, orderNum = "4")
    private Integer money;
    @Excel(name = "逾期日期", width = 20, orderNum = "5")
    private String planRepayDate;
    @Excel(name = "滞纳金金额", width = 20, orderNum = "6")
    private String interest;
    @Excel(name = "本息金额", width = 20, orderNum = "7")
    private Integer total;
    @Excel(name = "提醒结果", width = 20, orderNum = "8")
    private String remind;

    public String getBorrNum() {
        return borrNum;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public Integer getMoney() {
        return money;
    }

    public Integer getTotal() {
        return total;
    }

    public String getPlanRepayDate() {
        return planRepayDate;
    }

    public void setPlanRepayDate(String planRepayDate) {
        this.planRepayDate = planRepayDate;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public void setBorrNum(String borrNum) {
        this.borrNum = borrNum;

    }
}
