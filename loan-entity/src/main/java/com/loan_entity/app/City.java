package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class City implements Serializable{
    private Integer id;

    private String name;

    private String pinyin;

    private Integer pid;

}