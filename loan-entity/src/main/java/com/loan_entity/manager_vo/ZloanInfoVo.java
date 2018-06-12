package com.loan_entity.manager_vo;

import com.loan_entity.enums.BorrowStatusEnum;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Create by Jxl on 2017/9/12
 */
public class ZloanInfoVo implements Serializable {
    /**
     *  借款ID
     */
    private int id ;
    /**
     *  借款编号
     */
    private String borrNum;
    /**
     *  产品类型
     */
    private String productName;
    /**
     *  合同状态
     */
    private String borrStatus;
    /**
     *  申请时间
     */
    private Date askborrDate;
    /**
     *  合同签约日
     */
    private Date makeborrDate;
    /**
     *  放款成功日
     */
    private Date payDate;
    /**
     *  放款金额
     */
    private String maximumAmount;
    /**
     *  约定还款日
     */
    private Date planrepayDate;
    /**
     *  应还款总额
     */
    private String planRepay;
    /**
     *  实际结清日
     */
    private Date actRepayDate;
    /**
     *  实际还款总额
     */
    private String actRepayAmount;
    /**
     * 借款状态描述
     */
    private String borrStatusStr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBorrNum() {
        return borrNum;
    }

    public void setBorrNum(String borrNum) {
        this.borrNum = borrNum;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBorrStatus() {
        return borrStatus;
    }

    public void setBorrStatus(String borrStatus) {
        this.borrStatus = borrStatus;
        if(StringUtils.isNotBlank(borrStatus)){
            this.borrStatusStr = BorrowStatusEnum.getDescByCode(borrStatus);
        }
    }

    public Date getAskborrDate() {
        return askborrDate;
    }

    public void setAskborrDate(Date askborrDate) {
        this.askborrDate = askborrDate;
    }

    public Date getMakeborrDate() {
        return makeborrDate;
    }

    public void setMakeborrDate(Date makeborrDate) {
        this.makeborrDate = makeborrDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(String maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public Date getPlanrepayDate() {
        return planrepayDate;
    }

    public void setPlanrepayDate(Date planrepayDate) {
        this.planrepayDate = planrepayDate;
    }

    public String getPlanRepay() {
        return planRepay;
    }

    public void setPlanRepay(String planRepay) {
        this.planRepay = planRepay;
    }

    public Date getActRepayDate() {
        return actRepayDate;
    }

    public void setActRepayDate(Date actRepayDate) {
        this.actRepayDate = actRepayDate;
    }

    public String getActRepayAmount() {
        return actRepayAmount;
    }

    public void setActRepayAmount(String actRepayAmount) {
        this.actRepayAmount = actRepayAmount;
    }

    public String getBorrStatusStr() {
        return borrStatusStr;
    }

    public void setBorrStatusStr(String borrStatusStr) {
        this.borrStatusStr = borrStatusStr;
    }
}
