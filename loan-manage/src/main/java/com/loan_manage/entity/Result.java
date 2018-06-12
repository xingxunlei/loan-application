package com.loan_manage.entity;

import java.io.Serializable;

/**
 * 通用返回实体类
 */
public class Result implements Serializable {
    public final static int FAIL = 0;//请求成功
    public final static int S = 10;//银生宝回调成功
    public final static int F = 20;//银生宝回调失败
    public final static int P = 30;//处理中
    public final static int SUCCESS = 1;//请求失败
    private int code;//成功失败代码
    private String message;//成功失败消息
    private Object object;//返回数据

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
