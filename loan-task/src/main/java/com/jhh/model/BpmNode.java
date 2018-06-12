package com.jhh.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BpmNode implements Serializable {
    private static final long serialVersionUID = 497724629720343347L;
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