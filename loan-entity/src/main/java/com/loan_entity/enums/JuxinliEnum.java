package com.loan_entity.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Created by xuepengfei on 2017/10/19.
 */
public enum JuxinliEnum {

    JXL_SUCCESS("0000","认证成功"),
    JXL_REFUSE("1000","认证失败"),
    JXL_ERROR("2000","异常，展示异常信息"),
    JXL_MENUAL("8888","人工审核"),
    JXL_VERIFY("10002","提交短信验证码"),
    JXL_CXXD_VERIFY("10003","提示用户发送CXXD至....."),
    JXL_REQUEST_VERIFY("10004","用户点击重发验证码按钮"),
    JXL_REPEAT_VERIFY("10001","重复提交短信验证码"),
    JXL_COLLECTING("10008","已受理采集"),
    JXL_QUERYPWD("10022","提交查询密码");

    JuxinliEnum(String code,String describe){
        this.code = code;
        this.describe = describe;
    }

    private String code;

    private String describe;

    public String getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }


}
