package com.loan_entity.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Create by Jxl on 2017/9/12
 */
public enum NodeStatusEnum {

    WAIT_IDENTIFICATION("NS001","未认证"),
    DONE_IDENTIFICATION("NS002","已认证"),
    FAIL_IDENTIFICATION("NS003","认证失败"),
    PROCESS_IDENTIFICATION("NS004","已提交"),
    FAIL_BLACK_IDENTIFICATION("NS005","认证失败，且进黑名单");

    NodeStatusEnum(String code,String describe){
        this.code = code;
        this.describe = describe;
    }

    public static String getDescByCode(String code){
        if(StringUtils.isBlank(code)){
            return null;
        }
        for (NodeStatusEnum statusEnum : NodeStatusEnum.values()) {
            if(StringUtils.equals(statusEnum.code,code)){
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
