package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ym_product")
@Getter
@Setter
public class Product implements Serializable{
    private Integer id;

    private String guid;

    private String productcode;

    private String productName;

    private Integer productTypeId;

    private String repaymentMethod;

    private String repaymentSequence;

    private String remark;

    private Integer userGroupid;

    private String status;

    private Date creationDate;

    private Integer creationUser;

    private Date updateDate;

    private Integer updateUser;

    @Transient
    private String temName;

}