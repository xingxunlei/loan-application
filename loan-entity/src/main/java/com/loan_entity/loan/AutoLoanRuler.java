package com.loan_entity.loan;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 自动放款规则数据
 */
@Setter
@Getter
@ToString
@Table(name = "ym_auto_loan_ruler")
public class AutoLoanRuler implements Serializable {

    /** 规则属性名称，主键 */
    @Id
    private String property;

    /** 规则值 */
    private String value;

    /** 规则描述 */
    private String description;

    /** 是否开启规则 */
    private String enabledFlag;

    /** 规则更新时间 */
    private Date  updateDate;



}
