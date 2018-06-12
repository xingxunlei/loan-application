package com.loan_entity.app_vo;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class RiskContent implements Serializable {

    private String Name;

    private String IdValue;

    private String CellPhone;

    private String EMail;

    private String QQ;

    private String Address;

    private String CorpName;

    private String CorpAddress;

    private String CorpTel;

    private String Remark;

    private String Level;

    public void setName(String Name){
        this.Name = Name;
    }
    @JSONField(name="Name")
    public String getName(){
        return this.Name;
    }
    public void setIdValue(String IdValue){
        this.IdValue = IdValue;
    }
    @JSONField(name="IdValue")
    public String getIdValue(){
        return this.IdValue;
    }
    public void setCellPhone(String CellPhone){
        this.CellPhone = CellPhone;
    }
    @JSONField(name="CellPhone")
    public String getCellPhone(){
        return this.CellPhone;
    }
    public void setEMail(String EMail){
        this.EMail = EMail;
    }
    @JSONField(name="EMail")
    public String getEMail(){
        return this.EMail;
    }
    public void setQQ(String QQ){
        this.QQ = QQ;
    }
    @JSONField(name="QQ")
    public String getQQ(){
        return this.QQ;
    }
    public void setAddress(String Address){
        this.Address = Address;
    }
    @JSONField(name="Address")
    public String getAddress(){
        return this.Address;
    }
    public void setCorpName(String CorpName){
        this.CorpName = CorpName;
    }
    @JSONField(name="CorpName")
    public String getCorpName(){
        return this.CorpName;
    }
   
    public void setCorpAddress(String CorpAddress){
        this.CorpAddress = CorpAddress;
    }
    @JSONField(name="CorpAddress")
    public String getCorpAddress(){
        return this.CorpAddress;
    }
    
    public void setCorpTel(String CorpTel){
        this.CorpTel = CorpTel;
    }
    @JSONField(name="CorpTel")
    public String getCorpTel(){
        return this.CorpTel;
    }
    public void setRemark(String Remark){
        this.Remark = Remark;
    }
    @JSONField(name="Remark")
    public String getRemark(){
        return this.Remark;
    }
    
    public void setLevel(String Level){
        this.Level = Level;
    }
    @JSONField(name="Level")
    public String getLevel(){
        return this.Level;
    }
}
