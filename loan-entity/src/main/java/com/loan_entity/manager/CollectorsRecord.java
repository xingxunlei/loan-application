package com.loan_entity.manager;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ds_collectors_record")
public class CollectorsRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String contractId;
    private String bedueUser;
    private Date createTime;
    private String createUser;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getBedueUser() {
        return bedueUser;
    }

    public void setBedueUser(String bedueUser) {
        this.bedueUser = bedueUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
