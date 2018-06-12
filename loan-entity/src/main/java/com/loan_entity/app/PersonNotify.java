package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户消息推送实体
 */
@Entity
@Table(name = "ym_per_notify")
@Getter
@Setter
public class PersonNotify implements Serializable{

    private static final long serialVersionUID = -8816010212658838158L;

    private Integer id;
    private Integer perId;
    private String notifyId;
    private Integer status;
    private Date updateDate;
    private Date creationDate;
}
