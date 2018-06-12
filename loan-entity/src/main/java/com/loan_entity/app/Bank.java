package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ym_bank")
@Getter @Setter
public class Bank implements Serializable{
    @Id
    private Integer id;

    private Integer perId;

    private String bankId;

    private String bankNum;

    private String phone;

    private String status;

    private Date startDate;

    private Date endDate;

    private String resultCode;

    private String resultMsg;

    private String subContractNum;

    private Date creationDate;

    private Integer creationUser;

    private Date updateDate;

    private Integer updateUser;

    @Transient
    private String name;

}