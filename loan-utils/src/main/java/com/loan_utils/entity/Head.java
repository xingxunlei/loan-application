package com.loan_utils.entity;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class Head implements Serializable {
   
    private String Command;

    private String ReqTime;

    private String Guid;

    private String User;

    private String PassWord;

    private String RespCode;

    private String RespMessage;

    private String Md5;

    private String Lang;

    public void setCommand(String Command){
        this.Command = Command;
    }
    @JSONField(name="Command")
    public String getCommand(){
        return this.Command;
    }
    public void setReqTime(String ReqTime){
        this.ReqTime = ReqTime;
    }
    @JSONField(name="ReqTime")
    public String getReqTime(){
        return this.ReqTime;
    }
    public void setGuid(String Guid){
        this.Guid = Guid;
    }
    @JSONField(name="Guid")
    public String getGuid(){
        return this.Guid;
    }
    public void setUser(String User){
        this.User = User;
    }
    @JSONField(name="User")
    public String getUser(){
        return this.User;
    }
    public void setPassWord(String PassWord){
        this.PassWord = PassWord;
    }
    @JSONField(name="PassWord")
    public String getPassWord(){
        return this.PassWord;
    }
    public void setRespCode(String RespCode){
        this.RespCode = RespCode;
    }
    @JSONField(name="RespCode")
    public String getRespCode(){
        return this.RespCode;
    }
    public void setRespMessage(String RespMessage){
        this.RespMessage = RespMessage;
    }
    @JSONField(name="RespMessage")
    public String getRespMessage(){
        return this.RespMessage;
    }
    public void setMd5(String Md5){
        this.Md5 = Md5;
    }
    @JSONField(name="Md5")
    public String getMd5(){
        return this.Md5;
    }
    public void setLang(String Lang){
        this.Lang = Lang;
    }
    @JSONField(name="Lang")
    public String getLang(){
        return this.Lang;
    }
}
