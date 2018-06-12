package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 
*描述：
*@author: Wanyan
*@date： 日期：2016年11月10日 时间：下午9:28:15
*@version 1.0
 */

public class LoanInfoVo  implements Serializable {
	private int id;
   private String product_name;
   private String maximum_amount;
   private String loan_day;
   private String repay_date;
   private String meaning;
   private String borr_num;
   private Date makeborrDate;
   private String amount;
   private String description;
   private String isManual;
   private String bedueDays;

	public String getBedueDays() {
		return bedueDays;
	}

	public void setBedueDays(String bedueDays) {
		this.bedueDays = bedueDays;
	}

	public Date getMakeborrDate() {
		return makeborrDate;
	}

	public void setMakeborrDate(Date makeborrDate) {
		this.makeborrDate = makeborrDate;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
/**
 * @return the borr_num
 */
public String getBorr_num() {
	return borr_num;
}
/**
 * @param borr_num the borr_num to set
 */
public void setBorr_num(String borr_num) {
	this.borr_num = borr_num;
}
/**
 * @return the product_name
 */
public String getProduct_name() {
	return product_name;
}
/**
 * @param product_name the product_name to set
 */
public void setProduct_name(String product_name) {
	this.product_name = product_name;
}
/**
 * @return the maximum_amount
 */
public String getMaximum_amount() {
	return maximum_amount;
}
/**
 * @param maximum_amount the maximum_amount to set
 */
public void setMaximum_amount(String maximum_amount) {
	this.maximum_amount = maximum_amount;
}
/**
 * @return the loan_day
 */
public String getLoan_day() {
	return loan_day;
}
/**
 * @param loan_day the loan_day to set
 */
public void setLoan_day(String loan_day) {
	this.loan_day = loan_day;
}
/**
 * @return the repay_date
 */
public String getRepay_date() {
	return repay_date;
}
/**
 * @param repay_date the repay_date to set
 */
public void setRepay_date(String repay_date) {
	this.repay_date = repay_date;
}
/**
 * @return the meaning
 */
public String getMeaning() {
	return meaning;
}
/**
 * @param meaning the meaning to set
 */
public void setMeaning(String meaning) {
	this.meaning = meaning;
}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIsManual() {
		return isManual;
	}

	public void setIsManual(String isManual) {
		this.isManual = isManual;
	}
}
