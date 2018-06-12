package com.loan_entity.loan;

import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

@ExcelTarget("BadData")
public class BadData {
	@Excel(name = "id", width = 30, orderNum = "1")
	private Integer id;
	@Excel(name = "sysno", width = 30, orderNum = "2")
	private Integer sysno;
	@Excel(name = "brrownum", width = 30, orderNum = "3")
	private String brrownum;
	@Excel(name = "penalty", width = 30, orderNum = "4")
	private String penalty;
	@Excel(name = "penaltyinterest", width = 30, orderNum = "5")
	private String penaltyinterest;

	@Excel(name = "issettle", width = 30, orderNum = "6")
	private String issettle;
	@Excel(name = "surplusquota", width = 30, orderNum = "7")
	private String surplusquota;
	@Excel(name = "surplusmoney", width = 30, orderNum = "8")
	private String surplusmoney;
	@Excel(name = "surplusinterest", width = 30, orderNum = "9")
	private String surplusinterest;
	@Excel(name = "surpluspenalty", width = 30, orderNum = "10")
	private String surpluspenalty;
	@Excel(name = "surpluspenaltyinteres", width = 30, orderNum = "11")
	private String surpluspenaltyinteres;
	@Excel(name = "actrepaydate", width = 30, orderNum = "12")
	private String actrepaydate;
	@Excel(name = "borrstatus", width = 30, orderNum = "13")
	private String borrstatus;
	@Excel(name = "planrepay", width = 30, orderNum = "14")
	private String planrepay;
	@Excel(name = "actrepayamount", width = 30, orderNum = "15")
	private String actrepayamount;
	@Excel(name = "indate", width = 30, orderNum = "16")
	private Date indate;
	@Excel(name = "status", width = 30, orderNum = "17")
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSysno() {
		return sysno;
	}

	public void setSysno(Integer sysno) {
		this.sysno = sysno;
	}

	public String getBrrownum() {
		return brrownum;
	}

	public void setBrrownum(String brrownum) {
		this.brrownum = brrownum;
	}

	public String getPenalty() {
		return penalty;
	}

	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}

	public String getPenaltyinterest() {
		return penaltyinterest;
	}

	public void setPenaltyinterest(String penaltyinterest) {
		this.penaltyinterest = penaltyinterest;
	}

	public String getIssettle() {
		return issettle;
	}

	public void setIssettle(String issettle) {
		this.issettle = issettle;
	}

	public String getSurplusquota() {
		return surplusquota;
	}

	public void setSurplusquota(String surplusquota) {
		this.surplusquota = surplusquota;
	}

	public String getSurplusmoney() {
		return surplusmoney;
	}

	public void setSurplusmoney(String surplusmoney) {
		this.surplusmoney = surplusmoney;
	}

	public String getSurplusinterest() {
		return surplusinterest;
	}

	public void setSurplusinterest(String surplusinterest) {
		this.surplusinterest = surplusinterest;
	}

	public String getSurpluspenalty() {
		return surpluspenalty;
	}

	public void setSurpluspenalty(String surpluspenalty) {
		this.surpluspenalty = surpluspenalty;
	}

	public String getSurpluspenaltyinteres() {
		return surpluspenaltyinteres;
	}

	public void setSurpluspenaltyinteres(String surpluspenaltyinteres) {
		this.surpluspenaltyinteres = surpluspenaltyinteres;
	}

	public String getActrepaydate() {
		return actrepaydate;
	}

	public void setActrepaydate(String actrepaydate) {
		this.actrepaydate = actrepaydate;
	}

	public String getBorrstatus() {
		return borrstatus;
	}

	public void setBorrstatus(String borrstatus) {
		this.borrstatus = borrstatus;
	}

	public String getPlanrepay() {
		return planrepay;
	}

	public void setPlanrepay(String planrepay) {
		this.planrepay = planrepay;
	}

	public String getActrepayamount() {
		return actrepayamount;
	}

	public void setActrepayamount(String actrepayamount) {
		this.actrepayamount = actrepayamount;
	}

	public Date getIndate() {
		return indate;
	}

	public void setIndate(Date indate) {
		this.indate = indate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
