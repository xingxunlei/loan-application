package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.loan_entity.manager.RepaymentPlan;

public class RepaymentPlanVo extends RepaymentPlan implements Serializable{
	private String borrNum;

	/**
	 * @return the borrNum
	 */
	public String getBorrNum() {
		return borrNum;
	}

	/**
	 * @param borrNum the borrNum to set
	 */
	public void setBorrNum(String borrNum) {
		this.borrNum = borrNum;
	}
	
   
}