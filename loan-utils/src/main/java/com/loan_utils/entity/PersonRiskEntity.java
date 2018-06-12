package com.loan_utils.entity;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 个人认证内部风控接口请求实体类
 * @author xuepengfei
 *2016年10月14日下午2:39:47
 */
public class PersonRiskEntity implements Serializable {

    private static final long serialVersionUID = 1L;
   
    private Head Head;

    private Body Body;

    public void setHead(Head Head){
        this.Head = Head;
    }
    @JSONField(name="Head")
    public Head getHead(){
        return this.Head;
    }
    public void setBody(Body Body){
        this.Body = Body;
    }
    @JSONField(name="Body")
    public Body getBody(){
        return this.Body;
    }
       
}


