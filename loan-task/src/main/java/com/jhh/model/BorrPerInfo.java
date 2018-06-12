package com.jhh.model;

import java.io.Serializable;

public class BorrPerInfo implements Serializable {
	
	private String borrId;
	private String name;
	private double maximum_amount;
	private int term_value;
	private String planrepay_date;
	private double monthly_rate;
	private String phone;
	private String surplus_quota;
	private String surplus_penalty;
	private String surplus_penalty_Interes;
	
	public String getSurplus_penalty() {
		return surplus_penalty;
	}
	public void setSurplus_penalty(String surplus_penalty) {
		this.surplus_penalty = surplus_penalty;
	}
	public String getSurplus_penalty_Interes() {
		return surplus_penalty_Interes;
	}
	public void setSurplus_penalty_Interes(String surplus_penalty_Interes) {
		this.surplus_penalty_Interes = surplus_penalty_Interes;
	}
	public String getSurplus_quota() {
		return surplus_quota;
	}
	public void setSurplus_quota(String surplus_quota) {
		this.surplus_quota = surplus_quota;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public double getMonthly_rate() {
		return monthly_rate;
	}
	public void setMonthly_rate(double monthly_rate) {
		this.monthly_rate = monthly_rate;
	}
	public String getBorrId() {
		return borrId;
	}
	public void setBorrId(String borrId) {
		this.borrId = borrId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMaximum_amount() {
		return maximum_amount;
	}
	public void setMaximum_amount(double maximum_amount) {
		this.maximum_amount = maximum_amount;
	}
	public int getTerm_value() {
		return term_value;
	}
	public void setTerm_value(int term_value) {
		this.term_value = term_value;
	}
	public String getPlanrepay_date() {
		return planrepay_date;
	}
	public void setPlanrepay_date(String planrepay_date) {
		this.planrepay_date = planrepay_date;
	}
	
	
}
