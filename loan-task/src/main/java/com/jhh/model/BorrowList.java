package com.jhh.model;

import java.io.Serializable;
import java.util.Date;

public class BorrowList implements Serializable {
	
	private static final long serialVersionUID = 7331740624532982085L;

	private Integer id;

    private Integer perId;

    private Integer prodId;

    private String borrType;

    private Date askborrDate;

    private String borrNum;

    private Date makeborrDate;

    private Date payDate;

    private Date planrepayDate;

    private Date actRepayDate;

    private String borrStatus;

    private String planRepay;

    private String actRepayAmount;

    private Integer ispay;

    private Integer termId;

    private String borrAmount;

    private String sync;

    private Date updateDate;

    private Integer updateUser;

    private Date creationDate;

    private Integer creationUser;
    
    private String termValue;
    
    private String maximumAmount;
    
    private String borrStatusName;
    
    private String repayDate;
    
    private String perCouponId;
    
    public String getPerCouponId() {
        return perCouponId;
    }

    public void setPerCouponId(String perCouponId) {
        this.perCouponId = perCouponId;
    }
    
    

    public String getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(String repayDate) {
		this.repayDate = repayDate;
	}

	public String getBorrStatusName() {
		return borrStatusName;
	}

	public void setBorrStatusName(String borrStatusName) {
		this.borrStatusName = borrStatusName;
	}

	public String getTermValue() {
		return termValue;
	}

	public void setTermValue(String termValue) {
		this.termValue = termValue;
	}

	public String getMaximumAmount() {
		return maximumAmount;
	}

	public void setMaximumAmount(String maximumAmount) {
		this.maximumAmount = maximumAmount;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPerId() {
        return perId;
    }

    public void setPerId(Integer perId) {
        this.perId = perId;
    }

    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    public String getBorrType() {
        return borrType;
    }

    public void setBorrType(String borrType) {
        this.borrType = borrType;
    }

    public Date getAskborrDate() {
        return askborrDate;
    }

    public void setAskborrDate(Date askborrDate) {
        this.askborrDate = askborrDate;
    }

    public String getBorrNum() {
        return borrNum;
    }

    public void setBorrNum(String borrNum) {
        this.borrNum = borrNum;
    }

    public Date getMakeborrDate() {
        return makeborrDate;
    }

    public void setMakeborrDate(Date makeborrDate) {
        this.makeborrDate = makeborrDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Date getPlanrepayDate() {
        return planrepayDate;
    }

    public void setPlanrepayDate(Date planrepayDate) {
        this.planrepayDate = planrepayDate;
    }

    public Date getActRepayDate() {
        return actRepayDate;
    }

    public void setActRepayDate(Date actRepayDate) {
        this.actRepayDate = actRepayDate;
    }

    public String getBorrStatus() {
        return borrStatus;
    }

    public void setBorrStatus(String borrStatus) {
        this.borrStatus = borrStatus;
    }

   

    public String getPlanRepay() {
        return planRepay;
    }

    public void setPlanRepay(String planRepay) {
        this.planRepay = planRepay;
    }

    public String getActRepayAmount() {
        return actRepayAmount;
    }

    public void setActRepayAmount(String actRepayAmount) {
        this.actRepayAmount = actRepayAmount;
    }

    public Integer getIspay() {
        return ispay;
    }

    public void setIspay(Integer ispay) {
        this.ispay = ispay;
    }

    public Integer getTermId() {
        return termId;
    }

    public void setTermId(Integer termId) {
        this.termId = termId;
    }

    public String getBorrAmount() {
        return borrAmount;
    }

    public void setBorrAmount(String borrAmount) {
        this.borrAmount = borrAmount;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
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
}