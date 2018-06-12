package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ym_per_coupon")
@Getter
@Setter
public class PerCoupon {
    private Integer id;

    private Integer perId;

    private Integer couponId;

    private String couponName;

    private Integer productId;

    private Date startDate;

    private Date endDate;

    private String amount;

    private String status;

    private Date updateDate;

    private Integer updateUser;

    private Date creationDate;

    private Integer creationUser;
}