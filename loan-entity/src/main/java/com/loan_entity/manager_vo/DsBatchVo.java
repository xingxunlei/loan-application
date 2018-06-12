/**
 *描述：
 *@author: wanyan
 *@date： 日期：2016年11月24日 时间：下午4:31:21
 *@version 1.0
 */

package com.loan_entity.manager_vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述：批量处理结果实体
 *
 * @author: Wanyan
 * @date： 日期：2016年11月24日 时间：下午4:31:21
 * @version 1.0
 */
public class DsBatchVo implements Serializable{
	private String brrowNum; // 合同编号

	private Integer isSettle; // 0否1是，完颜说没有意思，出问题找

	private BigDecimal penalty; // 当期所有违约金

	private BigDecimal penaltyInterest; // 当期所有罚息

	private String surplusQuota; // 剩余本息

	private String surplusMoney; // 剩余本金

	private String surplusInterest; // 剩余利息

	private String surplusPenalty; // 剩余违约金

	private String surplusPenaltyInteres; // 剩余罚息

	
	private Date actRepayDate; // 实际结清日

	private String borrStatus; // 合同状态

	private String planRepay; // 应还款总额

	private String actRepayAmount; // 实际还款总额

	/**
	 * @return the brrowNum
	 */
	public String getBrrowNum() {
		return brrowNum;
	}

	/**
	 * @param brrowNum the brrowNum to set
	 */
	public void setBrrowNum(String brrowNum) {
		this.brrowNum = brrowNum;
	}

	/**
	 * @return the isSettle
	 */
	public Integer getIsSettle() {
		return isSettle;
	}

	/**
	 * @param isSettle the isSettle to set
	 */
	public void setIsSettle(Integer isSettle) {
		this.isSettle = isSettle;
	}

	/**
	 * @return the penalty
	 */
	public BigDecimal getPenalty() {
		return penalty;
	}

	/**
	 * @param penalty the penalty to set
	 */
	public void setPenalty(BigDecimal penalty) {
		this.penalty = penalty;
	}

	/**
	 * @return the penaltyInterest
	 */
	public BigDecimal getPenaltyInterest() {
		return penaltyInterest;
	}

	/**
	 * @param penaltyInterest the penaltyInterest to set
	 */
	public void setPenaltyInterest(BigDecimal penaltyInterest) {
		this.penaltyInterest = penaltyInterest;
	}

	/**
	 * @return the surplusQuota
	 */
	public String getSurplusQuota() {
		return surplusQuota;
	}

	/**
	 * @param surplusQuota the surplusQuota to set
	 */
	public void setSurplusQuota(String surplusQuota) {
		this.surplusQuota = surplusQuota;
	}

	/**
	 * @return the surplusMoney
	 */
	public String getSurplusMoney() {
		return surplusMoney;
	}

	/**
	 * @param surplusMoney the surplusMoney to set
	 */
	public void setSurplusMoney(String surplusMoney) {
		this.surplusMoney = surplusMoney;
	}

	/**
	 * @return the surplusInterest
	 */
	public String getSurplusInterest() {
		return surplusInterest;
	}

	/**
	 * @param surplusInterest the surplusInterest to set
	 */
	public void setSurplusInterest(String surplusInterest) {
		this.surplusInterest = surplusInterest;
	}

	/**
	 * @return the surplusPenalty
	 */
	public String getSurplusPenalty() {
		return surplusPenalty;
	}

	/**
	 * @param surplusPenalty the surplusPenalty to set
	 */
	public void setSurplusPenalty(String surplusPenalty) {
		this.surplusPenalty = surplusPenalty;
	}

	/**
	 * @return the surplusPenaltyInteres
	 */
	public String getSurplusPenaltyInteres() {
		return surplusPenaltyInteres;
	}

	/**
	 * @param surplusPenaltyInteres the surplusPenaltyInteres to set
	 */
	public void setSurplusPenaltyInteres(String surplusPenaltyInteres) {
		this.surplusPenaltyInteres = surplusPenaltyInteres;
	}

	/**
	 * @return the actRepayDate
	 */
	public Date getActRepayDate() {
		return actRepayDate;
	}

	/**
	 * @param actRepayDate the actRepayDate to set
	 */
	public void setActRepayDate(Date actRepayDate) {
		this.actRepayDate = actRepayDate;
	}

	/**
	 * @return the borrStatus
	 */
	public String getBorrStatus() {
		return borrStatus;
	}

	/**
	 * @param borrStatus the borrStatus to set
	 */
	public void setBorrStatus(String borrStatus) {
		this.borrStatus = borrStatus;
	}

	/**
	 * @return the planRepay
	 */
	public String getPlanRepay() {
		return planRepay;
	}

	/**
	 * @param planRepay the planRepay to set
	 */
	public void setPlanRepay(String planRepay) {
		this.planRepay = planRepay;
	}

	/**
	 * @return the actRepayAmount
	 */
	public String getActRepayAmount() {
		return actRepayAmount;
	}

	/**
	 * @param actRepayAmount the actRepayAmount to set
	 */
	public void setActRepayAmount(String actRepayAmount) {
		this.actRepayAmount = actRepayAmount;
	}
	
}
