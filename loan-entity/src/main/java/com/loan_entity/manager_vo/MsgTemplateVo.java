package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.util.Date;

import com.loan_entity.manager.MsgTemplate;

public class MsgTemplateVo extends MsgTemplate implements Serializable {
	private String employName;

	/**
	 * @return the employName
	 */
	public String getEmployName() {
		return employName;
	}

	/**
	 * @param employName the employName to set
	 */
	public void setEmployName(String employName) {
		this.employName = employName;
	}
}