package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
@Getter
@Setter
public class Contact implements Serializable{
    private Integer id;

    private Integer perId;

    private String contactName;

    private String contactNum;

    private Date updateDate;

    private String sync;
}