package com.loan_entity.loan;

import java.io.Serializable;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

@ExcelTarget("billingNotice")
public class BillingNotice implements Serializable{
    private static final long serialVersionUID = 2234858511587017462L;
    
    @Excel(name="手机号码", width=30, orderNum="1")
    private String phone;
    @Excel(name="返回结果", width=90, orderNum="2")
    private String result;

    public BillingNotice(){
    	
    }
    
    public BillingNotice(String phone, String result){
    	this.phone = phone;
    	this.result = result;
    }
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
