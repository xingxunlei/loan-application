package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="ym_person")
@Getter
@Setter
public class Person implements Serializable{
	private static final long serialVersionUID = 734164289531433453L;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    private String guid;

    private String username;

    private String password;

    private String phone;

    private Date checkDate;

    private String blacklist;

    private String phoneBusiness;

    private String phoneService;

    private Integer bpmId;

    private Integer grade;

    private Integer isLogin;

    private String tokenId;

    private String source;

    private Date createDate;

    private Date updateDate;

    private String sync;
    @Transient
    private String oldPassword;
    private int inviter;
    
    private String isManual;
    
    private String description;
}
