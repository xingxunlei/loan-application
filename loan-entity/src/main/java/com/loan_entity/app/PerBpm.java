package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class PerBpm implements Serializable{
    private Integer id;

    private Integer perId;

    private Integer bpmId;

    private String bpmStatus;

    private Date endDate;

    private Date startDate;
}