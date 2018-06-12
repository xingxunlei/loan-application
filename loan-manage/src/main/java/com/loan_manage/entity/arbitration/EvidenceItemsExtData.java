package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2018/3/27.
 */

@Getter
@Setter
public class EvidenceItemsExtData implements Serializable {

    /*****借款人名字*****/
    public String borrowerName;
    /*****出借人名字*****/
    public String lenderName;
    /*****服务公司名字*****/
    public String serviceCompany;
    /*****借款合同签订日期*****/
    public String loanDate;
    /*****借款合同本金总额*****/
    public String loanContractMoney;
    /*****借款日利率*****/
    public String dailyRate;
    /*****借款天数*****/
    public String loanDays;
    /*****还款日*****/
    public String repaymentDate;
    /*****平台管理费*****/
    public String serviceFee1;
    /*****快速信审费*****/
    public String serviceFee2;
    /*****服务费优惠*****/
    public String serviceFeeDiscount;
    /*****逾期利息日利率*****/
    public String liquidatedDamages1DailyRate;
    /*****违约金计算标准*****/
    public String liquidatedDamages2Rate;
    /*****借款人送达手机*****/
    public String borrowerServedMobile;
    /*****借款人送达邮箱*****/
    public String borrowerServedEmail;
    /*****借款协议编号*****/
    public String loanContractNo;
    /*****转款日期*****/
    public String transactionTime;
    /*****转款订单号*****/
    public String transactionOderNo;
    /*****转款流水号*****/
    public String transactionSerialNo;
    /*****转款金额*****/
    public String loanMoney;
    /*****收款账户*****/
    public String loanAccount;
    /*****放款账户*****/
    public String lenderAccount;
    /*****还款时间*****/
    public String repayTime;
    /*****转款金额*****/
    public String repayMoney;
    /*****还款账户*****/
    public String repayAccount;
    /*****催收时间*****/
    public String dunningTime;
    /*****催收方式*****/
    public String dunningType;
    /*****去电电话*****/
    public String dunningTocall;
    /*****催收情况*****/
    public String dunningRemark;
    /*****催收人员*****/
    public String dunningPerson;
    /*****授权时间*****/
    public String signPowerofAttorneyDate;

}
