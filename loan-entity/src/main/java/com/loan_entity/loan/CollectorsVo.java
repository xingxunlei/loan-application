package com.loan_entity.loan;

import java.io.Serializable;

public class CollectorsVo implements Serializable{
    private String contractId;
    private String bedueUserSysno;
    private String bedueName;
    private String updateDate;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getBedueUserSysno() {
        return bedueUserSysno;
    }

    public void setBedueUserSysno(String bedueUserSysno) {
        this.bedueUserSysno = bedueUserSysno;
    }

    public String getBedueName() {
        return bedueName;
    }

    public void setBedueName(String bedueName) {
        this.bedueName = bedueName;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
