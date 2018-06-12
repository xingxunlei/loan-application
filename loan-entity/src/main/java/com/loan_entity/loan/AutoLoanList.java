package com.loan_entity.loan;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 自动放款
 */
@Setter
@Getter
@ToString
@Table(name = "ym_auto_loan_list")
public class AutoLoanList implements Serializable,Cloneable {

    /**
     * 主键Id
     */
    @JSONField(name = "id")
    private Long id;
    /**
     * 自动放款开关状态
     */
    @JSONField(name = "status")
    private String status;
    /**
     * 数据创建人
     */
    @JSONField(name = "creation_user")
    private String creationUser;
    /**
     * 数据创建时间
     */
    @JSONField(name = "creation_date")
    private Date creationDate;
    /**
     * 自动放款规则JSON
     */
    @JSONField(name = "ruler_json")
    private String rulerJson;
    /**
     * 触发自动放款关闭条件的规则名称
     */
    @JSONField(name = "trigger_ruler_key")
    private String triggerRulerKey;
    /**
     * 触发自动放款关闭条件的规则键值
     */
    @JSONField(name = "trigger_ruler_value")
    private String triggerRulerValue;
    /**
     * 自动放款金额
     */
    @JSONField(name = "loan_amount")
    private Double loanAmount;
    /**
     * 自动放款金额更新时间
     */
    @JSONField(name = "loan_amount_update_date")
    private Date loanAmountUpdateDate;
    /**
     * 数据类型：1 修改规则 2打开开关 3关闭开关
     */
    @JSONField(name = "data_type")
    private Integer dataType;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
