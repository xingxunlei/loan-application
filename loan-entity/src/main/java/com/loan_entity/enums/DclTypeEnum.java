package com.loan_entity.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Create by Jxl on 2017/9/15
 */
public enum DclTypeEnum {

    Inner("1", "贷后催收"),
    Ooutter("2", "外包催收"),
    RiskControl("3","风控催收"),
    CustomerService("5","客服催收");

    DclTypeEnum(String code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public static String getDescByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (DclTypeEnum statusEnum : DclTypeEnum.values()) {
            if (StringUtils.equals(statusEnum.code, code)) {
                return statusEnum.describe;
            }
        }
        return null;
    }


    private String code;

    private String describe;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
