package com.jhh.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 *  申请贷款-放款是否通过
 */
@Setter
@Getter
@ToString
public class ApplyLoanVo implements Serializable{

    private static final long serialVersionUID = 5308808824870345756L;
    /**
     *  用户的手机号码
     */
    private String phone;

    /**
     *  跟贝壳钱包合作时约定 API 调用时的 key 值
     */
    private String key;

    /**
     *  申请贷款的时间
     */
    private String apply_time;

    /**
     *  申请贷款金额，单位为分
     */
    private String apply_money;

    /**
     *  申请贷款周期，单位为天
     */
    private String apply_period;

    /**
     *  审核结果 0：通过（通过时放款时间跟放款金额不能为空）；1：拒绝
     */
    private String verify_status;

    /**
     *  放款时间
     */
    private String loan_time;

    /**
     *  放款金额，单位为分
     */
    private String loan_money;

    /**
     *  订单号
     */
    private String order_id;

}
