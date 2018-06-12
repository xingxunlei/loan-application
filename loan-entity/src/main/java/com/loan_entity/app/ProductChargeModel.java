package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class ProductChargeModel implements Serializable{
    private Integer id;

    private String guid;

    private Integer productId;

    private Integer productTermId;

    private String chargeName;

    private String calcBasis;

    private String chargeRule;

    private String term;

    private BigDecimal amount;

    private BigDecimal rate;

    private Integer priority;

    private String remark;

    private String status;

    private Date creationDate;

    private Integer creationUser;

    private Date updateDate;

    private Integer updateUser;

}