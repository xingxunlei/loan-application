package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class ProductTerm implements Serializable{
    private Integer id;

    private String guid;

    private Integer productId;

    private String temName;

    private Integer termValue;

    private BigDecimal maximumAmount;

    private BigDecimal maximumInterestRate;

    private BigDecimal minimumInterestRate;

    private BigDecimal monthlyRate;

    private Short status;

    private Date creationDate;

    private Integer creationUser;

    private Date updateDate;

    private Integer updateUser;

}