package com.loan_entity.app_vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ProductApp implements Serializable {
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

    private Integer day;

    private Integer money;
}