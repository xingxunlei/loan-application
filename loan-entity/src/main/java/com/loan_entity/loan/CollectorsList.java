package com.loan_entity.loan;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ds_collectors_list")
public class CollectorsList implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String guid;
    private String contractSysno;
    private String contractId;
    private String contractType;
    private String bedueUserSysno;
    private String bedueName;
    private String acquMode;
    private Date obtainTime;
    private Integer operator;
    private Integer bedueLevel;
    private Integer userGroupId;
    private String status;
    private String createUser;
    private Date createDate;
    private String updateUser;
    private Date updateDate;

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

    public String getContractSysno() {
        return contractSysno;
    }

    public void setContractSysno(String contractSysno) {
        this.contractSysno = contractSysno;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
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

    public String getAcquMode() {
        return acquMode;
    }

    public void setAcquMode(String acquMode) {
        this.acquMode = acquMode;
    }

    public Date getObtainTime() {
        return obtainTime;
    }

    public void setObtainTime(Date obtainTime) {
        this.obtainTime = obtainTime;
    }

    public Integer getOperator() {
        return operator;
    }

    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    public Integer getBedueLevel() {
        return bedueLevel;
    }

    public void setBedueLevel(Integer bedueLevel) {
        this.bedueLevel = bedueLevel;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
