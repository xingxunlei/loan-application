package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BpmNode implements Serializable {
    private Integer id;

    private Integer perId;

    private Integer bpmId;

    private Integer nodeId;

    private String nodeStatus;

    private Date nodeDate;

    private String description;

    private String sync;

    private Date updateDate;
}