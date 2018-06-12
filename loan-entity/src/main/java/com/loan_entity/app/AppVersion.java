package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class AppVersion implements Serializable{
    private Integer id;

    private String name;

    private Integer code;

    private String version;

    private String forcedUpdate;

    private Date creationTime;
}