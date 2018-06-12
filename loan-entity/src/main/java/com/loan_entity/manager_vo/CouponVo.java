package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.util.Date;

import com.loan_entity.manager.Coupon;

/**
*描述：
*@author: Wanyan
*@date： 日期：2016年12月8日 时间：上午10:34:27
*@version 1.0
*/
public class CouponVo extends Coupon implements Serializable{

	private String employName;


    private String productName;


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


	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}


	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}


}