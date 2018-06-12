package com.loan_entity.manager;

import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "ym_repayment_plan")
public class RepaymentPlan implements Serializable{
    private Integer id;

    private String guid;

    private String serialNo;

    private Integer contractId;

    private String contractType;

    private Integer term;

    private String repayDate;

    private Date repayDateTime;

    private BigDecimal monthQuota;

    private BigDecimal monthMoney;

    private BigDecimal monthInterest;

    private BigDecimal penalty;

    private BigDecimal penaltyInterest;

    private Integer isSettle;

    private String surplusQuota;

    private String surplusMoney;

    private String surplusInterest;

    private String surplusPenalty;

    private String surplusPenaltyInteres;

    private Integer extension;

    private Date creationDate;

    private Integer creationUser;

    private Date updateDate;

    private Integer updateUser;

    private String sync;

    private Integer version;

    private String rundate;

    private Integer isRun;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Integer getTerm() {
        return term;
    }

    public void setTerm(Integer term) {
        this.term = term;
    }

    public String getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(String repayDate) throws ParseException {
        this.repayDate = repayDate;
        if(StringUtils.isNotBlank(repayDate)){
            this.repayDateTime = new SimpleDateFormat("yyyy-MM-dd").parse(repayDate);
        }
    }

    public void setRepayDateTime(Date repayDateTime) {
        this.repayDateTime = repayDateTime;
    }

    public BigDecimal getMonthQuota() {
        return monthQuota;
    }

    public void setMonthQuota(BigDecimal monthQuota) {
        this.monthQuota = monthQuota;
    }

    public BigDecimal getMonthMoney() {
        return monthMoney;
    }

    public void setMonthMoney(BigDecimal monthMoney) {
        this.monthMoney = monthMoney;
    }

    public BigDecimal getMonthInterest() {
        return monthInterest;
    }

    public void setMonthInterest(BigDecimal monthInterest) {
        this.monthInterest = monthInterest;
    }

    public BigDecimal getPenalty() {
        return penalty;
    }

    public void setPenalty(BigDecimal penalty) {
        this.penalty = penalty;
    }

    public BigDecimal getPenaltyInterest() {
        return penaltyInterest;
    }

    public void setPenaltyInterest(BigDecimal penaltyInterest) {
        this.penaltyInterest = penaltyInterest;
    }

    public Integer getIsSettle() {
        return isSettle;
    }

    public void setIsSettle(Integer isSettle) {
        this.isSettle = isSettle;
    }


    public String getSurplusQuota() {
		return surplusQuota;
	}

	public void setSurplusQuota(String surplusQuota) {
		this.surplusQuota = surplusQuota;
	}

	public String getSurplusMoney() {
		return surplusMoney;
	}

	public void setSurplusMoney(String surplusMoney) {
		this.surplusMoney = surplusMoney;
	}

	public String getSurplusInterest() {
		return surplusInterest;
	}

	public void setSurplusInterest(String surplusInterest) {
		this.surplusInterest = surplusInterest;
	}

	public String getSurplusPenalty() {
		return surplusPenalty;
	}

	public void setSurplusPenalty(String surplusPenalty) {
		this.surplusPenalty = surplusPenalty;
	}

	public String getSurplusPenaltyInteres() {
		return surplusPenaltyInteres;
	}

	public void setSurplusPenaltyInteres(String surplusPenaltyInteres) {
		this.surplusPenaltyInteres = surplusPenaltyInteres;
	}

	public Integer getExtension() {
        return extension;
    }

    public void setExtension(Integer extension) {
        this.extension = extension;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(Integer creationUser) {
        this.creationUser = creationUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRundate() {
        return rundate;
    }

    public void setRundate(String rundate) {
        this.rundate = rundate;
    }

    public Integer getIsRun() {
        return isRun;
    }

    public void setIsRun(Integer isRun) {
        this.isRun = isRun;
    }

    public Date getRepayDateTime() {
        return repayDateTime;
    }
}