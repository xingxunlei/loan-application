package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestMessage {
    private Integer id;

    private Integer perId;

    private String request;

    private String response;

    private String description;

    private Date creationDate;
}