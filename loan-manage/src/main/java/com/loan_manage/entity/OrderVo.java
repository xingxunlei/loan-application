package com.loan_manage.entity;

import com.loan_entity.manager.BankList;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class OrderVo {
    private String borrNum;
    private String serialNo;
    private String name;
    private String idCard;
    private String phone;
    private String bankNum;
    private String type;
    private String rlState;
    private String actAmount;
    private String createDate;
    private String createUser;
    private String collectionUser;
    private String collectors;
    private String reason;
    private String borrId;
    private String prodId;
    private String id;
    private String bankId;
    private String perId;
    private String bedueDays;
    private String deductionsType;
    private String bankName;
    private List<BankList> banks; //银行列表
    private String blackList;

}
