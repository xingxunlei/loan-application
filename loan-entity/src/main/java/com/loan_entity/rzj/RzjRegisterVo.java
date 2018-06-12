package com.loan_entity.rzj;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 *  融之家注册参数
 */
@Setter
@Getter
@ToString
public class RzjRegisterVo implements Serializable{

    private static final long serialVersionUID = 3802249278242594378L;
    /**
     * 借点钱对于当前申请的唯一标识
     */
    private String apply_no;
    /**
     * 借点钱渠道标识,由机构定义
     */
    private String channel_no;
    /**
     * 申请信息
     */
    private String apply_info;
    /**
     *  用户信息
     */
    private String user_attribute;
    /**
     * 申请时刻的UNIX时间戳（毫秒级）
     */
    private String timestamp;
    /**
     * 请求签名
     */
    private String sign;
}
