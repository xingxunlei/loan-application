package com.loan_entity.loan;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ds_collectors_level_back")
public class CollectorsLevelBack implements Serializable {
    private Integer id;
    private String guid;
    private String userSysno;
    private String userName;
    private Integer level;
    private Integer productSysno;
    private Integer bedueLevel;
    private Integer userGroupId;
    private String status;
    private String createUser;
    private Date createDate;
    private String updateUser;
    private Date updateDate;
    private Integer dclType;

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

    public String getUserSysno() {
        return userSysno;
    }

    public void setUserSysno(String userSysno) {
        this.userSysno = userSysno;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getProductSysno() {
        return productSysno;
    }

    public void setProductSysno(Integer productSysno) {
        this.productSysno = productSysno;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public Integer getDclType() {
        return dclType;
    }

    public void setDclType(Integer dclType) {
        this.dclType = dclType;
    }
}
