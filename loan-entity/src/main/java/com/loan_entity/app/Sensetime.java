package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class Sensetime implements Serializable{
    private Integer id;

    private Integer perId;

    private String type;

    private Integer count;

    private Date createTime;

}