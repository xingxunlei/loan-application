package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class Riewer implements Serializable{
    private Integer id;

    private String employNum;

    private String emplloyeeName;

    private String status;

    private Date creationDate;

    private Date updateDate;

}