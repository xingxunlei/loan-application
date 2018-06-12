package com.jhh.model;

import java.io.Serializable;

public class BorrowingExpertsOrder implements Serializable{

	private static final long serialVersionUID = 8933437002232350650L;
	
	private String order_no;
	private String order_time;
	private Double order_amount;
	private Integer order_term;
	private Integer term_unit;
	private Integer order_status;
	private String user_mobile;
	
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public Double getOrder_amount() {
		return order_amount;
	}
	public void setOrder_amount(Double order_amount) {
		this.order_amount = order_amount;
	}
	public Integer getOrder_term() {
		return order_term;
	}
	public void setOrder_term(Integer order_term) {
		this.order_term = order_term;
	}
	public Integer getTerm_unit() {
		return term_unit;
	}
	public void setTerm_unit(Integer term_unit) {
		this.term_unit = term_unit;
	}
	public Integer getOrder_status() {
		return order_status;
	}
	public void setOrder_status(Integer order_status) {
		this.order_status = order_status;
	}
	public String getUser_mobile() {
		return user_mobile;
	}
	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}
}
