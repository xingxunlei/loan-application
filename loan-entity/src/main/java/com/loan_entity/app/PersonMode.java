package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
public class PersonMode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String perId;
	private String cardNum;
	private String name;
	private String phone;
	private String profession;
	private String monthlypay;
	private String relatives;
	private String relativesName;
	private String relaPhone;
	private String society;
	private String societyName;
	private String sociPhone;
	private String bankName;
	private String bankNum;
	private String bankId;
	private String bankLocalId;

}
