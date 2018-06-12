package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter @Setter
public class BankVo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
    private Integer bankId;
    private String bankNum;
    private String bankName;
    
    private Integer status;
    private String phone;
    private Date createDate;

}
