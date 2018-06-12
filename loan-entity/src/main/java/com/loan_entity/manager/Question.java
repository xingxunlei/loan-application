package com.loan_entity.manager;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity @Table(name = "ym_question") @Getter @Setter
public class Question implements Serializable{
    private Integer id;

    private String questionType;

    private String questionText;

    private String employNum;

    private Date createTime;
    private Date updateDate;
    
    private String status;

    private String sync;

    private String answer;
}