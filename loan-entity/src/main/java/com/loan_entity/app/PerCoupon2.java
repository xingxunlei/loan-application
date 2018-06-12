package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PerCoupon2 extends PerCoupon implements Serializable{
	
	private String productName;
	
	private String startDateString;
	
	private String endDateString;

}
