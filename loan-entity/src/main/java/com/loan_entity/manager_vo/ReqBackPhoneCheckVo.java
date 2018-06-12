package com.loan_entity.manager_vo;

import java.io.Serializable;

import com.loan_entity.app.BpmNode;
import com.loan_entity.manager.Order;

public class ReqBackPhoneCheckVo  implements Serializable{
	
	 private String phone;
	 private String node_status ;
	 private String node_date ;
	 private String description;
	 private String requestId;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

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
	/**
	 * @return the node_status
	 */
	public String getNode_status() {
		return node_status;
	}
	/**
	 * @param node_status the node_status to set
	 */
	public void setNode_status(String node_status) {
		this.node_status = node_status;
	}
	/**
	 * @return the node_date
	 */
	public String getNode_date() {
		return node_date;
	}
	/**
	 * @param node_date the node_date to set
	 */
	public void setNode_date(String node_date) {
		this.node_date = node_date;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	 
}