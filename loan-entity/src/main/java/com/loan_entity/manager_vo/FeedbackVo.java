package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.util.Date;

import com.loan_entity.manager.Feedback;

public class FeedbackVo extends Feedback implements Serializable{
	private String phone;

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
  
}