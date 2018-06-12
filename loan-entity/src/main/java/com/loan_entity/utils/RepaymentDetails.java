package com.loan_entity.utils;

import java.io.Serializable;

public class RepaymentDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String bankId;
	
	private String bankLocalId;
	
	private String per_id;
	
	private String cardNo;
	
	private String borr_id;
	
	private String plan_repay;
	
	private String act_repay_amount;
	
	private String minimum;
	
	private String bankName;
	
	private String counterFee;
	
	private String couAmount;
	
	private String alsoAmount;
	

	public String getAlsoAmount() {
		return alsoAmount;
	}

	public void setAlsoAmount(String alsoAmount) {
		this.alsoAmount = alsoAmount;
	}

	public String getCouAmount() {
		return couAmount;
	}

	public void setCouAmount(String couAmount) {
		this.couAmount = couAmount;
	}

	public String getBankLocalId() {
		return bankLocalId;
	}

	public void setBankLocalId(String bankLocalId) {
		this.bankLocalId = bankLocalId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getPer_id() {
		return per_id;
	}

	public void setPer_id(String per_id) {
		this.per_id = per_id;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getBorr_id() {
		return borr_id;
	}

	public void setBorr_id(String borr_id) {
		this.borr_id = borr_id;
	}

	public String getPlan_repay() {
		return plan_repay;
	}

	public void setPlan_repay(String plan_repay) {
		this.plan_repay = plan_repay;
	}

	public String getAct_repay_amount() {
		return act_repay_amount;
	}

	public void setAct_repay_amount(String act_repay_amount) {
		this.act_repay_amount = act_repay_amount;
	}

	public String getMinimum() {
		return minimum;
	}

	public void setMinimum(String minimum) {
		this.minimum = minimum;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCounterFee() {
		return counterFee;
	}

	public void setCounterFee(String counterFee) {
		this.counterFee = counterFee;
	}

	
	
	

}
