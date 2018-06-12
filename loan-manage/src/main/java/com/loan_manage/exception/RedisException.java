package com.loan_manage.exception;

public class RedisException extends RuntimeException {

    private String retCd ;  //异常对应的返回码
    private String msgDes;  //异常对应的描述信息

    public RedisException(){
        super();
    }

    public RedisException(String message) {
        super(message);
        msgDes = message;
    }
}
