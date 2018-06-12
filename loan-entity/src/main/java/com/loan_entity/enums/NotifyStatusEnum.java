package com.loan_entity.enums;

/**
 * Created by chenchao on 2017/9/26.
 */
public enum NotifyStatusEnum {
    Register(1, "已注册"),
    Unregister(0, "未注册");

    private int statusCode;
    private String description;

    NotifyStatusEnum(int statusCode,String description){
                this.statusCode = statusCode;
                this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }
}
