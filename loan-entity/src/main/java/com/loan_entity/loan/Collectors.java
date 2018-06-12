package com.loan_entity.loan;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ds_collectors_level")
@Getter @Setter
public class Collectors implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String guid;
    private String userSysno;
    private String userName;
    private Integer level;
    private Integer productSysno;
    private Integer bedueLevel;
    private Integer userGroupId;
    private Integer levelType;
    private Integer isManage;
    private String status;
    private Date createDate;
    private String createUser;
    private Date updateDate;
    private String updateUser;
}
