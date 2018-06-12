package com.loan_entity.loan;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ReceiptUsers implements Serializable {
    private String id;
    private String userSysno;
    private String userName;
    private String productSysno;
    private String bedueLevel;
    private String status;
    private String levelType;
    private String isManage;
}
