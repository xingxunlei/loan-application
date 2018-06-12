package com.loan_entity.manager;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ym_code_value") @Setter @Getter
public class CodeValue implements Serializable{
    private Integer id;

    private String codeType;

    private String codeCode;

    private String meaning;

    private String description;

    private String enabledFlag;

    private String sync;
}