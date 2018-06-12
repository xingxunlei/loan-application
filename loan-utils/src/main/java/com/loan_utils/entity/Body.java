package com.loan_utils.entity;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class Body implements Serializable {
    
    
    private Object Content;
    
    private List<Object> ContactList;
    
    @JSONField(name="ContactList")
    public List<Object> getContactList() {
        return ContactList;
    }
    public void setContactList(List<Object> contactList) {
        ContactList = contactList;
    }

    public void setContent(Object Content){
        this.Content = Content;
    }
    @JSONField(name="Content")
    public Object getContent(){
        return this.Content;
    }
}
