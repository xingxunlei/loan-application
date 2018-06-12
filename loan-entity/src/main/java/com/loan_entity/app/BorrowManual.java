package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ym_borrow_manual")
@Getter
@Setter
public class BorrowManual implements Serializable{
    private Integer id;

    private Integer borrId;

    private String description;

    private Integer contactNum;

    private Integer isManual;

    private Date creationDate;

    public BorrowManual(Integer borrId, String description, Integer contactNum, Integer isManual, Date creationDate) {
        this.borrId = borrId;
        this.description = description;
        this.contactNum = contactNum;
        this.isManual = isManual;
        this.creationDate = creationDate;
    }

    public BorrowManual(){

    }
}