package com.loan_entity.manager;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity @Table(name = "ym_code_type") @Getter @Setter
public class CodeType implements Serializable{
    private Integer id;

    private String codeType;

    private String meaning;

    private String description;

    private String sync;
}