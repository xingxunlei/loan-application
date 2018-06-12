package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "ym_card")
@Getter
@Setter
public class Card {
    private Integer id;

    private Integer perId;

    private String cardNum;

    private String name;

    private String sex;

    private String nation;

    private Date birthday;

    private String address;

    private String office;

    private Date startDate;

    private Date endDate;

    private String cardPhotoz;

    private String cardPhotof;

    private String cardPhotod;

    private String cardPhotov;

    private Date updateDate;

    private String sync;

}