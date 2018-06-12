package com.loan_entity.lakala.query;


import com.loan_entity.lakala.LakalaCrossPaySuperResponse;

public class CustomAuthQueryResponse extends LakalaCrossPaySuperResponse{

	private static final long serialVersionUID = 1627663082608774833L;
	private String orderNo;
	private String payOrderId;
	public String getPayOrderId() {
		return payOrderId;
	}
	public void setPayOrderId(String payOrderId) {
		this.payOrderId = payOrderId;
	}
	private String status;
	
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
