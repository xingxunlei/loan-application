package com.loan_entity.loan_vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class BatchCollectEntity implements Serializable {

    private String guid;
    private String perId;
    private String borrId;
    private String name;
    private String idCardNo;
    private String optAmount;
    private String bankId;
    private String bankInfoId;
    private String bankNum;
    private String phone;
    private String description;
    private String createUser;
    private String collectionUser;
    private String deductionsType;
    /**
     * 判断调用哪个第三方
     */
    private String type;
}
