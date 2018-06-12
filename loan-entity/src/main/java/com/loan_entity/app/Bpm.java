package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Bpm implements Serializable{
    private Integer id;

    private String nodeName;

    private Integer parentNodeid;

    private String sync;

}