package com.loan_manage.entity;

import com.loan_entity.enums.BorrowStatusEnum;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * 外包导出视图
 * Create by Jxl on 2017/9/15
 */
public class LoansRemarkOutVo implements Serializable{

    private static final String  PLACHOLDER_4 = "****";
    private static final String  PLACHOLDER_6 = "******";

    @Excel(name="姓名")
    private String customerName;
    @Excel(name="身份证",width = 25)
    private String customerIdValue;
    @Excel(name="手机号码",width = 15)
    private String customerMobile;
    @Excel(name = "催记内容",width = 30)
    private String remark;
    @Excel(name="当前状态")
    private String stateString;
    @Excel(name="创建时间")
    private String createTime;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerIdValue() {
        return customerIdValue;
    }

    //对外部权限身份证后六位替换为*
    public void setCustomerIdValue(String customerIdValue) {
        if(StringUtils.isNotBlank(customerIdValue)){
            customerIdValue=customerIdValue.substring(0,customerIdValue.length()-6);
            customerIdValue += PLACHOLDER_6;
        }
        this.customerIdValue = customerIdValue;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    //对外部权限手机号后四位替换为*
    public void setCustomerMobile(String customerMobile) {
        if(StringUtils.isNotBlank(customerMobile)){
            customerMobile = customerMobile.substring(0,customerMobile.length() - 4);
            customerMobile += PLACHOLDER_4;
        }
        this.customerMobile = customerMobile;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStateString() {
        return stateString;
    }

    public void setStateString(String stateString) {
        String desc = BorrowStatusEnum.getDescByCode(stateString);
        if(StringUtils.isNotBlank(desc)){
            this.stateString = desc;
        }else{
            this.stateString = stateString;
        }
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
