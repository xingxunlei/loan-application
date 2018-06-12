package com.loan_entity.app_vo;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;


public class PhoneContent implements Serializable {

    private String Name;
    private String IdValue;
    private String CellPhone;
    private String ServerPsw;
    private String VerifiCode;
    private String Address;
    private String CorpName;
    private String CorpAddress;
    private String CorpTel;
    private String QueryPwd;   
    private String ID;
    private List<Object> list;



    @JSONField(name="QueryPwd")
    public String getQueryPwd() {
        return QueryPwd;
    }
    public void setQueryPwd(String queryPwd) {
        QueryPwd = queryPwd;
    }
    @JSONField(name="List")
    public List<Object> getList() {
        return list;
    }
    public void setList(List<Object> list) {
        this.list = list;
    }
    @JSONField(name="Name")
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    @JSONField(name="IdValue")
    public String getIdValue() {
        return IdValue;
    }
    public void setIdValue(String idValue) {
        IdValue = idValue;
    }
    @JSONField(name="CellPhone")
    public String getCellPhone() {
        return CellPhone;
    }
    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }
    @JSONField(name="ServerPsw")
    public String getServerPsw() {
        return ServerPsw;
    }
    public void setServerPsw(String serverPsw) {
        ServerPsw = serverPsw;
    }
    @JSONField(name="VerifiCode")
    public String getVerifiCode() {
        return VerifiCode;
    }
    public void setVerifiCode(String verifiCode) {
        VerifiCode = verifiCode;
    }
    @JSONField(name="Address")
    public String getAddress() {
        return Address;
    }
    public void setAddress(String address) {
        Address = address;
    }
    @JSONField(name="CorpName")
    public String getCorpName() {
        return CorpName;
    }
    public void setCorpName(String corpName) {
        CorpName = corpName;
    }
    @JSONField(name="CorpAddress")
    public String getCorpAddress() {
        return CorpAddress;
    }
    public void setCorpAddress(String corpAddress) {
        CorpAddress = corpAddress;
    }
    @JSONField(name="CorpTel")
    public String getCorpTel() {
        return CorpTel;
    }
    public void setCorpTel(String corpTel) {
        CorpTel = corpTel;
    }
    @JSONField(name="ID")
    public String getID() {
        return ID;
    }
    public void setID(String iD) {
        ID = iD;
    }
    
    
}
