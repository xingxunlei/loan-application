package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by wanzezhong on 2018/3/27.
 */

@Getter
@Setter
public class ClaimItems implements Serializable {

    /*****第XXX项仲裁请求*****/
    public String materialCode;
    /*****给付之诉*****/
    public String claimType;
    /*****标的*****/
    public String disputeFee;
    /*****0*****/
    public String payItem;
    /*****申请人*****/
    public String litigants;
    /*****仲裁请求内容*****/
    public String claimContent;
    /*****extData*****/
    public ExtData extData;

    private static final String calimContent1 = "被申请人向申请人一支付未还本金及利息共计{suplusTotal}元（借款本金{loanMoney}元，已还{repayTotal}元，自{payDate}起至{repaymentDate}止共{loanDays}天，按日利率{dailyRate}%计算，利息为{interest}元）。";
    private static final String calimContent2 = "被申请人向申请人一支付逾期利息及逾期违约金（逾期利息暂计算自{overdueDate}起至{now}止共{overdueDays}天，并计算至实际付清之日止，以{loanMoney}元为基数，按日利率{overdueDailyRate}%计算，逾期利息为{overdueInterest}元；逾期违约金以{loanMoney}为基数按10%计算，逾期违约金为{liquidatedDamages}元），可以{loanMoney}元为基数，自{overdueDate}至实际偿还之日，按年化24%计算。";
    private static final String calimContent3 = "被申请人向申请人二支付平台管理费及快速信审费{serviceFeeTotal}元（平台管理费{serviceFeeManage}元，快速信审费{serviceFeeTrust}元，优惠{serviceFeeDiscount}元）。";
    private static final String calimContent4 = "被申请人承担本案仲裁费。";

    public static ClaimItems getClaimItems1(ExtData extData){
        Double disputeFee = Double.valueOf(extData.getPrinciple()) + Double.valueOf(extData.getAccrual());
        ClaimItems claimItems = new ClaimItems();
        claimItems.setMaterialCode("第（一）项仲裁请求");
        claimItems.setClaimType("给付之诉");
        claimItems.setClaimContent(calimContent1.replaceAll("\\{suplusTotal}", String.format("%.2f", Float.parseFloat(extData.getLoanContractMoney()) - Float.parseFloat(extData.getRepayMoneyTotal()) + Float.parseFloat(extData.getAccrual()))).replaceAll("\\{loanMoney}", extData.getLoanContractMoney()).replaceAll("\\{repayTotal}", extData.getRepayMoneyTotal()).replaceAll("\\{payDate}", extData.getPayDate()).replaceAll("\\{repaymentDate}", extData.getRepaymentDate()).replaceAll("\\{loanDays}", extData.getLoanDays()).replaceAll("\\{dailyRate}", extData.getDailyRate()).replaceAll("\\{interest}", extData.getAccrual()));
        claimItems.setDisputeFee(String.format("%.2f",disputeFee));
        claimItems.setPayItem("0");
        claimItems.setLitigants("方伟");
        ExtData extData1 = new ExtData();
        extData1.setLoanContractMoney(extData.getLoanContractMoney());
        extData1.setPrinciple(extData.getPrinciple());
        extData1.setLoanDate(extData.getPayDate());
        extData1.setDailyRate(extData.getDailyRate());
        extData1.setLoanDays(extData.getLoanDays());
        extData1.setRepaymentDate(extData.getRepaymentDate());
        extData1.setAccrual(extData.getAccrual());
        extData1.setRepaymentMoneyTotal(extData.getRepayMoneyTotal());
        claimItems.setExtData(extData1);
        return  claimItems;
    }

    public static ClaimItems getClaimItems2(ExtData extData){
        Double disputeFee = Double.valueOf(extData.getLiquidatedDamages2()) + Double.valueOf(extData.getLiquidatedDamages1());
        ClaimItems claimItems = new ClaimItems();
        claimItems.setMaterialCode("第（二）项仲裁请求");
        claimItems.setClaimContent(calimContent2.replaceAll("\\{overdueDate}", LocalDate.parse(extData.getRepaymentDate()).plusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).replaceAll("\\{now}", extData.getAccountingDate()).replaceAll("\\{overdueDays}", extData.getAccountingDays()).replaceAll("\\{loanMoney}", extData.getLoanContractMoney()).replaceAll("\\{overdueDailyRate}", extData.getLiquidatedDamages1DailyRate()).replaceAll("\\{overdueInterest}", extData.getLiquidatedDamages1()).replaceAll("\\{liquidatedDamagesRate}", String.format("%.3f", Double.parseDouble(extData.getLiquidatedDamages2Rate()))).replaceAll("\\{liquidatedDamages}", extData.getPenalty()));
        claimItems.setClaimType("给付之诉");
        claimItems.setDisputeFee(String.format("%.2f",disputeFee));
        claimItems.setPayItem("0");
        claimItems.setLitigants("方伟");
        ExtData extData1 = new ExtData();
        extData1.setLoanContractMoney(extData.getLoanContractMoney());
        extData1.setPrinciple(extData.getPrinciple());
        extData1.setAccountingDate(extData.getAccountingDate());
        extData1.setRepaymentDate(extData.getRepaymentDate());
        extData1.setAccountingDays(extData.getAccountingDays());
        extData1.setLiquidatedDamages1(extData.getLiquidatedDamages1());
        extData1.setLiquidatedDamages1DailyRate(extData.getLiquidatedDamages1DailyRate());
        extData1.setLiquidatedDamages2(extData.getLiquidatedDamages2());
        extData1.setLiquidatedDamages2Rate(extData.getLiquidatedDamages2Rate());
        extData1.setRepayMoney(extData.getRepayMoneyTotal());
        claimItems.setExtData(extData1);
        return  claimItems;
    }

    public static ClaimItems getClaimItems3(ExtData extData){
        ClaimItems claimItems = new ClaimItems();
        claimItems.setMaterialCode("第（三）项仲裁请求");
        claimItems.setClaimContent(calimContent3.replaceAll("\\{serviceFeeTotal}", String.format("%.2f", Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount()))).replaceAll("\\{serviceFeeManage}", extData.getServiceFee1()).replaceAll("\\{serviceFeeTrust}", extData.getServiceFee2()).replaceAll("\\{serviceFeeDiscount}", extData.getServiceFeeDiscount()));
        claimItems.setClaimType("给付之诉");
        claimItems.setPayItem("0");
        claimItems.setDisputeFee(Double.toString(Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount())));
        claimItems.setLitigants("方伟");
        ExtData extData1 = new ExtData();
        extData1.setServiceFee1(extData.getServiceFee1());
        extData1.setServiceFee2(extData.getServiceFee2());
        extData1.setServiceFeeDiscount(extData.getServiceFeeDiscount());
        extData1.setServiceFeeTotal(Double.toString(Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount())));
        claimItems.setExtData(extData1);
        return  claimItems;
    }

    public static ClaimItems getClaimItems4(){
        ClaimItems claimItems = new ClaimItems();
        claimItems.setMaterialCode("第（四）项仲裁请求");
        claimItems.setClaimContent(calimContent4);
        claimItems.setClaimType("仲裁费");
        claimItems.setDisputeFee("0");
        claimItems.setPayItem("0");
        claimItems.setLitigants("方伟");
        return  claimItems;
    }
}


