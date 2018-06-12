package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class ProdMode implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String borrId;
	private String prodId;
	private String borrType;
	private String askborrDate;
	private String borrNum;
	private String borrAmount;
	private String termValue;
	private String payDate;
	private String planrepayDate;
	private String actRepayDate;
	private String planRepay;
	private String borrStatus;
	private String borrStatusName;
	private String litterAmout;
	private String managecostAmout;
	private String mationAmout;
	private String couponAmount;
	private String penalty;
	private String penaltyInterest;
	private String maximumAmount;
	
}
