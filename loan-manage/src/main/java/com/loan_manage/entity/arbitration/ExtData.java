package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2018/3/2
 */

@Getter
@Setter
public class ExtData extends ClaimItemsExtData implements Serializable {

    /*****借款人名字*****/
    public String borrowerName;
    /*****出借人名字*****/
    public String lenderName;
    /*****服务公司名字*****/
    public String serviceCompany;
    /*****借款协议签订日期*****/
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
    /*****还款总额*****/
    public String repayMoneyTotal;
    /*****服务费总额*****/
    public String serviceFeeTotal;
    /*****逾期利息日利率*****/
    public String liquidatedDamages1DailyRate;
    /*****违约金计算标准*****/
    public String liquidatedDamages2Rate;
    /*****利息总额*****/
    public String accrual;
    /*****还款总额*****/
    public String repaymentMoneyTotal;
    /*****打款时间*****/
    public String payDate;
    /*****注册日期*****/
    public String registrationDate;
    /*****违约金*****/
    public String penalty;
    /*****违约金*****/
    public String repayMoney;

    public static ExtData getExtData(ExtData extData){
        ExtData extData1 = new ExtData();
        extData1.setBorrowerName(extData.getBorrowerName());
        extData1.setLenderName(extData.getLenderName());
        extData1.setServiceCompany(extData.getServiceCompany());
        extData1.setLoanDate(extData.getLoanDate());
        extData1.setLoanContractMoney(extData.getLoanContractMoney());
        extData1.setDailyRate(extData.getDailyRate());
        extData1.setLoanDays(extData.getLoanDays());
        extData1.setRepaymentDate(extData.getRepaymentDate());
        extData1.setServiceFee1(extData.getServiceFee1());
        extData1.setServiceFee2(extData.getServiceFee2());
        extData1.setServiceFeeDiscount(extData.getServiceFeeDiscount());
        extData1.setRepayMoneyTotal(extData.getRepayMoneyTotal());
        extData1.setServiceFeeTotal(Double.toString(Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount())));
        extData1.setLiquidatedDamages1DailyRate(extData.getLiquidatedDamages1DailyRate());
        extData1.setLiquidatedDamages2Rate(extData.getLiquidatedDamages2Rate());
        extData1.setAccrual(extData.getAccrual());
        return extData1;
    }
}
