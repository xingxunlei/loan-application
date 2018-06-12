package com.loan_entity.shell;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 *  贝壳钱包 事件-注册
 */
@Setter
@Getter
@ToString
public class RegisterVo implements Serializable{

    private static final long serialVersionUID = -5941326475261067644L;
    /**
       注册用户的手机号码
     */
    private String phone;

    /**
     跟贝壳钱包合作时约定 API 调用时的 key 值
     */
    private String key;

    /**
      注册时间
     */
    private String register_time;
}
