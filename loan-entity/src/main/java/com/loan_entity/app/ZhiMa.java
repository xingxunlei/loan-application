package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ZhiMa {
    private Integer id;

    private Integer perId;

    private String zmScore;

    private String description;

    private String creationUser;

    private Date creationDate;

    private Date updateDate;
}