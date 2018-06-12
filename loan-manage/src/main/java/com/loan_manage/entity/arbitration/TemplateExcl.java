package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2018/3/28.
 */
@Setter @Getter
public class TemplateExcl implements Serializable {
    @Excel(name="借款单号（或合同号）")
    private String borrNum;

    @Excel(name="借款本金（元）")
    private String money;

    @Excel(name="利息")
    private String interest;

    @Excel(name="还款日")
    private String planDate;

    @Excel(name="服务费")
    private String serviceCharge;

    @Excel(name="逾期开始日期")
    private String lateStartDate;

    @Excel(name="逾期违约金（元）")
    private String lateDamages;

    @Excel(name="请求标的金额（元）")
    private String borrowMoney;

    @Excel(name="请求事项")
    private String requestItems;

    @Excel(name="简单描述事实和理由")
    private String reasons;

    @Excel(name="仲裁申请书文件名")
    private String arbitrationForm ;

    @Excel(name="仲裁法院名称")
    private String arbitrationCourt;

    @Excel(name="业务编号")
    private String businessNum;


    public static TemplateExcl assembleData(){
        TemplateExcl templateExcl = new TemplateExcl();
        templateExcl.setBorrNum("被申请人性质");
        templateExcl.setMoney("姓名/名称");
        templateExcl.setInterest("证件类别");
        templateExcl.setPlanDate("证件号码");
        templateExcl.setServiceCharge("法定代表人");
        templateExcl.setLateStartDate("职务");
        templateExcl.setLateDamages("性别");
        templateExcl.setBorrowMoney("证件地址");
        templateExcl.setRequestItems("手机号码");
        templateExcl.setReasons("电子邮箱");
        templateExcl.setArbitrationForm("联系地址");
        templateExcl.setArbitrationCourt("被申请人身份证正面");
        templateExcl.setBusinessNum("被申请人身份证反面");
        return  templateExcl;
    }

    public static TemplateExcl assembleData(String name, String text){
        TemplateExcl templateExcl = new TemplateExcl();
        templateExcl.setBorrNum(name);
        templateExcl.setMoney(text);
        return templateExcl;
    }
}
