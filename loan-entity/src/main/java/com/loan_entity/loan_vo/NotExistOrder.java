package com.loan_entity.loan_vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by xuepengfei on 2017/11/10.
 */
@Getter @Setter
public class NotExistOrder implements Serializable {

    private String serialNo;
    private int times;
    private int type;
}
