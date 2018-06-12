package com.loan_entity.manager;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity @Table(name = "ym_msg") @Getter @Setter
public class Msg implements Serializable{
    private Integer id;

    private String title;

    private Integer perId;

    private Integer type;

    private String status;

    private Date createTime;

    private String sync;

    private String content;
    
    private String create_time;
}