package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DsAppReport {
    private Integer id;

    private Integer sysno;

    private String brrownum;

    private String penalty;

    private String penaltyinterest;

    private String issettle;

    private String surplusquota;

    private String surplusmoney;

    private String surplusinterest;

    private String surpluspenalty;

    private String surpluspenaltyinteres;

    private String actrepaydate;

    private String borrstatus;

    private String planrepay;

    private String actrepayamount;

    private Date indate;

    private Integer status;
}